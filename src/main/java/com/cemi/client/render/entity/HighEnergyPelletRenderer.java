package com.cemi.client.render.entity;

import com.cemi.ApertureCraft;
import com.cemi.client.render.model.HighEnergyPelletModel;
import com.cemi.entity.HighEnergyPelletEntity;
import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.fabric.api.renderer.v1.material.MaterialFinder;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class HighEnergyPelletRenderer extends EntityRenderer<HighEnergyPelletEntity> {

    protected HighEnergyPelletModel model;

    public HighEnergyPelletRenderer(Context ctx) {
        super(ctx);
        model = new HighEnergyPelletModel(HighEnergyPelletModel.getTexturedModelData().createModel());
    }

    @Override
    public Identifier getTexture(HighEnergyPelletEntity entity) {
        return Identifier.of(ApertureCraft.MOD_ID, "textures/entity/hep.png");
    }

    @Override
    public void render(HighEnergyPelletEntity entity, float yaw, float tickDelta,
            MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        float lifeTime = entity.getLifeTime() / 240.0f; // 12 * 20
        // TODO find a better way to do this
        RenderLayer renderLayer = RenderLayer.getEntityTranslucentCull(getTexture(entity));
        if (renderLayer != null) {
            matrices.translate(0.0f, -0.5f, 0.0f);
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(renderLayer);
            matrices.scale(0.5f, 0.5f, 0.5f);
            matrices.push();
            
            matrices.multiply(this.dispatcher.getRotation());
            matrices.translate(0.0f, -0.75f, 0.0f);
            // model.render(matrices, vertexConsumer, LightmapTextureManager.pack(15, 15), 0, 1.0f, 1.0f, 1.0f,
            //         MathHelper.clamp(1.0f + (float) Math.log10(lifeTime), 0.5f, 1.0f));
            model.render(matrices, vertexConsumer, LightmapTextureManager.pack(15, 15), 0, 0xFFFFFFFF);

            matrices.pop();
        }
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }
}
