package com.cemi.render.entity;

import com.cemi.entity.RocketTurretEntity;
import com.cemi.render.entity.model.RocketTurretModel;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RocketTurretRenderer extends GeoEntityRenderer<RocketTurretEntity> {

    public RocketTurretRenderer(Context renderManager) {
        super(renderManager, new RocketTurretModel());
    }

}
