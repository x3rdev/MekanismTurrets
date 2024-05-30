package com.github.x3r.mekanism_turrets.common.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;

public class LaserEntity extends Projectile {

    private int lifeTime = 0;
    public LaserEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void tick() {
        super.tick();
        lifeTime++;
        if (lifeTime > 1200) {
            this.discard();
        }
        this.move(MoverType.SELF, this.position().add(this.getDeltaMovement()));
    }

    @Override
    protected void defineSynchedData() {

    }
}
