package com.cemi.client.fluid;

import com.cemi.ApertureCraft;
import com.cemi.fluid.ApertureFluids;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

public class ApertureFluidsClient {

    public static void registerFluids() {
        FluidRenderHandlerRegistry.INSTANCE.register(ApertureFluids.STILL_NEUROTOXIN,
                ApertureFluids.FLOWING_NEUROTOXIN,
                new SimpleFluidRenderHandler(
                        new Identifier(ApertureCraft.MOD_ID, "block/neurotoxin_still"),
                        new Identifier(ApertureCraft.MOD_ID, "block/neurotoxin_flow")));
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(),
                ApertureFluids.STILL_NEUROTOXIN, ApertureFluids.FLOWING_NEUROTOXIN);
    }

}
