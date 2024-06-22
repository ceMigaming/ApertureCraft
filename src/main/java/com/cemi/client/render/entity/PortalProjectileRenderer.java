package com.cemi.client.render.entity;

import com.cemi.ApertureCraft;
import com.cemi.client.render.entity.model.GhostBlockModel;
import com.cemi.client.render.entity.model.PortalProjectileModel;
import com.cemi.entity.PortalProjectileEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class PortalProjectileRenderer extends EntityRenderer<PortalProjectileEntity> {

    protected PortalProjectileModel model;

    public PortalProjectileRenderer(Context ctx) {
        super(ctx);
        model = new PortalProjectileModel(GhostBlockModel.getTexturedModelData().createModel());
    }

    @Override
    public Identifier getTexture(PortalProjectileEntity entity) {
        return new Identifier(ApertureCraft.MOD_ID, "textures/entity/ghostblock.png");
    }

    @Override
    public void render(PortalProjectileEntity entity, float yaw, float tickDelta,
            MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {

        int r = entity.getColor() >> 16 & 255;
        int g = entity.getColor() >> 8 & 255;
        int b = entity.getColor() & 255;


        model.render(matrices,
                vertexConsumers
                        .getBuffer(RenderLayer.getEntityTranslucent(this.getTexture(entity))),
                light, OverlayTexture.DEFAULT_UV, r, g, b, 0.5F);
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }
}
