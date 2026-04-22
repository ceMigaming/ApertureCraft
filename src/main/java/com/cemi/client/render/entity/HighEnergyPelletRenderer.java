package com.cemi.client.render.entity;

import org.joml.Matrix4f;

import com.cemi.ApertureCraft;
import com.cemi.client.render.model.HighEnergyPelletModel;
import com.cemi.entity.HighEnergyPelletEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class HighEnergyPelletRenderer extends EntityRenderer<HighEnergyPelletEntity> {

    protected HighEnergyPelletModel model;

    public HighEnergyPelletRenderer(Context ctx) {
        super(ctx);
        model = new HighEnergyPelletModel(HighEnergyPelletModel.getTexturedModelData().createModel());
    }

    @Override
    public Identifier getTexture(HighEnergyPelletEntity entity) {
        return new Identifier(ApertureCraft.MOD_ID, "textures/entity/hep.png");
    }

    public static void renderSprite(WorldRenderContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientWorld world = client.world;

        if (world == null)
            return;

        Camera camera = context.camera();
        Vec3d camPos = camera.getPos();

        MatrixStack matrices = context.matrixStack();

        matrices.translate(-camPos.x, -camPos.y, -camPos.z);

        for (Entity entity : world.getEntities()) {
            if (!(entity instanceof HighEnergyPelletEntity pellet))
                continue;

            renderPellet(pellet, context);
        }

    }

    private static void renderPellet(HighEnergyPelletEntity entity, WorldRenderContext context) {

        float lifeTime = entity.getLifeTime() / 240.0f; // 12 * 20

        MatrixStack matrices = context.matrixStack();

        matrices.push();

        float tickDelta = context.tickDelta();

        double x = MathHelper.lerp(tickDelta, entity.prevX, entity.getX());
        double y = MathHelper.lerp(tickDelta, entity.prevY, entity.getY());
        double z = MathHelper.lerp(tickDelta, entity.prevZ, entity.getZ());

        matrices.translate(x, y + 0.25, z);

        // Billboard
        matrices.multiply(context.camera().getRotation().rotateY((float) Math.PI));

        Matrix4f matrix = matrices.peek().getPositionMatrix();

        matrix.rotateZ(lifeTime*20);

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE);
        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
        RenderSystem.setShaderTexture(0, new Identifier(ApertureCraft.MOD_ID, "textures/entity/hep.png"));
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);

        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

        float size = 0.25f + (float) Math.sin(lifeTime * 500) * 0.015f;

        float alpha = 1.0f - (float)Math.exp(-(lifeTime) * 5);
        float r = 1.0f;
        float g = (float) Math.cos(lifeTime * 100) * 0.1f + 0.9f;
        float b = (float) Math.sin(lifeTime * 100) * 0.1f + 0.9f;

        buffer.vertex(matrix, -size, -size, 0).texture(0, 1).color(r*alpha, g*alpha, b*alpha, 1.0f).next();
        buffer.vertex(matrix, size, -size, 0).texture(1, 1).color(r*alpha, g*alpha, b*alpha, 1.0f).next();
        buffer.vertex(matrix, size, size, 0).texture(1, 0).color(r*alpha, g*alpha, b*alpha, 1.0f).next();
        buffer.vertex(matrix, -size, size, 0).texture(0, 0).color(r*alpha, g*alpha, b*alpha, 1.0f).next();

        BufferRenderer.drawWithGlobalProgram(buffer.end());
        RenderSystem.defaultBlendFunc();

        RenderSystem.disableBlend();
        RenderSystem.enableCull();
        RenderSystem.depthMask(true);

        matrices.pop();
    }
}
