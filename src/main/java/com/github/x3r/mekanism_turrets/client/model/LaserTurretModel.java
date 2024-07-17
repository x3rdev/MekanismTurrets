package com.github.x3r.mekanism_turrets.client.model;

import com.github.x3r.mekanism_turrets.MekanismTurrets;
import com.github.x3r.mekanism_turrets.common.block.LaserTurretBlock;
import com.github.x3r.mekanism_turrets.common.block_entity.LaserTurretBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class LaserTurretModel extends DefaultedBlockGeoModel<LaserTurretBlockEntity> {

    private static final ResourceLocation MODEL_BASIC = ResourceLocation.fromNamespaceAndPath(MekanismTurrets.MOD_ID, "geo/block/basic_laser_turret.geo.json");
    private static final ResourceLocation MODEL_ADVANCED = ResourceLocation.fromNamespaceAndPath(MekanismTurrets.MOD_ID, "geo/block/advanced_laser_turret.geo.json");
    private static final ResourceLocation MODEL_ELITE = ResourceLocation.fromNamespaceAndPath(MekanismTurrets.MOD_ID, "geo/block/elite_laser_turret.geo.json");
    private static final ResourceLocation MODEL_ULTIMATE = ResourceLocation.fromNamespaceAndPath(MekanismTurrets.MOD_ID, "geo/block/ultimate_laser_turret.geo.json");

    private static final ResourceLocation TEXTURE_BASIC = ResourceLocation.fromNamespaceAndPath(MekanismTurrets.MOD_ID, "textures/block/basic_laser_turret.png");
    private static final ResourceLocation TEXTURE_ADVANCED = ResourceLocation.fromNamespaceAndPath(MekanismTurrets.MOD_ID, "textures/block/advanced_laser_turret.png");
    private static final ResourceLocation TEXTURE_ELITE = ResourceLocation.fromNamespaceAndPath(MekanismTurrets.MOD_ID, "textures/block/elite_laser_turret.png");
    private static final ResourceLocation TEXTURE_ULTIMATE = ResourceLocation.fromNamespaceAndPath(MekanismTurrets.MOD_ID, "textures/block/ultimate_laser_turret.png");

    private static final ResourceLocation ANIMATION_BASIC = ResourceLocation.fromNamespaceAndPath(MekanismTurrets.MOD_ID, "animations/block/basic_anim.json");
    private static final ResourceLocation ANIMATION_ADVANCED = ResourceLocation.fromNamespaceAndPath(MekanismTurrets.MOD_ID, "animations/block/advanced_anim.json");
    private static final ResourceLocation ANIMATION_ELITE = ResourceLocation.fromNamespaceAndPath(MekanismTurrets.MOD_ID, "animations/block/elite_anim.json");
    private static final ResourceLocation ANIMATION_ULTIMATE = ResourceLocation.fromNamespaceAndPath(MekanismTurrets.MOD_ID, "animations/block/ultimate_anim.json");
    public LaserTurretModel() {
        super(ResourceLocation.fromNamespaceAndPath(MekanismTurrets.MOD_ID, "laser_turret"));
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
        GeoBone turret = getAnimationProcessor().getBone("turret");
        GeoBone cannon = getAnimationProcessor().getBone("cannon");
        if(turret != null && cannon != null) {
            if(hasTarget(animatable)) {
                Vec3 targetPos = new Vec3(targetX(animatable), targetY(animatable), targetZ(animatable));
                Direction direction = animatable.getBlockState().getValue(LaserTurretBlock.FACING);
                Vec3 center = animatable.getBlockPos().getCenter();
                Vector3f deltaPos = getTransform(direction).transform(new Vec3(targetPos.x - center.x, targetPos.y - center.y, targetPos.z - center.z).toVector3f());
                double deltaHorizontal = Math.sqrt(deltaPos.x * deltaPos.x + deltaPos.z * deltaPos.z);
                float xRot = lerp(animatable.xRot0, (float) ((3 * Mth.HALF_PI) + Math.atan2(deltaPos.y, deltaHorizontal)));
                float yRot = lerp(animatable.yRot0, (float) ((3 * Mth.HALF_PI) - Math.atan2(deltaPos.z, deltaPos.x)));
                animatable.xRot0 = xRot;
                animatable.yRot0 = yRot;
                cannon.setRotX(xRot);
                turret.setRotY(yRot);
            } else {
                float xRot = lerp(animatable.xRot0, -Mth.HALF_PI);
                float yRot = lerp(animatable.yRot0, 0);
                animatable.xRot0 = xRot;
                animatable.yRot0 = yRot;
                cannon.setRotX(xRot);
                turret.setRotY(yRot);
            }
        }
    }


    private Quaternionf getTransform(Direction direction) {
        switch (direction) {
            case NORTH -> {
                return new Quaternionf().rotationX((float) (-Math.PI/2));
            }
            case EAST -> {
                return new Quaternionf().rotationZ((float) (-Math.PI/2));
            }
            case SOUTH -> {
                return new Quaternionf().rotationX((float) (Math.PI/2));
            }
            case WEST -> {
                return new Quaternionf().rotationZ((float) (Math.PI/2));
            }
            case UP -> {
                return new Quaternionf().rotationZ((float) Math.PI);
            }
            case DOWN -> {
                return new Quaternionf();
            }
        }
        return new Quaternionf();
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
