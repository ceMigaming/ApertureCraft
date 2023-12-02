package com.cemi.render.entity;

import com.cemi.entity.StorageCubeEntity;
import com.cemi.render.entity.model.OldStorageCubeModel;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class OldStorageCubeRenderer extends GeoEntityRenderer<StorageCubeEntity> {

    public OldStorageCubeRenderer(Context renderManager) {
        super(renderManager, new OldStorageCubeModel());
    }

}
