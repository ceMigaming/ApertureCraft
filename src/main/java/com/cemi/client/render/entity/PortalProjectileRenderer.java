package com.cemi.client.render.entity;

import com.cemi.ApertureCraft;
import com.cemi.client.render.entity.model.GhostBlockModel;
import com.cemi.client.render.entity.model.PortalProjectileModel;
import com.cemi.entity.PortalProjectileEntity;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
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
}
