package com.github.x3r.mekanism_turrets.common.block_entity;

import com.github.x3r.mekanism_turrets.MekanismTurrets;
import com.github.x3r.mekanism_turrets.MekanismTurretsConfig;
import com.github.x3r.mekanism_turrets.common.entity.LaserEntity;
import com.github.x3r.mekanism_turrets.common.registry.SoundRegistry;
import com.github.x3r.mekanism_turrets.common.scheduler.Scheduler;
import mekanism.api.*;
import mekanism.api.math.FloatingLong;
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
import mekanism.common.upgrade.IUpgradeData;
import mekanism.common.util.NBTUtils;
import mekanism.common.util.SecurityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
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
    private static final RawAnimation SHOOT_ANIMATION = RawAnimation.begin().then("shoot", Animation.LoopType.PLAY_ONCE);
    private static final float MAX_RANGE = 20;
    private final AABB targetBox = AABB.ofSize(getBlockPos().getCenter(), MAX_RANGE, MAX_RANGE, MAX_RANGE);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private LaserTurretTier tier;
    private MachineEnergyContainer<LaserTurretBlockEntity> energyContainer;
    private boolean targetsHostile = false;
    private boolean targetsPassive = false;
    private boolean targetsPlayers = false;
    private boolean targetsTrusted = true;
    private @Nullable LivingEntity target;
    public float xRot0 = 0;
    public float yRot0 = 0;
    private int coolDown = 0;

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

    public LaserTurretTier getTier() {
        return tier;
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
            setAnimData(TARGET_POS_X, target.getX());
            setAnimData(TARGET_POS_Y, target.getY());
            setAnimData(TARGET_POS_Z, target.getZ());
            if(coolDown == 0) {
                coolDown = Math.max(0, tier.getCooldown()-(2*upgradeComponent.getUpgrades(Upgrade.SPEED)));
                if(energyContainer.getEnergy().greaterOrEqual(FloatingLong.create(laserShotEnergy()))) {
                    shootLaser();
                    if(tier.equals(LaserTurretTier.ULTIMATE)) {
                        Scheduler.schedule(this::shootLaser, 10);
                    }
                }
            } else {
                coolDown--;
            }
        }
    }

    private void shootLaser() {
        if(target != null) {
            int mufflerCount = getComponent().getUpgrades(Upgrade.MUFFLING);
            float volume = 1.0F - (mufflerCount / (float) Upgrade.MUFFLING.getMax());
            level.playSound(null, getBlockPos(), SoundRegistry.TURRET_SHOOT.get(), SoundSource.BLOCKS, volume, 1.0F);

            triggerAnim("controller", "shoot");

            float mobHeight = target.getBbHeight();
            Vec3 center = getBlockPos().getCenter();
            Vec3 lookVec = center.vectorTo(target.position().add(0, mobHeight/2, 0)).normalize().scale(0.75F);
            LaserEntity laser = new LaserEntity(level, center.add(lookVec), tier.getDamage());
            laser.setDeltaMovement(lookVec.scale(2.25F));
            level.addFreshEntity(laser);
            energyContainer.extract(FloatingLong.create(laserShotEnergy()), Action.EXECUTE, AutomationType.INTERNAL);
        }
    }

    private int laserShotEnergy() {
        return 1000*Mth.square((upgradeComponent.getUpgrades(Upgrade.SPEED)+1)*(tier.ordinal()+1));
    }

    public void tryInvalidateTarget() {
        if(target != null && !isValidTarget(target)) {
            setAnimData(HAS_TARGET, false);
            target = null;
        }
    }

    private void tryFindTarget() {
        if(target == null) {
            List<LivingEntity> livingEntityList = level.getEntities(null, targetBox).stream().filter(LivingEntity.class::isInstance).map(LivingEntity.class::cast).toList();
            Optional<LivingEntity> optional = livingEntityList.stream()
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
        if(e.distanceToSqr(this.getBlockPos().getCenter()) > MAX_RANGE*MAX_RANGE) {
            return false;
        }
        if(MekanismTurretsConfig.blacklistedEntities.get().stream().map(s -> ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(s))).anyMatch(entityType -> e.getType().equals(entityType))) {
            return false;
        }
        if(!canSeeTarget(e)) {
            return false;
        }
        if(this.targetsHostile && e.getType().getCategory().equals(MobCategory.MONSTER)) {
            return true;
        }
        if(this.targetsPassive && !e.getType().getCategory().equals(MobCategory.MONSTER)) {
            return true;
        }
        UUID owner = SecurityUtils.get().getOwnerUUID(this);
        if(this.targetsPlayers && e instanceof Player player) {
            if(player.getUUID().equals(owner)) {
                return false;
            }
            if(!targetsTrusted) {
                SecurityFrequency frequency = FrequencyType.SECURITY.getManager(null).getFrequency(owner);
                return frequency != null && !frequency.getTrustedUUIDs().contains(player.getUUID());
            }
        }
        return false;
    }

    private boolean canSeeTarget(LivingEntity e) {
        float mobHeight = e.getBbHeight();
        Vec3 center = getBlockPos().getCenter();
        Vec3 targetCenter = e.position().add(0, mobHeight/2, 0);
        Vec3 lookVec = center.vectorTo(targetCenter).normalize().scale(0.75F);
        ClipContext ctx = new ClipContext(center.add(lookVec), targetCenter, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null);
        return level.clip(ctx).getType().equals(HitResult.Type.MISS);
    }

    @Override
    public void parseUpgradeData(@NotNull IUpgradeData data) {
        if(data instanceof LaserTurretUpgradeData) {
            LaserTurretUpgradeData upgradeData = (LaserTurretUpgradeData) data;
            this.targetsHostile = upgradeData.targetsHostile();
            this.targetsPassive = upgradeData.targetsPassive();
            this.targetsPlayers = upgradeData.targetsPlayers();
            this.targetsTrusted = upgradeData.targetsTrusted();
        } else {
            super.parseUpgradeData(data);
        }
    }

    @Override
    public @Nullable IUpgradeData getUpgradeData() {
        return new LaserTurretUpgradeData(targetsHostile, targetsPassive, targetsPlayers, targetsTrusted);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        markUpdated();
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag nbtTags) {
        super.saveAdditional(nbtTags);
        getReducedUpdateTag();
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        handleUpdateTag(nbt);
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
        controllers.add(new AnimationController<>(this, "controller", 0, state -> PlayState.CONTINUE)
                .triggerableAnim("shoot", SHOOT_ANIMATION));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
