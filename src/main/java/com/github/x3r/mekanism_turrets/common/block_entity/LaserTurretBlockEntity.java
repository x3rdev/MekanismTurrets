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
import mekanism.common.lib.security.SecurityFrequency;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.component.ITileComponent;
import mekanism.common.upgrade.IUpgradeData;
import mekanism.common.util.NBTUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.constant.dataticket.SerializableDataTicket;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;


public class LaserTurretBlockEntity extends TileEntityMekanism implements GeoBlockEntity {
    @WrappingComputerMethod(wrapper = SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.class, methodNames = "getEnergyItem", docPlaceholder = "energy slot")
    EnergyInventorySlot energySlot;
    public static final SerializableDataTicket<Boolean> HAS_TARGET = GeckoLibUtil.addDataTicket(SerializableDataTicket.ofBoolean(ResourceLocation.fromNamespaceAndPath(MekanismTurrets.MOD_ID, "has_target")));
    public static final SerializableDataTicket<Double> TARGET_POS_X = GeckoLibUtil.addDataTicket(SerializableDataTicket.ofDouble(ResourceLocation.fromNamespaceAndPath(MekanismTurrets.MOD_ID, "target_pos_x")));
    public static final SerializableDataTicket<Double> TARGET_POS_Y = GeckoLibUtil.addDataTicket(SerializableDataTicket.ofDouble(ResourceLocation.fromNamespaceAndPath(MekanismTurrets.MOD_ID, "target_pos_y")));
    public static final SerializableDataTicket<Double> TARGET_POS_Z = GeckoLibUtil.addDataTicket(SerializableDataTicket.ofDouble(ResourceLocation.fromNamespaceAndPath(MekanismTurrets.MOD_ID, "target_pos_z")));
    private static final RawAnimation SHOOT_ANIMATION = RawAnimation.begin().then("shoot", Animation.LoopType.PLAY_ONCE);
    private final AABB targetBox = AABB.ofSize(getBlockPos().getCenter(), getTier().getRange(), getTier().getRange(), getTier().getRange());
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private LaserTurretTier tier;
    private MachineEnergyContainer<LaserTurretBlockEntity> energyContainer;
    private boolean targetsHostile = true;
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

    public void setTargetsPlayers(boolean targetsPlayers) {
        this.targetsPlayers = targetsPlayers;
    }

    public boolean targetsTrusted() {
        return targetsTrusted;
    }

    public void setTargetsTrusted(boolean targetsTrusted) {
        this.targetsTrusted = targetsTrusted;
    }

