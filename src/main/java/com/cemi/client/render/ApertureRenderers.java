package com.cemi.client.render;

import com.cemi.client.render.entity.CompanionCubeRenderer;
import com.cemi.client.render.entity.CustomPortalEntityRenderer;
import com.cemi.client.render.entity.GhostBlockRenderer;
import com.cemi.client.render.entity.OldStorageCubeRenderer;
import com.cemi.client.render.entity.PortalProjectileRenderer;
import com.cemi.client.render.entity.RadioRenderer;
import com.cemi.client.render.entity.RocketTurretRenderer;
import com.cemi.client.render.entity.RustyStorageCubeRenderer;
import com.cemi.client.render.entity.StorageCubeRenderer;
import com.cemi.client.render.entity.TurretRenderer;
import com.cemi.client.render.entity.model.PortalOverlayModel;
import com.cemi.entity.ApertureEntities;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class ApertureRenderers {

    public static void registerRenderers() {
        EntityRendererRegistry.register(ApertureEntities.APERTURE_PORTAL,
                CustomPortalEntityRenderer::new);
        EntityRendererRegistry.register(ApertureEntities.GHOSTBLOCK, GhostBlockRenderer::new);
        EntityRendererRegistry.register(ApertureEntities.PORTAL_PROJECTILE,
                PortalProjectileRenderer::new);
        EntityRendererRegistry.register(ApertureEntities.RUSTY_STORAGE_CUBE,
                RustyStorageCubeRenderer::new);
        EntityRendererRegistry.register(ApertureEntities.STORAGE_CUBE, StorageCubeRenderer::new);
        EntityRendererRegistry.register(ApertureEntities.OLD_STORAGE_CUBE,
                OldStorageCubeRenderer::new);
        EntityRendererRegistry.register(ApertureEntities.COMPANION_CUBE,
                CompanionCubeRenderer::new);
        EntityRendererRegistry.register(ApertureEntities.RADIO, RadioRenderer::new);
        EntityRendererRegistry.register(ApertureEntities.TURRET, TurretRenderer::new);
        EntityRendererRegistry.register(ApertureEntities.ROCKET_TURRET, RocketTurretRenderer::new);

        // Render Layers
        EntityModelLayerRegistry.registerModelLayer(CustomPortalEntityRenderer.OVERLAY_MODEL_LAYER,
                PortalOverlayModel::getTexturedModelData);
    }

}
