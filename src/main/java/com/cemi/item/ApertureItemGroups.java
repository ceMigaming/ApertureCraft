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
            .displayName(
                    Text.translatable("itemGroup." + ApertureCraft.MOD_ID + ".aperture_blocks"))
            .entries((context, entries) -> {
                entries.add(ApertureBlocks.SMALL_CONCRETE_TILE);
                entries.add(ApertureBlocks.MEDIUM_CONCRETE_TILE);
                entries.add(ApertureBlocks.CONCRETE_PILLAR);
                entries.add(ApertureBlocks.CONCRETE_LARGE_TILE);
                entries.add(ApertureBlocks.SMALL_METAL_TILE);
                entries.add(ApertureBlocks.MEDIUM_METAL_TILE);
                entries.add(ApertureBlocks.METAL_PILLAR);
                entries.add(ApertureBlocks.METAL_LARGE_TILE);
            }).build();

    private static final ItemGroup APERTURE_ITEMS = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ApertureItems.NEUROTOXIN_BUCKET))
            .displayName(Text.translatable("itemGroup." + ApertureCraft.MOD_ID + ".aperture_items"))
            .entries((context, entries) -> {
                entries.add(ApertureItems.NEUROTOXIN_BUCKET);
                entries.add(ApertureItems.PORTAL_GUN);
                entries.add(ApertureItems.LONG_FALL_BOOTS);
                entries.add(ApertureItems.COMPANION_CUBE_CAPSULE);
                entries.add(ApertureItems.RADIO_CAPSULE);
                entries.add(ApertureItems.TURRET_CAPSULE);
                entries.add(ApertureItems.ROCKET_TURRET_CAPSULE);
                entries.add(ApertureItems.STORAGE_CUBE_CAPSULE);
                entries.add(ApertureItems.RUSTY_STORAGE_CUBE_CAPSULE);
                entries.add(ApertureItems.OLD_STORAGE_CUBE_CAPSULE);
            }).build();

    private static final ItemGroup APERTURE_LOGIC = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ApertureBlocks.INDICATOR_LIGHT))
            .displayName(Text.translatable("itemGroup." + ApertureCraft.MOD_ID + ".aperture_logic"))
            .entries((context, entries) -> {
                entries.add(ApertureBlocks.INDICATOR_LIGHT);
                entries.add(ApertureBlocks.INDICATOR);
                entries.add(ApertureBlocks.DOOR);
                // entries.add(ApertureBlocks.BUTTON);
            }).build();

    public static void registerItemGroups() {
        Registry.register(Registries.ITEM_GROUP,
                new Identifier(ApertureCraft.MOD_ID, "aperture_blocks"), APERTURE_BLOCKS);
        Registry.register(Registries.ITEM_GROUP,
                new Identifier(ApertureCraft.MOD_ID, "aperture_items"), APERTURE_ITEMS);
        Registry.register(Registries.ITEM_GROUP,
                new Identifier(ApertureCraft.MOD_ID, "aperture_logic"), APERTURE_LOGIC);

    }


}
