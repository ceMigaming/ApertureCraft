package com.cemi.client.render.entity;

import com.cemi.client.render.entity.model.CameraModel;
import com.cemi.entity.CameraEntity;

import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CameraRenderer extends GeoEntityRenderer<CameraEntity> {

    public CameraRenderer(Context renderManager) {
        super(renderManager, new CameraModel());
    }

}
