package com.cemi.render;

import com.cemi.entity.StorageCubeEntity;
import com.cemi.render.model.StorageCubeModel;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class StorageCubeRenderer extends GeoEntityRenderer<StorageCubeEntity> {

    public StorageCubeRenderer(Context renderManager) {
        super(renderManager, new StorageCubeModel());
    }

}
