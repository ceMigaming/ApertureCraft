package com.cemi.client.render;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

import com.cemi.ApertureCraft;
import com.cemi.entity.AperturePortal;
import com.cemi.util.ShaderHelper;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import qouteall.imm_ptl.core.portal.Portal;

public class PortalOverlayHook {

    private static Identifier portalClosed = new Identifier(ApertureCraft.MOD_ID, "textures/entity/portalclose.png");
    private static Identifier portalOpen = new Identifier(ApertureCraft.MOD_ID, "textures/entity/portalopen.png");
    private static Identifier portalOutline = new Identifier(ApertureCraft.MOD_ID, "textures/entity/portaloutline.png");
    private static Identifier portalOutlineFancy = new Identifier(ApertureCraft.MOD_ID,
            "textures/entity/portaloutlinefancy.png");

    public static void renderOverlay(Portal portal) {
        if (!(portal instanceof AperturePortal ap))
            return;

        ShaderProgram shader = ShaderHelper.getPortalShader();
        if (shader == null)
            return;

        MatrixStack matrices = RenderSystem.getModelViewStack();
        matrices.push();

        MinecraftClient client = MinecraftClient.getInstance();
        Vec3d camPos = client.gameRenderer.getCamera().getPos();

        Vec3d portalPos = portal.getPos();

        // move into world position RELATIVE to camera
        // matrices.translate(portalPos.x, portalPos.y, portalPos.z);

        // Apply portal transform
        matrices.multiply(portal.getOrientationRotation().toMcQuaternion());

        int color = ap.getColor();
        int r = (color & 0xFF0000) >> 16;
        int g = (color & 0xFF00) >> 8;
        int b = color & 0xFF;
        final float portalScale = 2.5f;
        float time = 0.0f;

        shader.getUniform("time").set(time);
        shader.getUniform("theColorR").set(r / 255f);
        shader.getUniform("theColorG").set(g / 255f);
        shader.getUniform("theColorB").set(b / 255f);
        shader.getUniform("closeAlpha").set(portal.isVisible() ? 0f : 1f);

        VertexConsumerProvider.Immediate consumers = MinecraftClient.getInstance().getBufferBuilders()
                .getEntityVertexConsumers();

        // PASS 0
        // RenderSystem.depthMask(false);
        // RenderSystem.disableDepthTest();
        shader.getUniform("pass").set(0);
        drawPlane(matrices, consumers, getTexture(portal), portalScale, 15728880, r, g, b, 255);
        flush(consumers);

        // PASS 1 (glow)
        shader.getUniform("pass").set(1);
        drawPlane(matrices, consumers, getTexture(portal), portalScale, 15728880, r, g, b, 255);
        flush(consumers);

        matrices.pop();
    }

    private static Identifier getTexture(Portal entity) {
        return entity.isVisible() ? portalOpen : portalClosed;
    }

    private static void flush(VertexConsumerProvider bufferSource) {
        if (bufferSource instanceof VertexConsumerProvider.Immediate immediate) {
            immediate.draw();
        }
    }

    private static void drawPlane(MatrixStack matrixStack, VertexConsumerProvider bufferSource, Identifier texture,
            float size,
            int light, int r, int g, int b, int a) {
        VertexConsumer vc = bufferSource.getBuffer(ApertureRenderLayers.getPortalLayer(texture));
        MatrixStack.Entry entry = matrixStack.peek();
        Matrix4f matrix = entry.getPositionMatrix();
        Matrix3f normalMatrix = entry.getNormalMatrix();
        float half = 0.5F * size;
        vc.vertex(matrix, -half, -half, 0.0F).color(r, g, b, a).texture(0.0F, 0.0F).overlay(OverlayTexture.DEFAULT_UV)
                .light(light).normal(normalMatrix, 0, 0, 1).next();
        vc.vertex(matrix, half, -half, 0.0F).color(r, g, b, a).texture(1.0F, 0.0F).overlay(OverlayTexture.DEFAULT_UV)
                .light(light).normal(normalMatrix, 0, 0, 1).next();
        vc.vertex(matrix, half, half, 0.0F).color(r, g, b, a).texture(1.0F, 1.0F).overlay(OverlayTexture.DEFAULT_UV)
                .light(light).normal(normalMatrix, 0, 0, 1).next();
        vc.vertex(matrix, -half, half, 0.0F).color(r, g, b, a).texture(0.0F, 1.0F).overlay(OverlayTexture.DEFAULT_UV)
                .light(light).normal(normalMatrix, 0, 0, 1).next();
    }
}