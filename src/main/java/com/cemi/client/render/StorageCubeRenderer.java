package com.cemi.client.render;

import com.cemi.client.render.model.StorageCubeModel;
import com.cemi.entity.StorageCubeEntity;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class StorageCubeRenderer extends GeoEntityRenderer<StorageCubeEntity> {

    public StorageCubeRenderer(Context renderManager) {
        super(renderManager, new StorageCubeModel());
    }

}
