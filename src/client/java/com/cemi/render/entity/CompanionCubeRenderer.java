package com.cemi.render.entity;

import com.cemi.entity.StorageCubeEntity;
import com.cemi.render.entity.model.CompanionCubeModel;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CompanionCubeRenderer extends GeoEntityRenderer<StorageCubeEntity> {

    public CompanionCubeRenderer(Context renderManager) {
        super(renderManager, new CompanionCubeModel());
    }

}
