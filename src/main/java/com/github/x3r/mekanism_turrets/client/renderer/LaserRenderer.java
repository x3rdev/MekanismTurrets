package com.github.x3r.mekanism_turrets.client.renderer;

import com.github.x3r.mekanism_turrets.MekanismTurrets;
import com.github.x3r.mekanism_turrets.common.entity.LaserEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class LaserRenderer<T extends LaserEntity> extends EntityRenderer<T> {

    public static final ResourceLocation LASER_TEXTURE = ResourceLocation.fromNamespaceAndPath(MekanismTurrets.MOD_ID, "textures/entity/laser.png");
    public LaserRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(T pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        fixRotation(pEntity);
        pPoseStack.pushPose();
        pPoseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(pPartialTick, pEntity.yRotO, pEntity.getYRot()) - 90.0F));
        pPoseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(pPartialTick, pEntity.xRotO, pEntity.getXRot())));

        pPoseStack.scale(0.05625F, 0.05625F, 0.05625F);
        pPoseStack.translate(0F, 2.5F, 0F);
        VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.eyes(this.getTextureLocation(pEntity)));
        PoseStack.Pose pose = pPoseStack.last();

        for (int i = 0; i < 4; i++) {
            longFace(pose, vertexconsumer, pPackedLight);
            pPoseStack.translate(0F, 2F, -2F);
            pPoseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        }
        pPoseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        pPoseStack.translate(-8F, 0F, -2F);
        for (int i = 0; i < 2; i++) {
            shortFace(pose, vertexconsumer, pPackedLight);
            pPoseStack.translate(16F, 0F, 0F);
            pPoseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        }

        pPoseStack.popPose();
        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
    }

    private void shortFace(PoseStack.Pose pose, VertexConsumer vertexconsumer, int pPackedLight) {
        this.vertex(pose, vertexconsumer, 0, -2, -2, 0.0F, 0.0F, 0, -1, 0, pPackedLight);
        this.vertex(pose, vertexconsumer, 0, -2, 2, 0.125F, 0.0F, 0, -1, 0, pPackedLight);
        this.vertex(pose, vertexconsumer, 0, 2, 2, 0.125F, 0.125F, 0, -1, 0, pPackedLight);
        this.vertex(pose, vertexconsumer, 0, 2, -2, 0.0F, 0.125F, 0, -1, 0, pPackedLight);
    }

    private void longFace(PoseStack.Pose pose, VertexConsumer vertexconsumer, int pPackedLight) {
        this.vertex(pose, vertexconsumer, -8, 0, -2, 0.0F, 0.0F, 0, -1, 0, pPackedLight);
        this.vertex(pose, vertexconsumer, 8, 0, -2, 0.375F, 0.0F, 0, -1, 0, pPackedLight);
        this.vertex(pose, vertexconsumer, 8, 0, 2, 0.375F, 0.125F, 0, -1, 0, pPackedLight);
        this.vertex(pose, vertexconsumer, -8, 0, 2, 0.0F, 0.125F, 0, -1, 0, pPackedLight);
    }

    public void vertex(PoseStack.Pose pose, VertexConsumer pConsumer, int pX, int pY, int pZ, float pU, float pV, int pNormalX, int pNormalZ, int pNormalY, int pPackedLight) {
        pConsumer.addVertex(pose, pX, pY, pZ).setColor(255, 255, 255, 255).setUv(pU, pV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(pPackedLight).setNormal(pose, (float)pNormalX, (float)pNormalY, (float)pNormalZ);
    }

    private void fixRotation(T entity) {
        Vec3 vec = entity.getDeltaMovement();
        entity.setYRot((float) Math.atan2(vec.x, vec.z) * Mth.RAD_TO_DEG);
        entity.setXRot((float) Math.atan2(vec.y, Math.sqrt(vec.x * vec.x + vec.z * vec.z)) * Mth.RAD_TO_DEG);
    }

    @Override
    public ResourceLocation getTextureLocation(LaserEntity pEntity) {
        return LASER_TEXTURE;
    }
}
