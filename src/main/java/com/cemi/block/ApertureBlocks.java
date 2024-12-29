package com.cemi.block;

import com.cemi.ApertureCraft;
import com.cemi.entity.ApertureEntities;
import com.cemi.fluid.ApertureFluids;

import net.minecraft.block.AbstractBlock;
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
        public static final ApertureBlock SMALL_CONCRETE_TILE = new ApertureBlock("concrete_tile_small",
                        AbstractBlock.Settings.create().strength(4.f));
        public static final ApertureBlock MEDIUM_CONCRETE_TILE = new ApertureBlock("concrete_tile_medium",
                        AbstractBlock.Settings.create().strength(4.f));
        public static final LargeTileBlock CONCRETE_LARGE_TILE = new LargeTileBlock("concrete_tile_large",
                        AbstractBlock.Settings.create().strength(4.f));
        public static final BlockCollumn CONCRETE_PILLAR = new BlockCollumn("concrete_pillar",
                        AbstractBlock.Settings.create().strength(4.f));
        // metal
        public static final ApertureBlock SMALL_METAL_TILE = new ApertureBlock("metal_tile_small",
                        AbstractBlock.Settings.create().strength(4.f));
        public static final ApertureBlock MEDIUM_METAL_TILE = new ApertureBlock("metal_tile_medium",
                        AbstractBlock.Settings.create().strength(4.f));
        public static final LargeTileBlock METAL_LARGE_TILE = new LargeTileBlock("metal_tile_large",
                        AbstractBlock.Settings.create().strength(4.f));
        public static final BlockCollumn METAL_PILLAR = new BlockCollumn("metal_pillar",
                        AbstractBlock.Settings.create().strength(4.f));

        // glass
        public static final ApertureGlass GLASS_PANE = new ApertureGlass("glass_pane",
                        AbstractBlock.Settings.create().nonOpaque().strength(4.f));
        public static final ApertureGlass GLASS_PANE_SEEMED = new ApertureGlass("glass_pane_seemed",
                        AbstractBlock.Settings.create().nonOpaque().strength(4.f));
        public static final ApertureBlock GLASS_BLOCK = new ApertureBlock("glass_block",
                        AbstractBlock.Settings.create().nonOpaque().strength(4.f));
        public static final ApertureBlock GLASS_BLOCK_SEEMED = new ApertureBlock("glass_block_seemed",
                        AbstractBlock.Settings.create().nonOpaque().strength(4.f));

        // logic blocks
        public static final IndicatorLightBlock INDICATOR_LIGHT = new IndicatorLightBlock(
                        AbstractBlock.Settings.copy(Blocks.REDSTONE_WIRE));
        public static final HEPLauncherBlock HEP_LAUNCHER = new HEPLauncherBlock("hep_launcher",
                        AbstractBlock.Settings.copy(Blocks.DISPENSER).nonOpaque());

        public static final ApertureBlock INDICATOR = new IndicatorBlock(
                        AbstractBlock.Settings.copy(Blocks.STONE_BUTTON).nonOpaque());

        public static final ApertureDoorBlock DOOR = new ApertureDoorBlock(
                        AbstractBlock.Settings.copy(Blocks.IRON_DOOR));
        public static final ApertureFloorButton FLOOR_BUTTON = new ApertureFloorButton("floor_button",
                        AbstractBlock.Settings.create().nonOpaque().strength(4.f));
        public static final ApertureCubeDropperBlock CUBE_DROPPER = new ApertureCubeDropperBlock("cube_dropper",
                        AbstractBlock.Settings.copy(Blocks.DISPENSER).nonOpaque(), ApertureEntities.STORAGE_CUBE);
        public static final ApertureCubeDropperBlock COMPANION_CUBE_DROPPER = new ApertureCubeDropperBlock(
                        "companion_cube_dropper",
                        AbstractBlock.Settings.copy(Blocks.DISPENSER).nonOpaque(), ApertureEntities.COMPANION_CUBE);

        // smart panels
        public static final ApertureBlock PANEL_RAIL_STRAIGHT = new AperturePanelRailStraightBlock(
                        AbstractBlock.Settings.copy(Blocks.RAIL));
        public static final ApertureBlock PANEL_RAIL_CORNER = new AperturePanelRailCornerBlock(
                        AbstractBlock.Settings.copy(Blocks.RAIL));
        public static final ApertureBlock PANEL_RAIL_SUPPORT = new AperturePanelRailSupportBlock(
                        AbstractBlock.Settings.copy(Blocks.RAIL));

        private static final ApertureBlock[] BLOCKS = { SMALL_CONCRETE_TILE, MEDIUM_CONCRETE_TILE,
                        CONCRETE_LARGE_TILE, CONCRETE_PILLAR, SMALL_METAL_TILE, MEDIUM_METAL_TILE,
                        METAL_LARGE_TILE, METAL_PILLAR, INDICATOR_LIGHT, GLASS_PANE, GLASS_PANE_SEEMED,
                        GLASS_BLOCK, GLASS_BLOCK_SEEMED, HEP_LAUNCHER, INDICATOR, DOOR, FLOOR_BUTTON, CUBE_DROPPER,
                        COMPANION_CUBE_DROPPER, PANEL_RAIL_STRAIGHT, PANEL_RAIL_CORNER, PANEL_RAIL_SUPPORT };

        public static void registerBlocks() {
                NEUROTOXIN = Registry.register(Registries.BLOCK,
                                Identifier.of(ApertureCraft.MOD_ID, "neurotoxin"),
                                new FluidBlock(ApertureFluids.STILL_NEUROTOXIN,
                                                AbstractBlock.Settings.copy(Blocks.WATER)
                                                                .mapColor(MapColor.DARK_GREEN)) {
                                });

                for (ApertureBlock block : BLOCKS) {
                        block.register();
                }
        }
}
