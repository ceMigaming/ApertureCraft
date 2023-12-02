package com.cemi.render.entity;

import com.cemi.entity.StorageCubeEntity;
import com.cemi.render.entity.model.RustyStorageCubeModel;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RustyStorageCubeRenderer extends GeoEntityRenderer<StorageCubeEntity> {

    public RustyStorageCubeRenderer(Context renderManager) {
        super(renderManager, new RustyStorageCubeModel());
    }

}
