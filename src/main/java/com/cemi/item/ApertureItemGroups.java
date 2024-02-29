package com.cemi.item;

import com.cemi.ApertureCraft;
import com.cemi.block.ApertureBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ApertureItemGroups {
        private static final ItemGroup APERTURE_BLOCKS = FabricItemGroup.builder()
                        .icon(() -> new ItemStack(ApertureBlocks.SMALL_CONCRETE_TILE))
                        .displayName(Text.translatable(
                                        "itemGroup." + ApertureCraft.MOD_ID + ".aperture_blocks"))
                        .entries((context, entries) -> {
                                entries.add(ApertureBlocks.SMALL_CONCRETE_TILE);
                                entries.add(ApertureBlocks.MEDIUM_CONCRETE_TILE);
                                entries.add(ApertureBlocks.CONCRETE_PILLAR);
                                entries.add(ApertureBlocks.CONCRETE_LARGE_TILE);
                                entries.add(ApertureBlocks.SMALL_METAL_TILE);
                                entries.add(ApertureBlocks.MEDIUM_METAL_TILE);
                                entries.add(ApertureBlocks.METAL_PILLAR);
                                entries.add(ApertureBlocks.METAL_LARGE_TILE);
                                entries.add(ApertureBlocks.GLASS_PANE);
                                entries.add(ApertureBlocks.GLASS_PANE_SEEMED);
                                entries.add(ApertureBlocks.GLASS_BLOCK);
                                entries.add(ApertureBlocks.GLASS_BLOCK_SEEMED);

                        }).build();
        private static final ItemGroup APERTURE_ITEMS = FabricItemGroup.builder()
                        .icon(() -> new ItemStack(ApertureItems.NEUROTOXIN_BUCKET))
                        .displayName(Text.translatable(
                                        "itemGroup." + ApertureCraft.MOD_ID + ".aperture_items"))
                        .entries((context, entries) -> {
                                entries.add(ApertureItems.NEUROTOXIN_BUCKET);
                        }).build();

        public static void registerItemGroups() {
                Registry.register(Registries.ITEM_GROUP,
                                new Identifier(ApertureCraft.MOD_ID, "aperture_blocks"),
                                APERTURE_BLOCKS);
                Registry.register(Registries.ITEM_GROUP,
                                new Identifier(ApertureCraft.MOD_ID, "aperture_items"),
                                APERTURE_ITEMS);
        }


}
