package com.cemi.client.render.entity;

import com.cemi.client.render.entity.model.CompanionCubeModel;
import com.cemi.entity.StorageCubeEntity;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CompanionCubeRenderer extends GeoEntityRenderer<StorageCubeEntity> {

    public CompanionCubeRenderer(Context renderManager) {
        super(renderManager, new CompanionCubeModel());
    }

}
