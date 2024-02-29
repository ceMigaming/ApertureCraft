package com.cemi.client.render.entity;

import com.cemi.ApertureCraft;
import com.cemi.client.render.entity.model.GhostBlockModel;
import com.cemi.entity.GhostBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class GhostBlockRenderer extends EntityRenderer<GhostBlockEntity> {

    protected GhostBlockModel model;

    public GhostBlockRenderer(Context ctx) {
        super(ctx);
        model = new GhostBlockModel(GhostBlockModel.getTexturedModelData().createModel());
    }

    @Override
    public Identifier getTexture(GhostBlockEntity entity) {
        return new Identifier(ApertureCraft.MOD_ID, "textures/entity/ghostblock.png");
    }

    @Override
    public void render(GhostBlockEntity entity, float yaw, float tickDelta, MatrixStack matrices,
            VertexConsumerProvider vertexConsumers, int light) {
        int lifeTime = entity.getLifeTime();
        // TODO find a better way to do this
        RenderLayer renderLayer = RenderLayer.getOutline(getTexture(entity));
        if (renderLayer != null) {
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(renderLayer);
            if ((lifeTime / 10) % 2 == 0)
                model.render(matrices, vertexConsumer, light, 0, 1.0f, .5f, .5f, 0.5f);
        }
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }
}
