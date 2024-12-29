package com.cemi.item;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.cemi.ApertureCraft;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public class ApertureArmorMaterials {

    public static final RegistryEntry<ArmorMaterial> APERTURE_ARMOR_MATERIAL = registerMaterial("aperture",
            Map.of(
                    ArmorItem.Type.HELMET, 3,
                    ArmorItem.Type.CHESTPLATE, 8,
                    ArmorItem.Type.LEGGINGS, 6,
                    ArmorItem.Type.BOOTS, 3),
            0,
            SoundEvents.ITEM_ARMOR_EQUIP_IRON,
            () -> Ingredient.ofItems(Items.FEATHER),
            0.0F,
            0.0F,
            false);

    public static RegistryEntry<ArmorMaterial> registerMaterial(String id, Map<ArmorItem.Type, Integer> defensePoints,
            int enchantability, RegistryEntry<SoundEvent> equipSound, Supplier<Ingredient> repairIngredientSupplier,
            float toughness, float knockbackResistance, boolean dyeable) {
        List<ArmorMaterial.Layer> layers = List.of(
                new ArmorMaterial.Layer(Identifier.of(ApertureCraft.MOD_ID, id), "", dyeable));

        ArmorMaterial material = new ArmorMaterial(defensePoints, enchantability, equipSound, repairIngredientSupplier,
                layers, toughness, knockbackResistance);
        material = Registry.register(Registries.ARMOR_MATERIAL, Identifier.of(ApertureCraft.MOD_ID, id),
                material);
        return RegistryEntry.of(material);
    }

}
