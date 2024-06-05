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

    private static final ResourceLocation MODEL_BASIC = new ResourceLocation(MekanismTurrets.MOD_ID, "geo/block/basic_laser_turret.geo.json");
    private static final ResourceLocation MODEL_ADVANCED = new ResourceLocation(MekanismTurrets.MOD_ID, "geo/block/advanced_laser_turret.geo.json");
    private static final ResourceLocation MODEL_ELITE = new ResourceLocation(MekanismTurrets.MOD_ID, "geo/block/elite_laser_turret.geo.json");
    private static final ResourceLocation MODEL_ULTIMATE = new ResourceLocation(MekanismTurrets.MOD_ID, "geo/block/ultimate_laser_turret.geo.json");

    private static final ResourceLocation TEXTURE_BASIC = new ResourceLocation(MekanismTurrets.MOD_ID, "textures/block/basic_laser_turret.png");
    private static final ResourceLocation TEXTURE_ADVANCED = new ResourceLocation(MekanismTurrets.MOD_ID, "textures/block/advanced_laser_turret.png");
    private static final ResourceLocation TEXTURE_ELITE = new ResourceLocation(MekanismTurrets.MOD_ID, "textures/block/elite_laser_turret.png");
    private static final ResourceLocation TEXTURE_ULTIMATE = new ResourceLocation(MekanismTurrets.MOD_ID, "textures/block/ultimate_laser_turret.png");

    private static final ResourceLocation ANIMATION_BASIC = new ResourceLocation(MekanismTurrets.MOD_ID, "animations/block/basic_anim.json");
    private static final ResourceLocation ANIMATION_ADVANCED = new ResourceLocation(MekanismTurrets.MOD_ID, "animations/block/advanced_anim.json");
    private static final ResourceLocation ANIMATION_ELITE = new ResourceLocation(MekanismTurrets.MOD_ID, "animations/block/elite_anim.json");
    private static final ResourceLocation ANIMATION_ULTIMATE = new ResourceLocation(MekanismTurrets.MOD_ID, "animations/block/ultimate_anim.json");
    public LaserTurretModel() {
        super(new ResourceLocation(MekanismTurrets.MOD_ID, "laser_turret"));
    }

    @Override
    public ResourceLocation getModelResource(LaserTurretBlockEntity animatable) {
        return switch (animatable.getTier()) {
            case BASIC -> MODEL_BASIC;
            case ADVANCED -> MODEL_ADVANCED;
            case ELITE -> MODEL_ELITE;
            case ULTIMATE -> MODEL_ULTIMATE;
        };
    }

    @Override
    public ResourceLocation getTextureResource(LaserTurretBlockEntity animatable) {
        return switch (animatable.getTier()) {
            case BASIC -> TEXTURE_BASIC;
            case ADVANCED -> TEXTURE_ADVANCED;
            case ELITE -> TEXTURE_ELITE;
            case ULTIMATE -> TEXTURE_ULTIMATE;
        };
    }

    @Override
    public ResourceLocation getAnimationResource(LaserTurretBlockEntity animatable) {
        return switch (animatable.getTier()) {
            case BASIC -> ANIMATION_BASIC;
            case ADVANCED -> ANIMATION_ADVANCED;
            case ELITE -> ANIMATION_ELITE;
            case ULTIMATE -> ANIMATION_ULTIMATE;
        };
    }

    @Override
    public void setCustomAnimations(LaserTurretBlockEntity animatable, long instanceId, AnimationState<LaserTurretBlockEntity> animationState) {
        CoreGeoBone turret = getAnimationProcessor().getBone("turret");
        if(turret != null) {
            if(hasTarget(animatable)) {
                double targetX = targetX(animatable);
                double targetY = targetY(animatable);
                double targetZ = targetZ(animatable);
                Vec3 center = animatable.getBlockPos().getCenter();
                double d = targetX - center.x;
                double e = (targetY + 1.25) - center.y;
                double f = targetZ - center.z;
                double g = Math.sqrt(d * d + f * f);
                float xRot = lerp(animatable.xRot0, (float) Math.atan2(e, g));
                animatable.xRot0 = xRot;
                turret.setRotX(xRot);
                float yRot = lerp(animatable.yRot0, (float) ((3 * Mth.HALF_PI) - Math.atan2(f, d)));
                animatable.yRot0 = yRot;
                turret.setRotY(yRot);
            } else {
                float xRot = lerp(animatable.xRot0, 0);
                animatable.xRot0 = xRot;
                turret.setRotX(xRot);
                float yRot = lerp(animatable.yRot0, 0);
                animatable.yRot0 = yRot;
                turret.setRotY(yRot);
            }
        }
    }

    private float lerp(float start, float end) {
        return Mth.rotLerp(0.1F, start * Mth.RAD_TO_DEG, end * Mth.RAD_TO_DEG) * Mth.DEG_TO_RAD;
    }

    private boolean hasTarget(LaserTurretBlockEntity animatable) {
        return Boolean.TRUE.equals(animatable.getAnimData(LaserTurretBlockEntity.HAS_TARGET));
    }

    private double targetX(LaserTurretBlockEntity animatable) {
        Double targetX = animatable.getAnimData(LaserTurretBlockEntity.TARGET_POS_X);
        if(targetX != null) {
            return targetX;
        }
        return 0;
    }
    private double targetY(LaserTurretBlockEntity animatable) {
        Double targetY = animatable.getAnimData(LaserTurretBlockEntity.TARGET_POS_Y);
        if(targetY != null) {
            return targetY;
        }
        return 0;
    }
    private double targetZ(LaserTurretBlockEntity animatable) {
        Double targetZ = animatable.getAnimData(LaserTurretBlockEntity.TARGET_POS_Z);
        if(targetZ != null) {
            return targetZ;
        }
        return 0;
    }
}
