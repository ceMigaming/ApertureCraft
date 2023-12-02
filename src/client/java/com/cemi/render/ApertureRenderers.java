package com.cemi.render;

import com.cemi.entity.ApertureEntities;
import com.cemi.render.entity.CompanionCubeRenderer;
import com.cemi.render.entity.GhostBlockRenderer;
import com.cemi.render.entity.OldStorageCubeRenderer;
import com.cemi.render.entity.RadioRenderer;
import com.cemi.render.entity.RustyStorageCubeRenderer;
import com.cemi.render.entity.StorageCubeRenderer;
import com.cemi.render.entity.TurretRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class ApertureRenderers {

    public static void registerRenderers() {
        EntityRendererRegistry.register(ApertureEntities.GHOSTBLOCK, GhostBlockRenderer::new);
        EntityRendererRegistry.register(ApertureEntities.RUSTY_STORAGE_CUBE,
                RustyStorageCubeRenderer::new);
        EntityRendererRegistry.register(ApertureEntities.STORAGE_CUBE, StorageCubeRenderer::new);
        EntityRendererRegistry.register(ApertureEntities.OLD_STORAGE_CUBE,
                OldStorageCubeRenderer::new);
        EntityRendererRegistry.register(ApertureEntities.COMPANION_CUBE,
                CompanionCubeRenderer::new);
        EntityRendererRegistry.register(ApertureEntities.RADIO, RadioRenderer::new);
        EntityRendererRegistry.register(ApertureEntities.TURRET, TurretRenderer::new);
    }

}
