package com.github.x3r.mekanism_turrets.common.entity;

import com.github.x3r.mekanism_turrets.common.block.LaserTurretBlock;
import com.github.x3r.mekanism_turrets.common.registry.DamageTypeRegistry;
import com.github.x3r.mekanism_turrets.common.registry.EntityRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.checkerframework.checker.units.qual.A;
import software.bernie.geckolib.event.GeoRenderEvent;
import net.minecraft.world.entity.player.Player;

import static net.minecraft.world.item.enchantment.EnchantmentHelper.getEnchantmentLevel;

public class LaserEntity extends Projectile {

    private int lifeTime = 0;
    private double damage = 1.0F;
    public LaserEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.noPhysics = true;
    }

    public LaserEntity(Level pLevel, Vec3 pos, double damage) {
        this(EntityRegistry.LASER.get(), pLevel);
        this.setPos(pos);
        this.damage = damage;
    }

    @Override
    public void tick() {
        super.tick();
        if(!level().isClientSide()) {
            HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
            if(hitResult.getType().equals(HitResult.Type.BLOCK)) {
                onHitBlock((BlockHitResult) hitResult);
            }
            level().getEntities(this, getBoundingBox().inflate(0.75)).forEach(entity -> {
                boolean isPlayer = entity.getType().toString().equals("entity.minecraft.player");

                if (isPlayer) {
                    double finaldamage = 0;
                    float armor = 0;
                    float toughness = 0;
                    float protectEnchantReduction = (float) (0.01 * Math.max(80, 4 * getEnchantmentLevel(Enchantments.ALL_DAMAGE_PROTECTION, (LivingEntity) entity)));
                    for (ItemStack armorStack : entity.getArmorSlots()) {
                        if (armorStack.getItem() instanceof ArmorItem armorItem) {
                            armor = armor + armorItem.getDefense();
                            toughness = toughness + armorItem.getToughness();
                        }
                    }
                    float damageReductionRate = (float) (Math.min(20, Math.max(armor / 5, armor - ((4 * this.damage) / (Math.min(toughness, 20) + 8)))) / 25);
                    finaldamage = this.damage * (1 - damageReductionRate) * (1 - protectEnchantReduction);
                    int durabilityLoss = (int) Math.floor(this.damage - finaldamage);
                    for (ItemStack armorStack : entity.getArmorSlots()) {
                        armorStack.hurtAndBreak(durabilityLoss, (LivingEntity) entity, (player) -> player.broadcastBreakEvent(armorStack.getEquipmentSlot()));
                    }
                    this.damage = finaldamage;
                }

                Entity player = getOwner();
                try {
                    ((LivingEntity) entity).setLastHurtByPlayer((Player) this.getOwner());
                } catch (Exception e) {
                    ;
                }
                entity.hurt(new DamageTypeRegistry(level().registryAccess(), player).laser(), (float) this.damage);
            });
            lifeTime++;
            if (lifeTime > 10 * 20) {
                this.discard();
            }
        }
        this.setPos(this.position().add(this.getDeltaMovement()));
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        super.onHitBlock(pResult);
        BlockState state = (level().getBlockState(pResult.getBlockPos()));
        if(!(state.getBlock() instanceof LaserTurretBlock) && state.isCollisionShapeFullBlock(level(), pResult.getBlockPos())) {
            this.discard();
        }
    }

    @Override
    protected void defineSynchedData() {

    }
}
