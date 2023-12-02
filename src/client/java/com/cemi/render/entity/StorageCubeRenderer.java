package com.cemi.render.entity;

import com.cemi.entity.StorageCubeEntity;
import com.cemi.render.entity.model.StorageCubeModel;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class StorageCubeRenderer extends GeoEntityRenderer<StorageCubeEntity> {

    public StorageCubeRenderer(Context renderManager) {
        super(renderManager, new StorageCubeModel());
    }

}
