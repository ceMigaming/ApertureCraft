package com.cemi.client.render;

import com.cemi.block.ApertureBlocks;
import com.cemi.block.IndicatorLightBlock;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;

public class ApertureColorProviders {

    public static void registerColorProviders() {
        ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> {
            return state.get(IndicatorLightBlock.POWERED) ? 0xffff19 : 0x18ddff;
        }, ApertureBlocks.INDICATOR_LIGHT);
    }
}
