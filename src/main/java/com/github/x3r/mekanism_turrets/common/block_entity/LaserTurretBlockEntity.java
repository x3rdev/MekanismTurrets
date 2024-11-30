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
import mekanism.common.tile.component.ITileComponent;
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
import java.util.ArrayList;


public class LaserTurretBlockEntity extends TileEntityMekanism implements GeoBlockEntity {

    @WrappingComputerMethod(wrapper = SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper.class, methodNames = "getEnergyItem", docPlaceholder = "energy slot")
    EnergyInventorySlot energySlot;
    public static SerializableDataTicket<Boolean> HAS_TARGET;
    public static SerializableDataTicket<Double> TARGET_POS_X;
    public static SerializableDataTicket<Double> TARGET_POS_Y;
    public static SerializableDataTicket<Double> TARGET_POS_Z;
    private static final RawAnimation SHOOT_ANIMATION = RawAnimation.begin().then("shoot", Animation.LoopType.PLAY_ONCE);
    private final AABB targetBox = AABB.ofSize(getBlockPos().getCenter(), getTier().getRange()*2, getTier().getRange()*2, getTier().getRange()*2);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private LaserTurretTier tier;
    private MachineEnergyContainer<LaserTurretBlockEntity> energyContainer;
    private boolean targetsHostile = true;
    private boolean targetsPassive = false;
    private boolean targetsPlayers = false;
    private boolean targetsTrusted = true;
    private @Nullable LivingEntity target;
    private List<Vec3> targetPreVelocity = new ArrayList<Vec3>();
    public float xRot0 = 0;
    public float yRot0 = 0;
    private int coolDown = 0;
    private int idleTicks = 0;

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
    protected void onUpdateServer() {
        super.onUpdateServer();
        energySlot.fillContainerOrConvert();
        tryInvalidateTarget();
        tryFindTarget();
        energyContainer.setEnergyPerTick(FloatingLong.create(laserShotEnergy()));
        if(target != null) {
            if (targetPreVelocity.size()>=5){
                targetPreVelocity.remove(0);
            }
            targetPreVelocity.add(target.getDeltaMovement());
            Vec3 targetPos = getShootLocation(target,targetPreVelocity);
            setAnimData(TARGET_POS_X, targetPos.x);
            setAnimData(TARGET_POS_Y, targetPos.y);
            setAnimData(TARGET_POS_Z, targetPos.z);
            setAnimData(HAS_TARGET, target != null);
            if(coolDown == 0) {
                float x = 10*((float) upgradeComponent.getUpgrades(Upgrade.SPEED) /8);
                //make the speed upgrade function properly as the tip with function \frac{(x+1)\ln{x+1}-x}{16} 's curve
                coolDown = Math.max(0,(int) Math.floor((tier.getCooldown()*(1-(0.9*(((x+1)*Math.log(x+1)-x)/16))))));
                //coolDown = Math.max(0, tier.getCooldown()-(2*upgradeComponent.getUpgrades(Upgrade.SPEED)));
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
//use 5 ticks' velocity data to predict movement,providing more accurate prediction
    private static Vec3 getShootLocation(LivingEntity entity,List<Vec3> preV) {
        Vec3 averageVelocity = Vec3.ZERO;
        for (Vec3 prevVelo : preV){
            averageVelocity = averageVelocity.add(prevVelo);
        }
        averageVelocity = averageVelocity.scale(1.0 / preV.size());// calculate averageVelocity
        Vec3 targetPos = new Vec3(entity.getX(), entity.getY(), entity.getZ());
        double laserSpeed = 3F; //Laser speed is constant
        for (int i = 1; i < 21; i++) { //Tries to predict the path of the entity one second into the future
            Vec3 deltaMovement = averageVelocity.multiply(0.4, 0, 0.4);
            Vec3 nextPos = targetPos.add(deltaMovement.scale(i-1));
            if(nextPos.length() <= laserSpeed*i || i == 20) {
                return new Vec3(nextPos.x, nextPos.y+(entity.getBbHeight()*0.8), nextPos.z);
            }

        }
        return targetPos;
    }

    private void shootLaser() {
        if(target != null) { // Needed for scheduled shot
            int mufflerCount = getComponent().getUpgrades(Upgrade.MUFFLING);
            float volume = 1.0F - (mufflerCount / (float) Upgrade.MUFFLING.getMax());
            level.playSound(null, getBlockPos(), SoundRegistry.TURRET_SHOOT.get(), SoundSource.BLOCKS, volume, 1.0F);

            triggerAnim("controller", "shoot");

            Vec3 center = getBlockPos().getCenter();
            Vec3 targetPos = getShootLocation(target,targetPreVelocity);

            LaserEntity laser = new LaserEntity(level, center.add(0, -0.15, 0), tier.getDamage());
            laser.setDeltaMovement(center.vectorTo(targetPos).normalize().scale(3.0F));
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
            targetPreVelocity.clear();
        }
    }

    private void tryFindTarget() {
        if(idleTicks-- > 0) {
            return;
        }
        if(target == null && (level.getGameTime()+this.hashCode()) % 3 == 0) {
            Optional<LivingEntity> optional = level.getEntitiesOfClass(LivingEntity.class, targetBox, this::isValidTarget).stream()
                    .min((o1, o2) -> Double.compare(
                            o1.distanceToSqr(this.getBlockPos().getCenter()),
                            o2.distanceToSqr(this.getBlockPos().getCenter())
                    ));
            if(optional.isPresent()) {
                this.target = optional.get();
                setAnimData(HAS_TARGET, true);
            } else {
                idleTicks = 20 * 4;
            }
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
        if(MekanismTurretsConfig.blacklistedEntities == null) {
            return false;
        }
        if(MekanismTurretsConfig.blacklistedEntities.get().stream().map(s -> ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(s))).anyMatch(entityType -> e.getType().equals(entityType))) {
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
        UUID owner = SecurityUtils.get().getOwnerUUID(this);
        if(this.targetsPlayers && e instanceof Player player) {
            if(!player.getUUID().equals(owner)) {
                if(this.targetsTrusted) {
                    // turret targets ALL players
                    return true;
                } else {
                    SecurityFrequency frequency = FrequencyType.SECURITY.getManager(null).getFrequency(owner);
                    if(frequency == null) {
                        // if frequency is null, the owner has not "trusted" any players, return true
                        return true;
                    }
                    if(!frequency.getTrustedUUIDs().contains(player.getUUID())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean canSeeTarget(LivingEntity e) {
        Vec3 center = getBlockPos().getCenter();
        Vec3 targetPos = e.position().add(0, (e.getBbHeight()*0.75), 0);
        Vec3 lookVec = center.vectorTo(targetPos).normalize().scale(0.75F);
        ClipContext ctx = new ClipContext(center.add(lookVec), targetPos, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, null);
        return level.clip(ctx).getType().equals(HitResult.Type.MISS);
    }

    @Override
    public void parseUpgradeData(@NotNull IUpgradeData data) {
        if(data instanceof LaserTurretUpgradeData upgradeData) {
            this.targetsHostile = upgradeData.targetsHostile();
            this.targetsPassive = upgradeData.targetsPassive();
            this.targetsPlayers = upgradeData.targetsPlayers();
            this.targetsTrusted = upgradeData.targetsTrusted();
            for (ITileComponent component : getComponents()) {
                component.read(upgradeData.components());
            }
        } else {
            super.parseUpgradeData(data);
        }
    }

    @Override
    public @Nullable IUpgradeData getUpgradeData() {
        return new LaserTurretUpgradeData(targetsHostile, targetsPassive, targetsPlayers, targetsTrusted, getComponents());
    }

    @Override
    public void onLoad() {
        super.onLoad();
        markUpdated();
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean("targetsHostile", targetsHostile);
        tag.putBoolean("targetsPassive", targetsPassive);
        tag.putBoolean("targetsPlayers", targetsPlayers);
        tag.putBoolean("targetsTrusted", targetsTrusted);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        NBTUtils.setBooleanIfPresent(tag, "targetsHostile", value -> targetsHostile = value);
        NBTUtils.setBooleanIfPresent(tag, "targetsPassive", value -> targetsPassive = value);
        NBTUtils.setBooleanIfPresent(tag, "targetsPlayers", value -> targetsPlayers = value);
        NBTUtils.setBooleanIfPresent(tag, "targetsTrusted", value -> targetsTrusted = value);
    }

    @Override
    public @NotNull CompoundTag getReducedUpdateTag() {
        CompoundTag tag = super.getReducedUpdateTag();
        tag.putBoolean("targetsHostile", targetsHostile);
        tag.putBoolean("targetsPassive", targetsPassive);
        tag.putBoolean("targetsPlayers", targetsPlayers);
        tag.putBoolean("targetsTrusted", targetsTrusted);
        return tag;
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
