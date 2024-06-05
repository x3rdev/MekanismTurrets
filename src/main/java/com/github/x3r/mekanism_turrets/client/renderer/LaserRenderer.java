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

    public static final ResourceLocation LASER_TEXTURE = new ResourceLocation(MekanismTurrets.MOD_ID, "textures/entity/laser.png");
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
        Matrix4f matrix4f = pose.pose();
        Matrix3f matrix3f = pose.normal();

        for (int i = 0; i < 4; i++) {
            longFace(matrix4f, matrix3f, vertexconsumer, pPackedLight);
            pPoseStack.translate(0F, 2F, -2F);
            pPoseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        }
        pPoseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        pPoseStack.translate(-8F, 0F, -2F);
        for (int i = 0; i < 2; i++) {
            shortFace(matrix4f, matrix3f, vertexconsumer, pPackedLight);
            pPoseStack.translate(16F, 0F, 0F);
            pPoseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        }

        pPoseStack.popPose();
        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
    }

    private void shortFace(Matrix4f matrix4f, Matrix3f matrix3f, VertexConsumer vertexconsumer, int pPackedLight) {
        this.vertex(matrix4f, matrix3f, vertexconsumer, 0, -2, -2, 0.0F, 0.0F, 0, -1, 0, pPackedLight);
        this.vertex(matrix4f, matrix3f, vertexconsumer, 0, -2, 2, 0.125F, 0.0F, 0, -1, 0, pPackedLight);
        this.vertex(matrix4f, matrix3f, vertexconsumer, 0, 2, 2, 0.125F, 0.125F, 0, -1, 0, pPackedLight);
        this.vertex(matrix4f, matrix3f, vertexconsumer, 0, 2, -2, 0.0F, 0.125F, 0, -1, 0, pPackedLight);
    }

    private void longFace(Matrix4f matrix4f, Matrix3f matrix3f, VertexConsumer vertexconsumer, int pPackedLight) {
        this.vertex(matrix4f, matrix3f, vertexconsumer, -8, 0, -2, 0.0F, 0.0F, 0, -1, 0, pPackedLight);
        this.vertex(matrix4f, matrix3f, vertexconsumer, 8, 0, -2, 0.375F, 0.0F, 0, -1, 0, pPackedLight);
        this.vertex(matrix4f, matrix3f, vertexconsumer, 8, 0, 2, 0.375F, 0.125F, 0, -1, 0, pPackedLight);
        this.vertex(matrix4f, matrix3f, vertexconsumer, -8, 0, 2, 0.0F, 0.125F, 0, -1, 0, pPackedLight);
    }

    public void vertex(Matrix4f pMatrix, Matrix3f pNormal, VertexConsumer pConsumer, int pX, int pY, int pZ, float pU, float pV, int pNormalX, int pNormalZ, int pNormalY, int pPackedLight) {
        pConsumer.vertex(pMatrix, pX, pY, pZ).color(255, 255, 255, 255).uv(pU, pV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(pPackedLight).normal(pNormal, (float)pNormalX, (float)pNormalY, (float)pNormalZ).endVertex();
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
