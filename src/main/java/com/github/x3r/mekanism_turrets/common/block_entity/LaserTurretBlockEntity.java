package com.github.x3r.mekanism_turrets.common.block_entity;

import com.github.x3r.mekanism_turrets.MekanismTurrets;
import mekanism.api.IContentsListener;
import mekanism.api.RelativeSide;
import mekanism.api.providers.IBlockProvider;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.capabilities.holder.energy.EnergyContainerHelper;
import mekanism.common.capabilities.holder.energy.IEnergyContainerHolder;
import mekanism.common.capabilities.holder.slot.IInventorySlotHolder;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.lib.frequency.FrequencyType;
import mekanism.common.lib.security.SecurityFrequency;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.util.NBTUtils;
import mekanism.common.util.SecurityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.network.SerializableDataTicket;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public class LaserTurretBlockEntity extends TileEntityMekanism implements GeoBlockEntity {

    @WrappingComputerMethod(wrapper = SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.class, methodNames = "getEnergyItem", docPlaceholder = "energy slot")
    EnergyInventorySlot energySlot;

    public static final SerializableDataTicket<Boolean> HAS_TARGET = GeckoLibUtil.addDataTicket(SerializableDataTicket.ofBoolean(new ResourceLocation(MekanismTurrets.MOD_ID, "has_target")));
    public static final SerializableDataTicket<Double> TARGET_POS_X = GeckoLibUtil.addDataTicket(SerializableDataTicket.ofDouble(new ResourceLocation(MekanismTurrets.MOD_ID, "target_pos_x")));
    public static final SerializableDataTicket<Double> TARGET_POS_Y = GeckoLibUtil.addDataTicket(SerializableDataTicket.ofDouble(new ResourceLocation(MekanismTurrets.MOD_ID, "target_pos_y")));
    public static final SerializableDataTicket<Double> TARGET_POS_Z = GeckoLibUtil.addDataTicket(SerializableDataTicket.ofDouble(new ResourceLocation(MekanismTurrets.MOD_ID, "target_pos_z")));
    private static final float MAX_RANGE = 10;
    private final AABB targetBox = AABB.ofSize(getBlockPos().getCenter(), MAX_RANGE, MAX_RANGE, MAX_RANGE);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private LaserTurretTier tier;
    private MachineEnergyContainer<LaserTurretBlockEntity> energyContainer;
    private boolean targetsHostile = false;
    private boolean targetsPassive = false;
    private boolean targetsPlayers = false;
    private boolean targetsTrusted = true;
    private @Nullable LivingEntity target;


    public LaserTurretBlockEntity(IBlockProvider blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state);
    }

    @Override
    protected @Nullable IEnergyContainerHolder getInitialEnergyContainers(IContentsListener listener) {
        EnergyContainerHelper builder = EnergyContainerHelper.forSide(this::getDirection);
        builder.addContainer(energyContainer = MachineEnergyContainer.input(this, listener));
        return builder.build();
    }

    public MachineEnergyContainer<LaserTurretBlockEntity> getEnergyContainer() {
        return energyContainer;
    }

    @Override
    protected @Nullable IInventorySlotHolder getInitialInventory(IContentsListener listener) {
        InventorySlotHelper builder = InventorySlotHelper.forSide(this::getDirection);
        builder.addSlot(energySlot = EnergyInventorySlot.fillOrConvert(energyContainer, this::getLevel, listener, 143, 35), RelativeSide.BACK);
        return builder.build();
    }

    public boolean targetsHostile() {
        return targetsHostile;
    }

    public void setTargetsHostile(boolean targetsHostile) {
        this.targetsHostile = targetsHostile;
    }

    public boolean targetsPassive() {
        return targetsPassive;
    }

    public void setTargetsPassive(boolean targetsPassive) {
        this.targetsPassive = targetsPassive;
    }

    public boolean targetsPlayers() {
        return targetsPlayers;
    }
    public boolean targetsTrusted() {
        return targetsTrusted;
    }
    public void setTargetsTrusted(boolean targetsTrusted) {
        this.targetsTrusted = targetsTrusted;
    }

    public void setTargetsPlayers(boolean targetsPlayers) {
        this.targetsPlayers = targetsPlayers;
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        energySlot.fillContainerOrConvert();
        tryInvalidateTarget();
        tryFindTarget();
        if(target != null) {
//            setAnimData(TARGET_POS_X, target.getX());
//            setAnimData(TARGET_POS_Y, target.getY());
//            setAnimData(TARGET_POS_Z, target.getZ());
        }
        setAnimData(HAS_TARGET, true);
    }

    public void tryInvalidateTarget() {
        if(target != null && !target.isAlive() && target.distanceToSqr(this.getBlockPos().getCenter()) > MAX_RANGE*MAX_RANGE) {
            target = null;
            setAnimData(HAS_TARGET, false);
        }
    }

    private void tryFindTarget() {
        if(target == null) {
            List<LivingEntity> livingEntityList = level.getEntities(null, targetBox).stream().filter(LivingEntity.class::isInstance).map(LivingEntity.class::cast).toList();
            Optional<LivingEntity> optional = livingEntityList.stream()
                    .filter(e -> e.distanceToSqr(this.getBlockPos().getCenter()) <= MAX_RANGE*MAX_RANGE)
                    .sorted((o1, o2) -> Double.compare(o1.distanceToSqr(this.getBlockPos().getCenter()), o2.distanceToSqr(this.getBlockPos().getCenter())))
                    .filter(this::isValidTarget).findFirst();
            optional.ifPresent(livingEntity -> {
                this.target = livingEntity;
                setAnimData(HAS_TARGET, true);
            });
        }
    }

    private boolean isValidTarget(LivingEntity e) {
        if(!e.canBeSeenAsEnemy()) {
            return false;
        }
        if(this.targetsHostile && e.canAttackType(EntityType.PLAYER)) {
            return true;
        }
        if(this.targetsPassive && !(e instanceof Player)) {
            return true;
        }
        UUID owner = SecurityUtils.get().getOwnerUUID(this);
        if(this.targetsPlayers && e instanceof Player player) {
            if(player.getUUID().equals(owner)) {
                return false;
            }
            SecurityFrequency frequency = FrequencyType.SECURITY.getManager(null).getFrequency(owner);
            boolean playerTrusted = frequency.getTrustedUUIDs().contains(player.getUUID());
            return targetsTrusted || !playerTrusted;
        }
        return false;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        markUpdated();
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag nbtTags) {
        super.saveAdditional(nbtTags);
        nbtTags.putBoolean("targetsHostile", targetsHostile);
        nbtTags.putBoolean("targetsPassive", targetsPassive);
        nbtTags.putBoolean("targetsPlayers", targetsPlayers);
        nbtTags.putBoolean("targetsTrusted", targetsTrusted);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        NBTUtils.setBooleanIfPresent(nbt, "targetsHostile", value -> targetsHostile = value);
        NBTUtils.setBooleanIfPresent(nbt, "targetsPassive", value -> targetsPassive = value);
        NBTUtils.setBooleanIfPresent(nbt, "targetsPlayers", value -> targetsPlayers = value);
        NBTUtils.setBooleanIfPresent(nbt, "targetsTrusted", value -> targetsTrusted = value);
    }

    @Override
    public @NotNull CompoundTag getReducedUpdateTag() {
        CompoundTag updateTag = super.getReducedUpdateTag();
        updateTag.putBoolean("targetsHostile", targetsHostile);
        updateTag.putBoolean("targetsPassive", targetsPassive);
        updateTag.putBoolean("targetsPlayers", targetsPlayers);
        updateTag.putBoolean("targetsTrusted", targetsTrusted);
        return updateTag;
    }

    @Override
    public void handleUpdateTag(@NotNull CompoundTag tag) {
        super.handleUpdateTag(tag);
        NBTUtils.setBooleanIfPresent(tag, "targetsHostile", value -> targetsHostile = value);
        NBTUtils.setBooleanIfPresent(tag, "targetsPassive", value -> targetsPassive = value);
        NBTUtils.setBooleanIfPresent(tag, "targetsPlayers", value -> targetsPlayers = value);
        NBTUtils.setBooleanIfPresent(tag, "targetsTrusted", value -> targetsTrusted = value);
    }

    public void markUpdated() {
        this.setChanged();
        this.getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        if(!this.level.isClientSide()) sendUpdatePacket();
    }

    @Override
    protected void presetVariables() {
        super.presetVariables();
        tier = Attribute.getTier(getBlockType(), LaserTurretTier.class);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
