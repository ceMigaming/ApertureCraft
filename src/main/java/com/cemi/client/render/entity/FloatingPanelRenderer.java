package com.cemi.client.render.entity;

import com.cemi.client.render.entity.model.FloatingPanelModel;
import com.cemi.entity.FloatingPanelEntity;

import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FloatingPanelRenderer extends GeoEntityRenderer<FloatingPanelEntity> {

    public FloatingPanelRenderer(Context renderManager) {
        super(renderManager, new FloatingPanelModel());
    }

}
