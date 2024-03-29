package com.cemi.client.render;

import com.cemi.block.ApertureBlocks;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;

public class ApertureRenderLayers {
    public static void registerRenderLayers() {
        BlockRenderLayerMap.INSTANCE.putBlock(ApertureBlocks.INDICATOR_LIGHT,
                RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ApertureBlocks.GLASS_WALL,
                RenderLayer.getTranslucent());
    }
}
