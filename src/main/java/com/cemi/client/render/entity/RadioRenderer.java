package com.cemi.client.render.entity;

import com.cemi.client.render.entity.model.RadioModel;
import com.cemi.entity.RadioEntity;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RadioRenderer extends GeoEntityRenderer<RadioEntity> {

    public RadioRenderer(Context renderManager) {
        super(renderManager, new RadioModel());
    }

}
