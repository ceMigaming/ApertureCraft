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

    public static final EntityType<StorageCubeEntity> RUSTY_STORAGE_CUBE = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(ApertureCraft.MOD_ID, "rusty_storage_cube"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, StorageCubeEntity::new)
                    .dimensions(EntityDimensions.fixed(1.0f, 1.0f)).build());

    public static final EntityType<StorageCubeEntity> STORAGE_CUBE = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(ApertureCraft.MOD_ID, "storage_cube"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, StorageCubeEntity::new)
                    .dimensions(EntityDimensions.fixed(1.0f, 1.0f)).build());

    public static final EntityType<StorageCubeEntity> OLD_STORAGE_CUBE = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(ApertureCraft.MOD_ID, "old_storage_cube"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, StorageCubeEntity::new)
                    .dimensions(EntityDimensions.fixed(1.0f, 1.0f)).build());

    public static final EntityType<StorageCubeEntity> COMPANION_CUBE = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(ApertureCraft.MOD_ID, "companion_cube"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, StorageCubeEntity::new)
                    .dimensions(EntityDimensions.fixed(1.0f, 1.0f)).build());

    public static final EntityType<TurretEntity> TURRET = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(ApertureCraft.MOD_ID, "turret"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, TurretEntity::new)
                    .dimensions(EntityDimensions.fixed(1.0f, 1.0f)).build());

    public static final EntityType<RocketTurretEntity> ROCKET_TURRET = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(ApertureCraft.MOD_ID, "rocket_turret"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, RocketTurretEntity::new)
                    .dimensions(EntityDimensions.fixed(1.0f, 1.0f)).build());

    public static void registerEntities() {}

}
