package com.cemi.client.render;

import com.cemi.block.entity.ApertureBlockEntities;
import com.cemi.client.render.block.CubeDropperEntityRenderer;
import com.cemi.client.render.block.DoorBlockEntityRenderer;
import com.cemi.client.render.block.FloorButtonBlockEntityRenderer;
import com.cemi.client.render.block.HEPLauncherBlockEntityRenderer;
import com.cemi.client.render.block.PanelRailCornerEntityRenderer;
import com.cemi.client.render.block.PanelRailEntityRenderer;
import com.cemi.client.render.block.PanelRailSupportEntityRenderer;
import com.cemi.client.render.entity.CompanionCubeRenderer;
import com.cemi.client.render.entity.CustomPortalEntityRenderer;
import com.cemi.client.render.entity.FloatingPanelRenderer;
import com.cemi.client.render.entity.GhostBlockRenderer;
import com.cemi.client.render.entity.HighEnergyPelletRenderer;
import com.cemi.client.render.entity.OldStorageCubeRenderer;
import com.cemi.client.render.entity.PortalProjectileRenderer;
import com.cemi.client.render.entity.RadioRenderer;
import com.cemi.client.render.entity.RocketTurretRenderer;
import com.cemi.client.render.entity.RustyStorageCubeRenderer;
import com.cemi.client.render.entity.StorageCubeRenderer;
import com.cemi.client.render.entity.TurretRenderer;
import com.cemi.client.render.entity.model.PortalOverlayModel;
import com.cemi.entity.ApertureEntities;

import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

@SuppressWarnings("deprecation")
public class ApertureRenderers {

        public static void registerRenderers() {
                EntityRendererRegistry.register(ApertureEntities.APERTURE_PORTAL,
                                CustomPortalEntityRenderer::new);
                EntityRendererRegistry.register(ApertureEntities.GHOSTBLOCK, GhostBlockRenderer::new);
                EntityRendererRegistry.register(ApertureEntities.HIGH_ENERGY_PELLET,
                                HighEnergyPelletRenderer::new);
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
                EntityRendererRegistry.register(ApertureEntities.FLOATING_PANEL, FloatingPanelRenderer::new);

                // Block Entities
                BlockEntityRendererRegistry.register(ApertureBlockEntities.HEP_LAUNCHER,
                                HEPLauncherBlockEntityRenderer::new);
                BlockEntityRendererRegistry.register(ApertureBlockEntities.DOOR,
                                DoorBlockEntityRenderer::new);
                BlockEntityRendererRegistry.register(ApertureBlockEntities.FLOOR_BUTTON,
                                FloorButtonBlockEntityRenderer::new);
                BlockEntityRendererRegistry.register(ApertureBlockEntities.CUBE_DROPPER_BLOCK_ENTITY,
                                CubeDropperEntityRenderer::new);
                BlockEntityRendererRegistry.register(ApertureBlockEntities.PANEL_RAIL_BLOCK_ENTITY,
                                PanelRailEntityRenderer::new);
                BlockEntityRendererRegistry.register(ApertureBlockEntities.PANEL_RAIL_CORNER_BLOCK_ENTITY,
                                PanelRailCornerEntityRenderer::new);
                BlockEntityRendererRegistry.register(ApertureBlockEntities.PANEL_RAIL_SUPPORT_BLOCK_ENTITY,
                                PanelRailSupportEntityRenderer::new);

                // Render Layers
                EntityModelLayerRegistry.registerModelLayer(CustomPortalEntityRenderer.OVERLAY_MODEL_LAYER,
                                PortalOverlayModel::getTexturedModelData);
        }

}
