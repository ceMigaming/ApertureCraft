package com.cemi.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;

public class ApertureAttributes {
    public static void registerAttributes() {
        FabricDefaultAttributeRegistry.register(ApertureEntities.STORAGE_CUBE,
                StorageCubeEntity.createLivingAttributes());

    }
}
