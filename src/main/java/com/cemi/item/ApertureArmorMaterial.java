package com.cemi.item;

import net.minecraft.item.ArmorItem.Type;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class ApertureArmorMaterial implements ArmorMaterial {
    private static final int BASE_DURABILITY = 1;
    private static final int PROTECTION_AMOUNT = 0;

    @Override
    public int getDurability(Type type) {
        return BASE_DURABILITY;
    }

    @Override
    public int getEnchantability() {
        return 0;
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ITEM_ARMOR_EQUIP_IRON;
    }

    @Override
    public float getKnockbackResistance() {
        return 0;
    }

    @Override
    public String getName() {
        return "aperture";
    }

    @Override
    public int getProtection(Type type) {
        return PROTECTION_AMOUNT;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.ofItems(Items.FEATHER);
    }

    @Override
    public float getToughness() {
        return 0;
    }

}
