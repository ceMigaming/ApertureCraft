package com.cemi.entity;

import com.cemi.ApertureCraft;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import qouteall.imm_ptl.core.portal.Portal;

public class ApertureEntities {

    public static final EntityType<AperturePortal> APERTURE_PORTAL = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(ApertureCraft.MOD_ID, "aperture_portal"),
            Portal.createPortalEntityType(AperturePortal::new));

    public static final EntityType<GhostBlockEntity> GHOSTBLOCK = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(ApertureCraft.MOD_ID, "ghostblock"),
            EntityType.Builder.create(GhostBlockEntity::new, SpawnGroup.MISC)
                    .setDimensions(1.0f, 1.0f).build());

    public static final EntityType<HighEnergyPelletEntity> HIGH_ENERGY_PELLET = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(ApertureCraft.MOD_ID, "high_energy_pellet"),
            EntityType.Builder.create(HighEnergyPelletEntity::new, SpawnGroup.MISC)
                    .setDimensions(.5f, .5f).build());

    public static final EntityType<RadioEntity> RADIO = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(ApertureCraft.MOD_ID, "radio"), EntityType.Builder
                    .create(RadioEntity::new, SpawnGroup.MISC).setDimensions(1.0f, 1.0f).build());

    public static final EntityType<StorageCubeEntity> RUSTY_STORAGE_CUBE = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(ApertureCraft.MOD_ID, "rusty_storage_cube"),
            EntityType.Builder.create(StorageCubeEntity::new, SpawnGroup.MISC)
                    .setDimensions(1.0f, 1.0f).build());

    public static final EntityType<StorageCubeEntity> STORAGE_CUBE = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(ApertureCraft.MOD_ID, "storage_cube"),
            EntityType.Builder.create(StorageCubeEntity::new, SpawnGroup.MISC)
                    .setDimensions(1.0f, 1.0f).build());

    public static final EntityType<StorageCubeEntity> OLD_STORAGE_CUBE = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(ApertureCraft.MOD_ID, "old_storage_cube"),
            EntityType.Builder.create(StorageCubeEntity::new, SpawnGroup.MISC)
                    .setDimensions(1.0f, 1.0f).build());

    public static final EntityType<StorageCubeEntity> COMPANION_CUBE = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(ApertureCraft.MOD_ID, "companion_cube"),
            EntityType.Builder.create(StorageCubeEntity::new, SpawnGroup.MISC)
                    .setDimensions(1.0f, 1.0f).build());

    public static final EntityType<TurretEntity> TURRET = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(ApertureCraft.MOD_ID, "turret"), EntityType.Builder
                    .create(TurretEntity::new, SpawnGroup.MISC).setDimensions(1.0f, 1.0f).build());

    public static final EntityType<RocketTurretEntity> ROCKET_TURRET = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(ApertureCraft.MOD_ID, "rocket_turret"),
            EntityType.Builder.create(RocketTurretEntity::new, SpawnGroup.MISC)
                    .setDimensions(1.0f, 1.0f).build());

    public static final EntityType<PortalProjectileEntity> PORTAL_PROJECTILE = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(ApertureCraft.MOD_ID, "portal_projectile"),
            EntityType.Builder.create(PortalProjectileEntity::new, SpawnGroup.MISC)
                    .setDimensions(.5f, .5f).build());
    

    

    public static void registerEntities() {}

}
