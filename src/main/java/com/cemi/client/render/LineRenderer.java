package com.cemi.client.render;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class LineRenderer {
    public static void renderLine(MatrixStack poseStack, VertexConsumerProvider bufferSource, Vec3d vecA, Vec3d vecB,
            float thickness, int packedLight, int color, int alpha) {

        poseStack.push();
        Vec3d dir = vecB.subtract(vecA).normalize();

        // camera-facing (billboard) version:
        Vec3d cameraDir = MinecraftClient.getInstance().gameRenderer.getCamera().getPos().subtract(vecA).normalize();

        // main perpendicular
        Vec3d perp = dir.crossProduct(cameraDir);

        // if too small → fallback
        if (perp.lengthSquared() < 1) {
            // choose a safe axis that is NOT parallel to dir
            Vec3d fallback = Math.abs(dir.y) < 0.99
                    ? new Vec3d(0, 1, 0) // world up
                    : new Vec3d(1, 0, 0); // world right

            perp = dir.crossProduct(fallback);
        }

        perp = perp.normalize().multiply(thickness);
        Vec3d v1 = vecA.add(perp);
        Vec3d v2 = vecA.subtract(perp);
        Vec3d v3 = vecB.subtract(perp);
        Vec3d v4 = vecB.add(perp);

        VertexConsumer buffer = bufferSource
                .getBuffer(RenderLayer.getEntityTranslucent(new Identifier("minecraft", "textures/misc/white.png")));

        Matrix4f mat = poseStack.peek().getPositionMatrix();
        Matrix3f normalMat = poseStack.peek().getNormalMatrix();

        int r = (color & 0xFF0000) >> 16;
        int g = (color & 0xFF00) >> 8;
        int b = color & 0xFF;

        buffer.vertex(mat, (float) v1.x, (float) v1.y, (float) v1.z)
                .color(r, g, b, alpha)
                .texture(0, 1)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(packedLight)
                .normal(normalMat, 0, 0, 1)
                .next();

        buffer.vertex(mat, (float) v2.x, (float) v2.y, (float) v2.z)
                .color(r, g, b, alpha)
                .texture(0, 1)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(packedLight)
                .normal(normalMat, 0, 0, 1)
                .next();

        buffer.vertex(mat, (float) v3.x, (float) v3.y, (float) v3.z)
                .color(r, g, b, alpha)
                .texture(0, 1)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(packedLight)
                .normal(normalMat, 0, 0, 1)
                .next();

        buffer.vertex(mat, (float) v4.x, (float) v4.y, (float) v4.z)
                .color(r, g, b, alpha)
                .texture(0, 1)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(packedLight)
                .normal(normalMat, 0, 0, 1)
                .next();
        poseStack.pop();
    }
}
