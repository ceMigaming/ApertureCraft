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
    // concrete
    public static final ApertureBlock SMALL_CONCRETE_TILE =
            new ApertureBlock("concrete_tile_small", FabricBlockSettings.create().strength(4.f));
    public static final ApertureBlock MEDIUM_CONCRETE_TILE =
            new ApertureBlock("concrete_tile_medium", FabricBlockSettings.create().strength(4.f));
    public static final LargeTile CONCRETE_LARGE_TILE =
            new LargeTile("concrete_tile_large", FabricBlockSettings.create().strength(4.f));
    public static final BlockCollumn CONCRETE_PILLAR =
            new BlockCollumn("concrete_pillar", FabricBlockSettings.create().strength(4.f));
    // metal
    public static final ApertureBlock SMALL_METAL_TILE =
            new ApertureBlock("metal_tile_small", FabricBlockSettings.create().strength(4.f));
    public static final ApertureBlock MEDIUM_METAL_TILE =
            new ApertureBlock("metal_tile_medium", FabricBlockSettings.create().strength(4.f));
    public static final LargeTile METAL_LARGE_TILE =
            new LargeTile("metal_tile_large", FabricBlockSettings.create().strength(4.f));
    public static final BlockCollumn METAL_PILLAR =
            new BlockCollumn("metal_pillar", FabricBlockSettings.create().strength(4.f));


    private static final ApertureBlock[] BLOCKS =
            {SMALL_CONCRETE_TILE, MEDIUM_CONCRETE_TILE, CONCRETE_LARGE_TILE, CONCRETE_PILLAR,
                    SMALL_METAL_TILE, MEDIUM_METAL_TILE, METAL_LARGE_TILE, METAL_PILLAR};

    public static void registerBlocks() {
        NEUROTOXIN = Registry.register(Registries.BLOCK,
                new Identifier(ApertureCraft.MOD_ID, "neurotoxin"),
                new FluidBlock(ApertureFluids.STILL_NEUROTOXIN,
                        FabricBlockSettings.copy(Blocks.WATER)) {});

        for (ApertureBlock block : BLOCKS) {
            block.register();
        }
    }
}
