package com.cemi.block;

import com.cemi.ApertureCraft;
import com.cemi.fluid.ApertureFluids;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ApertureBlocks {

    // fluid blocks
    public static Block NEUROTOXIN;

    // decorative blocks
    public static final ApertureBlock SMALL_CONCRETE_TILE =
            new ApertureBlock("small_concrete_tile", FabricBlockSettings.create().strength(4.f));

    private static final ApertureBlock[] BLOCKS = {SMALL_CONCRETE_TILE};

    public static void registerBlocks() {
        NEUROTOXIN = Registry.register(Registries.BLOCK,
                new Identifier(ApertureCraft.MOD_ID, "neurotoxin"), new FluidBlock(
                        ApertureFluids.NEUROTOXIN_STILL, FabricBlockSettings.copy(Blocks.WATER)) {});

        for (ApertureBlock block : BLOCKS) {
            block.register();
        }
    }
}
