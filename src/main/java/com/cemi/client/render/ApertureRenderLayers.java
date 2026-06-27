package com.cemi.client.render;

import com.cemi.block.ApertureBlocks;
import com.cemi.util.ShaderHelper;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

public class ApertureRenderLayers {

    public static final Identifier LASER_TEXTURE = new Identifier("minecraft", "textures/misc/white.png");

    public static void registerRenderLayers() {
        BlockRenderLayerMap.INSTANCE.putBlock(ApertureBlocks.INDICATOR_LIGHT,
                RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ApertureBlocks.GLASS_PANE,
                RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ApertureBlocks.GLASS_PANE_SEEMED,
                RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ApertureBlocks.GLASS_BLOCK,
                RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ApertureBlocks.GLASS_BLOCK_SEEMED,
                RenderLayer.getTranslucent());
    }

    public static RenderLayer getPortalLayer(Identifier texture) {
        return getPortalLayer(texture, false);
    }

    public static RenderLayer getPortalLayer(Identifier texture, boolean ignoreDepth) {
        return RenderLayer.of(
                "portal_layer",
                VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
                VertexFormat.DrawMode.QUADS,
                256,
                false,
                true,
                RenderLayer.MultiPhaseParameters.builder()
                        .program(new RenderPhase.ShaderProgram(
                                () -> ShaderHelper.getPortalShader()))
                        .texture(new RenderPhase.Texture(texture, false, false))
                        .transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY)
                        .depthTest(ignoreDepth ? RenderPhase.ALWAYS_DEPTH_TEST : RenderPhase.LEQUAL_DEPTH_TEST)
                        .writeMaskState(RenderPhase.COLOR_MASK)
                        .cull(RenderPhase.DISABLE_CULLING)
                        .lightmap(RenderPhase.ENABLE_LIGHTMAP)
                        .overlay(RenderPhase.ENABLE_OVERLAY_COLOR)
                        .build(true));
    }

    public static final RenderLayer LASER = RenderLayer.of(
            "laser",
            VertexFormats.POSITION_COLOR_TEXTURE,
            VertexFormat.DrawMode.QUADS,
            256,
            false,
            true,
            RenderLayer.MultiPhaseParameters.builder()
                    .program(RenderPhase.POSITION_COLOR_TEXTURE_PROGRAM)
                    .texture(new RenderPhase.Texture(LASER_TEXTURE, false, false))
                    .transparency(RenderPhase.ADDITIVE_TRANSPARENCY)
                    .cull(RenderPhase.DISABLE_CULLING)
                    .layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
                    .depthTest(RenderPhase.ALWAYS_DEPTH_TEST)
                    .writeMaskState(RenderPhase.COLOR_MASK)
                    .build(false));
}
