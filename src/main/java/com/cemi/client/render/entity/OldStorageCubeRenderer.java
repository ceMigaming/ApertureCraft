package com.cemi.client.render.entity;

import com.cemi.client.render.entity.model.OldStorageCubeModel;
import com.cemi.entity.StorageCubeEntity;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class OldStorageCubeRenderer extends GeoEntityRenderer<StorageCubeEntity> {

    public OldStorageCubeRenderer(Context renderManager) {
        super(renderManager, new OldStorageCubeModel());
    }

}
