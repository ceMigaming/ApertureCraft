package com.cemi.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;

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
                TurretEntity.createMobAttributes());
        FabricDefaultAttributeRegistry.register(ApertureEntities.RADIO,
                RadioEntity.createMobAttributes());
        FabricDefaultAttributeRegistry.register(ApertureEntities.ROCKET_TURRET,
                RocketTurretEntity.createMobAttributes());
    }
}
