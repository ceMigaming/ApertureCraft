package com.cemi.client.render.entity;

import com.cemi.client.render.LineRenderer;
import com.cemi.client.render.entity.model.RocketTurretModel;
import com.cemi.entity.RocketTurretEntity;
import com.cemi.entity.TurretEntity;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RocketTurretRenderer extends GeoEntityRenderer<RocketTurretEntity> {

    public RocketTurretRenderer(Context renderManager) {
        super(renderManager, new RocketTurretModel());
    }

    @Override
    public void render(RocketTurretEntity entity, float entityYaw, float partialTick,
            MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        if (entity.laserEnd == null || entity.lastLaserEnd == null)
            return;

        // Smooth interpolation between last tick and current tick (important!)
        Vec3d smoothedEnd = entity.lastLaserEnd.lerp(entity.laserEnd, partialTick);

        // Start position (turret muzzle offset)
        Vec3d start = entity.getLerpedPos(partialTick)
                .add(0, entity.getEyeHeight(entity.getPose()), 0);

        Vec3d dir = smoothedEnd.subtract(start);

        poseStack.push();
        float yaw = MathHelper.lerp(partialTick, entity.lastYaw, entity.yaw);
        poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-yaw));
        poseStack.translate(0, entity.getEyeHeight(entity.getPose()), 0);
        poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(yaw));

        LineRenderer.renderLine(poseStack, bufferSource, new Vec3d(0, 0, 0),
                dir, 0.02f,
                packedLight, 0x00FFFF, 127);

        poseStack.pop();
    }

}
