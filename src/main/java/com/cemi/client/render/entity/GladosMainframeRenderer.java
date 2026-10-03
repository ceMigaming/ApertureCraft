package com.cemi.client.render.entity;

import com.cemi.client.render.entity.model.GladosMainframeModel;
import com.cemi.entity.GladosMainframeEntity;

import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class GladosMainframeRenderer extends GeoEntityRenderer<GladosMainframeEntity> {

    public GladosMainframeRenderer(Context renderManager) {
        super(renderManager, new GladosMainframeModel());
    }

}
