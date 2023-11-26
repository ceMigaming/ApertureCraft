package com.cemi.item;

import com.cemi.ApertureCraft;
import com.cemi.fluid.ApertureFluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ApertureItems {
    public static BucketItem NEUROTOXIN_BUCKET;
    private static final ApertureItem[] ITEMS = {};

    public static void registerItems() {
        NEUROTOXIN_BUCKET = Registry.register(Registries.ITEM,
                new Identifier(ApertureCraft.MOD_ID, "neurotoxin_bucket"),
                new BucketItem(ApertureFluids.STILL_NEUROTOXIN,
                        new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1)));
        for (ApertureItem item : ITEMS) {
            item.register();
        }
    }
}
