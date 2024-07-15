package com.cemi.item;

import com.cemi.ApertureCraft;
import com.cemi.entity.ApertureEntities;
import com.cemi.fluid.ApertureFluids;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ApertureItems {
    
    public static final IntelligenceDeploymentCapsule COMPANION_CUBE_CAPSULE = new IntelligenceDeploymentCapsule("companion_cube_capsule", ApertureEntities.COMPANION_CUBE);
    public static final IntelligenceDeploymentCapsule RADIO_CAPSULE = new IntelligenceDeploymentCapsule("radio_capsule", ApertureEntities.RADIO);
    public static final IntelligenceDeploymentCapsule TURRET_CAPSULE = new IntelligenceDeploymentCapsule("turret_capsule", ApertureEntities.TURRET);
    public static final IntelligenceDeploymentCapsule ROCKET_TURRET_CAPSULE = new IntelligenceDeploymentCapsule("rocket_turret_capsule", ApertureEntities.ROCKET_TURRET);
    public static final IntelligenceDeploymentCapsule STORAGE_CUBE_CAPSULE = new IntelligenceDeploymentCapsule("storage_cube_capsule", ApertureEntities.STORAGE_CUBE);
    public static final IntelligenceDeploymentCapsule RUSTY_STORAGE_CUBE_CAPSULE = new IntelligenceDeploymentCapsule("rusty_storage_cube_capsule", ApertureEntities.RUSTY_STORAGE_CUBE);
    public static final IntelligenceDeploymentCapsule OLD_STORAGE_CUBE_CAPSULE = new IntelligenceDeploymentCapsule("old_storage_cube_capsule", ApertureEntities.OLD_STORAGE_CUBE);

    public static final ApertureArmorMaterial APERTURE_ARMOR_MATERIAL = new ApertureArmorMaterial();

    public static final Item LONG_FALL_BOOTS = new ArmorItem(APERTURE_ARMOR_MATERIAL,
            net.minecraft.item.ArmorItem.Type.BOOTS, new Item.Settings());

    public static BucketItem NEUROTOXIN_BUCKET;
    public static PortalGunItem PORTAL_GUN = new PortalGunItem();
    private static final ApertureItem[] ITEMS = {PORTAL_GUN, COMPANION_CUBE_CAPSULE, RADIO_CAPSULE, TURRET_CAPSULE, 
        ROCKET_TURRET_CAPSULE, STORAGE_CUBE_CAPSULE, RUSTY_STORAGE_CUBE_CAPSULE, OLD_STORAGE_CUBE_CAPSULE};

    public static void registerItems() {
        NEUROTOXIN_BUCKET = Registry.register(Registries.ITEM,
                new Identifier(ApertureCraft.MOD_ID, "neurotoxin_bucket"),
                new BucketItem(ApertureFluids.STILL_NEUROTOXIN,
                        new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1)));
        Registry.register(Registries.ITEM, new Identifier(ApertureCraft.MOD_ID, "long_fall_boots"),
                LONG_FALL_BOOTS);
        for (ApertureItem item : ITEMS) {
            item.register();
        }
    }
}