    @Override
    protected boolean onUpdateServer() {
        energySlot.fillContainerOrConvert();
        tryInvalidateTarget();
        tryFindTarget();
        energyContainer.setEnergyPerTick(FloatingLong.create(laserShotEnergy()));
        if(target != null) {
            updateTargetLocation();
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
        return super.onUpdateServer();
    }

    private void updateTargetLocation() {
        Vec3 targetPos = new Vec3(target.getX(), target.getY() + target.getBoundingBox().getYsize()/1.75, target.getZ());
        double laserSpeed = 0.75F*3F; //Laser speed is constant
        for (int i = 0; i < 21; i++) { //Tries to predict the path of the entity one second into the future
            Vec3 nextPos = targetPos.add(target.getDeltaMovement().scale(i));
            if(nextPos.length() <= laserSpeed*i || i == 20) {
                setAnimData(TARGET_POS_X, nextPos.x);
                setAnimData(TARGET_POS_Y, nextPos.y);
                setAnimData(TARGET_POS_Z, nextPos.z);
                return;
            }
        }
    }

    private void shootLaser() {
        if(target != null) { // Needed for scheduled shot
            int mufflerCount = getComponent().getUpgrades(Upgrade.MUFFLING);
            float volume = 1.0F - (mufflerCount / (float) Upgrade.MUFFLING.getMax());
            level.playSound(null, getBlockPos(), SoundRegistry.TURRET_SHOOT.get(), SoundSource.BLOCKS, volume, 1.0F);

            triggerAnim("controller", "shoot");

            float mobHeight = target.getBbHeight();
            Vec3 center = getBlockPos().getCenter();
            Vec3 lookVec = center.vectorTo(target.position().add(0, mobHeight / 2, 0)).normalize().scale(0.75F);
            LaserEntity laser = new LaserEntity(level, center.add(lookVec), tier.getDamage());
            laser.setDeltaMovement(lookVec.scale(2.25F));
            level.addFreshEntity(laser);
            energyContainer.extract(FloatingLong.create(laserShotEnergy()), Action.EXECUTE, AutomationType.INTERNAL);
        }
    }

    private int laserShotEnergy() {
        return 1000*(tier.ordinal()+1)*Mth.square(upgradeComponent.getUpgrades(Upgrade.SPEED)+1);
    }

    public void tryInvalidateTarget() {
        if(!isValidTarget(target)) {
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
        if(e == null) {
            return false;
        }
        if(!e.canBeSeenAsEnemy()) {
            return false;
        }
        if(e.distanceToSqr(this.getBlockPos().getCenter()) > getTier().getRange()*getTier().getRange()) {
            return false;
        }
        if(MekanismTurretsConfig.blacklistedEntities.get().stream().map(s -> BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(s))).anyMatch(entityType -> e.getType().equals(entityType))) {
            return false;
        }
        if(!turretFlagsAllowTargeting(e)) {
            return false;
        }
        if(!canSeeTarget(e)) {
            return false;
        }
        return true;
    }

    private boolean turretFlagsAllowTargeting(LivingEntity e) {
        MobCategory category = e.getType().getCategory();
        if(this.targetsHostile && !category.isFriendly()) {
            return true;
        }
        if(this.targetsPassive && category.isFriendly() && !category.equals(MobCategory.MISC)) {
            return true;
        }
        UUID owner = getOwnerUUID();

        if(this.targetsPlayers && e instanceof Player player) {
            if(!player.getUUID().equals(owner)) {
                if(this.targetsTrusted) {
                    // turret targets ALL players
                    return true;
                } else {
                    SecurityFrequency frequency = getSecurity().getFrequency();
                    if(frequency == null) {
                        // if frequency is null, the owner has not "trusted" any players, return true
                        return true;
                    }
                    if(!frequency.isTrusted(player.getUUID())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean canSeeTarget(LivingEntity e) {
        float mobHeight = e.getBbHeight();
        Vec3 center = getBlockPos().getCenter();
        Vec3 targetCenter = e.position().add(0, mobHeight/2, 0);
        Vec3 lookVec = center.vectorTo(targetCenter).normalize().scale(0.75F);
        ClipContext ctx = new ClipContext(center.add(lookVec), targetCenter, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty());
        return level.clip(ctx).getType().equals(HitResult.Type.MISS);
    }

    @Override
    public void parseUpgradeData(HolderLookup.Provider provider, @NotNull IUpgradeData data) {
        if(data instanceof LaserTurretUpgradeData upgradeData) {
            this.targetsHostile = upgradeData.targetsHostile();
            this.targetsPassive = upgradeData.targetsPassive();
            this.targetsPlayers = upgradeData.targetsPlayers();
            this.targetsTrusted = upgradeData.targetsTrusted();
            for (ITileComponent component : getComponents()) {
                component.read(upgradeData.components(), provider);
            }
        } else {
            super.parseUpgradeData(provider, data);
        }
    }

    @Override
    public @Nullable IUpgradeData getUpgradeData(HolderLookup.Provider provider) {
        return new LaserTurretUpgradeData(targetsHostile, targetsPassive, targetsPlayers, targetsTrusted, getComponents(), provider);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        markUpdated();
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putBoolean("targetsHostile", targetsHostile);
        tag.putBoolean("targetsPassive", targetsPassive);
        tag.putBoolean("targetsPlayers", targetsPlayers);
        tag.putBoolean("targetsTrusted", targetsTrusted);
        tag.putBoolean("hasTarget", target != null);
    }

    @Override
    public void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        NBTUtils.setBooleanIfPresent(tag, "targetsHostile", value -> targetsHostile = value);
        NBTUtils.setBooleanIfPresent(tag, "targetsPassive", value -> targetsPassive = value);
        NBTUtils.setBooleanIfPresent(tag, "targetsPlayers", value -> targetsPlayers = value);
        NBTUtils.setBooleanIfPresent(tag, "targetsTrusted", value -> targetsTrusted = value);
        NBTUtils.setBooleanIfPresent(tag, "hasTarget", value -> setAnimData(HAS_TARGET, value));
    }

    @Override
    public @NotNull CompoundTag getReducedUpdateTag(@NotNull HolderLookup.Provider provider) {
        CompoundTag tag = super.getReducedUpdateTag(provider);
        tag.putBoolean("targetsHostile", targetsHostile);
        tag.putBoolean("targetsPassive", targetsPassive);
        tag.putBoolean("targetsPlayers", targetsPlayers);
        tag.putBoolean("targetsTrusted", targetsTrusted);
        return tag;
    }

    @Override
    public void handleUpdateTag(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        super.handleUpdateTag(tag, provider);
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
