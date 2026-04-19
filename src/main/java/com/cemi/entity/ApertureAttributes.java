package com.cemi.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;

public class ApertureAttributes {
    public static void registerAttributes() {
        FabricDefaultAttributeRegistry.register(ApertureEntities.RUSTY_STORAGE_CUBE,
                StorageCubeEntity.createMobAttributes());
        FabricDefaultAttributeRegistry.register(ApertureEntities.STORAGE_CUBE,
                StorageCubeEntity.createMobAttributes());
        FabricDefaultAttributeRegistry.register(ApertureEntities.OLD_STORAGE_CUBE,
                StorageCubeEntity.createMobAttributes());
        FabricDefaultAttributeRegistry.register(ApertureEntities.COMPANION_CUBE,
                StorageCubeEntity.createMobAttributes());
        FabricDefaultAttributeRegistry.register(ApertureEntities.TURRET,
                TurretEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 2.0));
        FabricDefaultAttributeRegistry.register(ApertureEntities.RADIO,
                RadioEntity.createMobAttributes());
        FabricDefaultAttributeRegistry.register(ApertureEntities.ROCKET_TURRET,
                RocketTurretEntity.createMobAttributes());
    }
}
