package com.cemi.client.render;

import java.util.ArrayList;
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
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import qouteall.imm_ptl.core.portal.Portal;
import qouteall.imm_ptl.core.render.PortalRenderable;
import qouteall.imm_ptl.core.render.renderer.PortalRenderer;
import qouteall.imm_ptl.core.render.context_management.PortalRendering;

public class PortalOverlayRenderer {

    private static float time = 0;
    private static final VertexConsumerProvider.Immediate OVERLAY_CONSUMERS =
            VertexConsumerProvider.immediate(new BufferBuilder(1024));

    private static Identifier portalClosed = new Identifier(ApertureCraft.MOD_ID, "textures/entity/portalclose.png");
    private static Identifier portalOpen = new Identifier(ApertureCraft.MOD_ID, "textures/entity/portalopen.png");
    private static Identifier portalOutline = new Identifier(ApertureCraft.MOD_ID, "textures/entity/portaloutline.png");
    private static Identifier portalOutlineFancy = new Identifier(ApertureCraft.MOD_ID,
            "textures/entity/portaloutlinefancy.png");

    public static void render(WorldRenderContext context) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.world == null || client.cameraEntity == null)
            return;

        if (PortalRendering.isRendering())
            return;

        float tickDelta = context.tickDelta();
        time += tickDelta * 0.05f;

        Camera camera = context.camera();
        Vec3d camPos = camera.getPos();

        MatrixStack matrices = context.matrixStack();
        matrices.push();
        try {
            // VERY IMPORTANT: translate to camera-relative coords
            matrices.translate(-camPos.x, -camPos.y, -camPos.z);

            boolean wasStencilEnabled = GL11.glIsEnabled(GL11.GL_STENCIL_TEST);
            if (wasStencilEnabled) {
                GL11.glDisable(GL11.GL_STENCIL_TEST);
            }
            GL11.glColorMask(true, true, true, true);

            try {
                for (AperturePortal portal : getPortals(client, camPos)) {
                    if (!portal.isVisible()) {
                        renderPortalOverlay(portal, matrices, OVERLAY_CONSUMERS, tickDelta);
                    }
                }
            } finally {
                if (wasStencilEnabled) {
                    GL11.glEnable(GL11.GL_STENCIL_TEST);
                }
            }
        } finally {
            matrices.pop();
        }

        // consumers.draw(); // flush once at end
    }

    public static void renderAfterImmersivePortal(PortalRenderable portalRenderable, MatrixStack matrices) {
        if (PortalRendering.isRendering()) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.cameraEntity == null) {
            return;
        }

        Vec3d camPos = client.gameRenderer.getCamera().getPos();

        matrices.push();
        try {
            matrices.translate(-camPos.x, -camPos.y, -camPos.z);

            boolean wasStencilEnabled = GL11.glIsEnabled(GL11.GL_STENCIL_TEST);
            if (wasStencilEnabled) {
                GL11.glDisable(GL11.GL_STENCIL_TEST);
            }
            GL11.glColorMask(true, true, true, true);

            try {
                if (portalRenderable instanceof AperturePortal portal && portal.isVisible()) {
                    renderPortalOverlay(portal, matrices, OVERLAY_CONSUMERS, 0.0f);
                } else if (portalRenderable instanceof PortalRenderer.PortalGroupToRender groupToRender) {
                    for (Portal portal : groupToRender.portals()) {
                        if (portal instanceof AperturePortal aperturePortal && aperturePortal.isVisible()) {
                            renderPortalOverlay(aperturePortal, matrices, OVERLAY_CONSUMERS, 0.0f);
                        }
                    }
                }
            } finally {
                if (wasStencilEnabled) {
                    GL11.glEnable(GL11.GL_STENCIL_TEST);
                }
            }
        } finally {
            matrices.pop();
        }
    }

    private static List<AperturePortal> getPortals(MinecraftClient client, Vec3d camPos) {
        List<AperturePortal> portals = new ArrayList<>();
        double maxDistanceSquared = 64.0 * 64.0;

        client.world.getEntities().forEach(entity -> {
            if (entity instanceof AperturePortal portal
                    && portal.getPos().squaredDistanceTo(camPos) <= maxDistanceSquared) {
                portals.add(portal);
            }
        });

        return portals;
    }

    private static void renderPortalOverlay(
            AperturePortal portal,
            MatrixStack matrices,
            VertexConsumerProvider consumers,
            float tickDelta) {

        matrices.push();
        RenderSystem.enablePolygonOffset();
        RenderSystem.polygonOffset(-1.0f, -10.0f);

        try {
            // Move to portal position
            Vec3d pos = portal.getPos();
            matrices.translate(pos.x, pos.y, pos.z);

            // Rotate like the portal
            matrices.multiply(portal.getOrientationRotation().toMcQuaternion());
            matrices.translate(0, 0, 0.01);

            // Scale
            float scale = 1.0f;
            matrices.scale(scale, scale * 2.0f, 1.0f);

            // Shader
            ShaderProgram shader = ShaderHelper.getPortalShader();
            if (shader == null) {
                return;
            }

            int color = portal.getColor();
            int r = (color & 0xFF0000) >> 16;
            int g = (color & 0xFF00) >> 8;
            int b = color & 0xFF;
            final float portalScale = 2.5f;

            shader.getUniform("time").set(time);
            shader.getUniform("theColorR").set(r / 255f);
            shader.getUniform("theColorG").set(g / 255f);
            shader.getUniform("theColorB").set(b / 255f);
            shader.getUniform("closeAlpha").set(portal.isVisible() ? 0f : 1f);

            RenderSystem.depthMask(false);
            try {
                // PASS 0
                // RenderSystem.disableDepthTest();
                shader.getUniform("pass").set(0);
                drawPlane(matrices, consumers, getTexture(portal), portal.isVisible(), portalScale, 15728880, r, g, b, 255);
                flush(consumers);

                // PASS 1 (glow)
                shader.getUniform("pass").set(1);
                drawPlane(matrices, consumers, getTexture(portal), portal.isVisible(), portalScale, 15728880, r, g, b, 255);
                flush(consumers);
                // RenderSystem.enableDepthTest();
            } finally {
                RenderSystem.depthMask(true);
            }
        } finally {
            RenderSystem.disablePolygonOffset();
            matrices.pop();
        }
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
            boolean ignoreDepth,
            float size,
            int light, int r, int g, int b, int a) {
        VertexConsumer vc = bufferSource.getBuffer(ApertureRenderLayers.getPortalLayer(texture, ignoreDepth));
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
