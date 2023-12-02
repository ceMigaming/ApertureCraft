package com.cemi.render.entity;

import com.cemi.entity.TurretEntity;
import com.cemi.render.entity.model.TurretModel;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class TurretRenderer extends GeoEntityRenderer<TurretEntity> {

    public TurretRenderer(Context renderManager) {
        super(renderManager, new TurretModel());
    }

}
