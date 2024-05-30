package com.github.x3r.mekanism_turrets.client.model;

import com.github.x3r.mekanism_turrets.MekanismTurrets;
import com.github.x3r.mekanism_turrets.common.block_entity.LaserTurretBlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class LaserTurretModel extends DefaultedBlockGeoModel<LaserTurretBlockEntity> {

    public LaserTurretModel() {
        super(new ResourceLocation(MekanismTurrets.MOD_ID, "basic_laser_turret"));
    }

    @Override
    public void setCustomAnimations(LaserTurretBlockEntity animatable, long instanceId, AnimationState<LaserTurretBlockEntity> animationState) {
        CoreGeoBone turret = getAnimationProcessor().getBone("turret");
        if(turret != null) {
            if(hasTarget(animationState)) {
                double targetX = animationState.getData(LaserTurretBlockEntity.TARGET_POS_X);
                double targetY = animationState.getData(LaserTurretBlockEntity.TARGET_POS_Y);
                double targetZ = animationState.getData(LaserTurretBlockEntity.TARGET_POS_Z);
                Vec3 center = animatable.getBlockPos().getCenter();
                double d = targetX - center.x;
                double e = (targetY + 1.25) - center.y;
                double f = targetZ - center.z;
                double g = Math.sqrt(d * d + f * f);
                turret.setRotY((float) ((3 * Mth.HALF_PI) - Math.atan2(f, d)));
                turret.setRotX((float) Math.atan2(e, g));
            } else {
                turret.setRotX(0);
                turret.setRotY(0);
                turret.setRotZ(0);
            }
        }
    }

    private boolean hasTarget(AnimationState<LaserTurretBlockEntity> animationState) {
        return animationState.getData(LaserTurretBlockEntity.HAS_TARGET) != null && animationState.getData(LaserTurretBlockEntity.HAS_TARGET);
    }
}
