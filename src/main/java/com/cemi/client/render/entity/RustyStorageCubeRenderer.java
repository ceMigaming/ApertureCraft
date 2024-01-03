package com.cemi.client.render.entity;

import com.cemi.client.render.entity.model.RustyStorageCubeModel;
import com.cemi.entity.StorageCubeEntity;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RustyStorageCubeRenderer extends GeoEntityRenderer<StorageCubeEntity> {

    public RustyStorageCubeRenderer(Context renderManager) {
        super(renderManager, new RustyStorageCubeModel());
    }

}
