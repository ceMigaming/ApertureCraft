package com.cemi.render;

import com.cemi.entity.ApertureEntities;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class ApertureRenderers {

    public static void registerRenderers() {
        EntityRendererRegistry.register(ApertureEntities.GHOSTBLOCK, GhostBlockRenderer::new);
        EntityRendererRegistry.register(ApertureEntities.STORAGE_CUBE, StorageCubeRenderer::new);
    }

}
