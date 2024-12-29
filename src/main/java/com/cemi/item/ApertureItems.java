package com.cemi.item;

import java.util.EnumMap;

import com.cemi.ApertureCraft;
import com.cemi.fluid.ApertureFluids;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public class ApertureItems {

    public static final Item LONG_FALL_BOOTS = new ArmorItem(ApertureArmorMaterials.APERTURE_ARMOR_MATERIAL,
            net.minecraft.item.ArmorItem.Type.BOOTS, new Item.Settings());

    public static BucketItem NEUROTOXIN_BUCKET;
    public static PortalGunItem PORTAL_GUN = new PortalGunItem();
    private static final ApertureItem[] ITEMS = { PORTAL_GUN };

    public static void registerItems() {
        NEUROTOXIN_BUCKET = Registry.register(Registries.ITEM,
                Identifier.of(ApertureCraft.MOD_ID, "neurotoxin_bucket"),
                new BucketItem(ApertureFluids.STILL_NEUROTOXIN,
                        new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1)));
        Registry.register(Registries.ITEM, Identifier.of(ApertureCraft.MOD_ID, "long_fall_boots"),
                LONG_FALL_BOOTS);
        for (ApertureItem item : ITEMS) {
            item.register();
        }
    }
}
