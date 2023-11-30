package com.cemi.entity;

import com.cemi.ApertureCraft;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ApertureEntities {

    public static final EntityType<GhostBlockEntity> GHOSTBLOCK = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(ApertureCraft.MOD_ID, "ghostblock"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, GhostBlockEntity::new)
                    .dimensions(EntityDimensions.fixed(1.0f, 1.0f)).build());

    public static final EntityType<RadioEntity> RADIO =
            Registry.register(Registries.ENTITY_TYPE, new Identifier(ApertureCraft.MOD_ID, "radio"),
                    FabricEntityTypeBuilder.create(SpawnGroup.MISC, RadioEntity::new)
                            .dimensions(EntityDimensions.fixed(1.0f, 1.0f)).build());

    public static final EntityType<StorageCubeEntity> STORAGE_CUBE = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(ApertureCraft.MOD_ID, "storage_cube"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, StorageCubeEntity::new)
                    .dimensions(EntityDimensions.fixed(1.0f, 1.0f)).build());

    public static void registerEntities() {}

}
