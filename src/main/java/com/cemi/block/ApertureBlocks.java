package com.cemi.block;

import com.cemi.ApertureCraft;
import com.cemi.entity.ApertureEntities;
import com.cemi.fluid.ApertureFluids;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.MapColor;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ApertureBlocks {

    // fluid blocks
    public static Block NEUROTOXIN;

    // decorative blocks
    // concrete

    public static final ApertureBlock PORTAL1_CONCRETE_TILE = new ApertureBlock("portal1_concrete_tile",
            FabricBlockSettings.create().strength(4.f));
    public static final ApertureBlock SMALL_CONCRETE_TILE = new ApertureBlock("concrete_tile_small",
            FabricBlockSettings.create().strength(4.f));
    public static final ApertureBlock MEDIUM_CONCRETE_TILE = new ApertureBlock("concrete_tile_medium",
            FabricBlockSettings.create().strength(4.f));
    public static final LargeTileBlock CONCRETE_LARGE_TILE = new LargeTileBlock("concrete_tile_large",
            FabricBlockSettings.create().strength(4.f));
    public static final BlockCollumn CONCRETE_PILLAR = new BlockCollumn("concrete_pillar",
            FabricBlockSettings.create().strength(4.f));

    // metal
    public static final ApertureBlock SMALL_METAL_TILE = new ApertureBlock("metal_tile_small",
            FabricBlockSettings.create().strength(4.f));
    public static final ApertureBlock MEDIUM_METAL_TILE = new ApertureBlock("metal_tile_medium",
            FabricBlockSettings.create().strength(4.f));
    public static final LargeTileBlock METAL_LARGE_TILE = new LargeTileBlock("metal_tile_large",
            FabricBlockSettings.create().strength(4.f));
    public static final BlockCollumn METAL_PILLAR = new BlockCollumn("metal_pillar",
            FabricBlockSettings.create().strength(4.f));

    // glass
    public static final ApertureGlass GLASS_PANE = new ApertureGlass("glass_pane",
            FabricBlockSettings.create().nonOpaque().strength(4.f));
    public static final ApertureGlass GLASS_PANE_SEEMED = new ApertureGlass("glass_pane_seemed",
            FabricBlockSettings.create().nonOpaque().strength(4.f));
    public static final ApertureBlock GLASS_BLOCK = new ApertureBlock("glass_block",
            FabricBlockSettings.create().nonOpaque().strength(4.f));
    public static final ApertureBlock GLASS_BLOCK_SEEMED = new ApertureBlock("glass_block_seemed",
            FabricBlockSettings.create().nonOpaque().strength(4.f));

    // logic blocks
    public static final IndicatorLightBlock INDICATOR_LIGHT = new IndicatorLightBlock(
            FabricBlockSettings.copy(Blocks.REDSTONE_WIRE).notSolid().nonOpaque());
    public static final HEPLauncherBlock HEP_LAUNCHER = new HEPLauncherBlock("hep_launcher",
            FabricBlockSettings.copy(Blocks.DISPENSER).nonOpaque());

    public static final ApertureBlock INDICATOR = new IndicatorBlock(
            FabricBlockSettings.copy(Blocks.STONE_BUTTON).nonOpaque());

    public static final ApertureDoorBlock DOOR = new ApertureDoorBlock(FabricBlockSettings.copy(Blocks.IRON_DOOR));
    public static final FloorButtonBlock FLOOR_BUTTON = new FloorButtonBlock("floor_button",
            FabricBlockSettings.create().nonOpaque().strength(4.f));
    public static final PedestalButtonBlock PEDESTAL_BUTTON = new PedestalButtonBlock(
            "pedestal_button", FabricBlockSettings.create().nonOpaque().strength(4.f));

    public static final ApertureCubeDropperBlock CUBE_DROPPER = new ApertureCubeDropperBlock("cube_dropper",
            FabricBlockSettings.copy(Blocks.DISPENSER).nonOpaque(), ApertureEntities.STORAGE_CUBE);
    public static final ApertureCubeDropperBlock COMPANION_CUBE_DROPPER = new ApertureCubeDropperBlock(
            "companion_cube_dropper",
            FabricBlockSettings.copy(Blocks.DISPENSER).nonOpaque(), ApertureEntities.COMPANION_CUBE);

    // support blocks
    public static final TodoBlock TODO_BLOCK = new TodoBlock();

    private static final ApertureBlock[] BLOCKS = { PORTAL1_CONCRETE_TILE, SMALL_CONCRETE_TILE, MEDIUM_CONCRETE_TILE,
            CONCRETE_LARGE_TILE,
            CONCRETE_PILLAR,
            SMALL_METAL_TILE, MEDIUM_METAL_TILE, METAL_LARGE_TILE, METAL_PILLAR,
            INDICATOR_LIGHT, GLASS_PANE, GLASS_PANE_SEEMED, GLASS_BLOCK, GLASS_BLOCK_SEEMED,
            HEP_LAUNCHER, INDICATOR, DOOR, FLOOR_BUTTON, PEDESTAL_BUTTON, TODO_BLOCK, CUBE_DROPPER,
            COMPANION_CUBE_DROPPER };

    public static void registerBlocks() {
        NEUROTOXIN = Registry.register(Registries.BLOCK,
                new Identifier(ApertureCraft.MOD_ID, "neurotoxin"),
                new FluidBlock(ApertureFluids.STILL_NEUROTOXIN,
                        FabricBlockSettings.copy(Blocks.WATER).mapColor(MapColor.DARK_GREEN)) {
                });
        for (ApertureBlock block : BLOCKS) {
            block.register();
        }
    }
}
