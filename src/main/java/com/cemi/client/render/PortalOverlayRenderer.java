package com.cemi.client.render;

import java.util.List;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

import com.cemi.ApertureCraft;
import com.cemi.entity.AperturePortal;
import com.cemi.util.ShaderHelper;
import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import qouteall.imm_ptl.core.portal.Portal;
import qouteall.imm_ptl.core.render.context_management.PortalRendering;

public class PortalOverlayRenderer {

    private static float time = 0;

    private static Identifier portalClosed = new Identifier(ApertureCraft.MOD_ID, "textures/entity/portalclose.png");
    private static Identifier portalOpen = new Identifier(ApertureCraft.MOD_ID, "textures/entity/portalopen.png");
    private static Identifier portalOutline = new Identifier(ApertureCraft.MOD_ID, "textures/entity/portaloutline.png");
    private static Identifier portalOutlineFancy = new Identifier(ApertureCraft.MOD_ID,
            "textures/entity/portaloutlinefancy.png");

    public static void init() {
        WorldRenderEvents.AFTER_TRANSLUCENT.register(context -> {
            PortalOverlayRenderer.render(context);
        });
    }

    public static void render(WorldRenderContext context) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.world == null || client.cameraEntity == null)
            return;

        float tickDelta = context.tickDelta();
        time += tickDelta * 0.05f;

        Camera camera = context.camera();
        Vec3d camPos = camera.getPos();

        MatrixStack matrices = context.matrixStack();
        VertexConsumerProvider.Immediate consumers = client.getBufferBuilders().getEntityVertexConsumers();

        matrices.push();

        // VERY IMPORTANT: translate to camera-relative coords
        matrices.translate(-camPos.x, -camPos.y, -camPos.z);

        for (AperturePortal portal : getPortals(client)) {
            renderPortalOverlay(portal, matrices, consumers, tickDelta);
        }

        matrices.pop();

        // consumers.draw(); // flush once at end
    }

    private static List<AperturePortal> getPortals(MinecraftClient client) {
        return client.world.getEntitiesByClass(
                AperturePortal.class,
                client.cameraEntity.getBoundingBox().expand(64), // render distance
                e -> true);
    }

    private static void renderPortalOverlay(
            AperturePortal portal,
            MatrixStack matrices,
            VertexConsumerProvider consumers,
            float tickDelta) {

        matrices.push();
        RenderSystem.enablePolygonOffset();
        RenderSystem.polygonOffset(-1.0f, -10.0f);

        // Move to portal position
        Vec3d pos = portal.getPos();
        matrices.translate(pos.x, pos.y, pos.z);

        // Rotate like the portal
        matrices.multiply(
                portal.getOrientationRotation().toMcQuaternion().rotateY(portal.isVisible() ? (float) Math.PI : 0.0f));
        matrices.translate(0, 0, 0.01);

        // Scale
        float scale = 1.0f;
        matrices.scale(scale, scale * 2.0f, 1.0f);

        // Shader
        ShaderProgram shader = ShaderHelper.getPortalShader();
        if (shader == null) {
            matrices.pop();
            return;
        }

        int color = portal.isVisible() ? portal.getOtherColor() : portal.getColor();
        int r = (color & 0xFF0000) >> 16;
        int g = (color & 0xFF00) >> 8;
        int b = color & 0xFF;
        final float portalScale = 2.5f;

        shader.getUniform("time").set(time);
        shader.getUniform("theColorR").set(r / 255f);
        shader.getUniform("theColorG").set(g / 255f);
        shader.getUniform("theColorB").set(b / 255f);
        shader.getUniform("closeAlpha").set(portal.isVisible() ? 0f : 1f);

        // PASS 0
        RenderSystem.depthMask(false);
        // RenderSystem.disableDepthTest();
        shader.getUniform("pass").set(0);
        drawPlane(matrices, consumers, getTexture(portal), portalScale, 15728880, r, g, b, 255);
        flush(consumers);

        // PASS 1 (glow)
        shader.getUniform("pass").set(1);
        drawPlane(matrices, consumers, getTexture(portal), portalScale, 15728880, r, g, b, 255);
        flush(consumers);
        RenderSystem.depthMask(true);
        // RenderSystem.enableDepthTest();

        RenderSystem.disablePolygonOffset();

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