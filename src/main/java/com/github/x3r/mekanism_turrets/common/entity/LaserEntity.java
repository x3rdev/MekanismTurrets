package com.github.x3r.mekanism_turrets.common.entity;

import com.github.x3r.mekanism_turrets.common.block.LaserTurretBlock;
import com.github.x3r.mekanism_turrets.common.registry.DamageTypeRegistry;
import com.github.x3r.mekanism_turrets.common.registry.EntityRegistry;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityEvent;

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
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {

    }

    @Override
    public void tick() {
        super.tick();
        if(!level().isClientSide()) {
            if (lifeTime++ > 10 * 20) {
                this.discard();
                return;
            }
            if(this.position().y > level().getMaxBuildHeight()+100) {
                this.discard();
                return;
            }
            HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
            if(hitResult.getType().equals(HitResult.Type.BLOCK)) {
                onHitBlock((BlockHitResult) hitResult);
            }
            if(!this.isRemoved()) { // may be removed by onHitBlock
                level().getEntities(this, getBoundingBox().inflate(0.5)).forEach(entity -> {
                    entity.hurt(new DamageTypeRegistry(level().registryAccess()).laser(), (float) this.damage);
                });
            }
//            if(hitResult.getType().equals(HitResult.Type.ENTITY)) {
//                ((EntityHitResult) hitResult).getEntity().hurt(new DamageTypeRegistry(level().registryAccess()).laser(), (float) this.damage);
//            }
        }
        this.setPos(this.position().add(this.getDeltaMovement()));
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        if(!level().isClientSide()) {
            pResult.getEntity().hurt(new DamageTypeRegistry(level().registryAccess()).laser(), (float) this.damage);
        }
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
    public boolean canUsePortal(boolean allowPassengers) {
        return false;
    }

    @SubscribeEvent
    public static void enterChunk(EntityEvent.EnteringSection event) {
        if(!event.getEntity().level().isClientSide() && event.didChunkChange() && event.getEntity() instanceof LaserEntity) {
            ServerLevel level = ((ServerLevel) event.getEntity().level());
            if(level.isPositionEntityTicking(SectionPos.of(event.getPackedNewPos()).center())) {
                event.getEntity().discard();
            }
        }
    }
}
