package com.cemi.client.render.entity;

import com.cemi.ApertureCraft;
import com.cemi.client.render.entity.model.GhostBlockModel;
import com.cemi.entity.HighEnergyPelletEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class HighEnergyPelletRenderer extends EntityRenderer<HighEnergyPelletEntity> {

    protected GhostBlockModel model;

    public HighEnergyPelletRenderer(Context ctx) {
        super(ctx);
        model = new GhostBlockModel(GhostBlockModel.getTexturedModelData().createModel());
    }

    @Override
    public Identifier getTexture(HighEnergyPelletEntity entity) {
        return new Identifier(ApertureCraft.MOD_ID, "textures/entity/ghostblock.png");
    }

    @Override
    public void render(HighEnergyPelletEntity entity, float yaw, float tickDelta,
            MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        float lifeTime = entity.getLifeTime() / 240.0f; // 12 * 20
        // TODO find a better way to do this
        RenderLayer renderLayer = RenderLayer.getEntityTranslucentEmissive(getTexture(entity));
        if (renderLayer != null) {
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(renderLayer);
            model.render(matrices, vertexConsumer, light, 0, 1.0f, 1.0f, 0.0f,
                    MathHelper.clamp(1.0f + (float) Math.log10(lifeTime), 0.0f, 0.5f));
        }
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }
}
