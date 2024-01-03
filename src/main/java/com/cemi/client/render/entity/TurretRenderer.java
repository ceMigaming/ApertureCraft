package com.cemi.client.render.entity;

import com.cemi.client.render.entity.model.TurretModel;
import com.cemi.entity.TurretEntity;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class TurretRenderer extends GeoEntityRenderer<TurretEntity> {

    public TurretRenderer(Context renderManager) {
        super(renderManager, new TurretModel());
    }

}
