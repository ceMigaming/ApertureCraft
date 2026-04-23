package com.cemi.client.render.entity;

import com.cemi.client.render.entity.model.RocketModel;
import com.cemi.entity.RocketEntity;

import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RocketRenderer extends GeoEntityRenderer<RocketEntity> {

    public RocketRenderer(Context renderManager) {
        super(renderManager, new RocketModel());
    }

}
