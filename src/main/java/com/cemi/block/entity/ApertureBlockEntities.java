package com.cemi.block.entity;

import com.cemi.ApertureCraft;
import com.cemi.block.ApertureBlocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ApertureBlockEntities {
    public static final BlockEntityType<LargeTileBlockEntity> CONCRETE_LARGE_TILE = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new Identifier(ApertureCraft.MOD_ID, "concrete_large_tile_block_entity"),
            BlockEntityType.Builder
                    .create(LargeTileBlockEntity::new, ApertureBlocks.CONCRETE_LARGE_TILE)
                    .build());
    public static final BlockEntityType<HEPLauncherBlockEntity> HEP_LAUNCHER = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new Identifier(ApertureCraft.MOD_ID, "hep_launcher_block_entity"),
            BlockEntityType.Builder
                    .create(HEPLauncherBlockEntity::new, ApertureBlocks.HEP_LAUNCHER)
                    .build());
    public static final BlockEntityType<IndicatorLightBlockEntity> INDICATOR_LIGHT = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new Identifier(ApertureCraft.MOD_ID, "indicator_light_block_entity"),
            BlockEntityType.Builder
                    .create(IndicatorLightBlockEntity::new, ApertureBlocks.INDICATOR_LIGHT)
                    .build());
    public static final BlockEntityType<ApertureDoorBlockEntity> DOOR = Registry.register(Registries.BLOCK_ENTITY_TYPE,
            new Identifier(ApertureCraft.MOD_ID, "aperture_door_block_entity"),
            BlockEntityType.Builder
                    .create(ApertureDoorBlockEntity::new, ApertureBlocks.DOOR).build());
    public static final BlockEntityType<ApertureFloorButtonBlockEntity> FLOOR_BUTTON = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new Identifier(ApertureCraft.MOD_ID, "aperture_floor_button_block_entity"),
            BlockEntityType.Builder.create(ApertureFloorButtonBlockEntity::new,
                    ApertureBlocks.FLOOR_BUTTON).build());

    public static final BlockEntityType<TodoBlockEntity> TODO = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new Identifier(ApertureCraft.MOD_ID, "todo_block_entity"),
            BlockEntityType.Builder.create(TodoBlockEntity::new, ApertureBlocks.TODO_BLOCK).build());
    public static final BlockEntityType<ApertureCubeDropperBlockEntity> CUBE_DROPPER_BLOCK_ENTITY = Registry
            .register(Registries.BLOCK_ENTITY_TYPE,
                    new Identifier(ApertureCraft.MOD_ID, "aperture_cube_dropper_block_entity"),
                    BlockEntityType.Builder.create(ApertureCubeDropperBlockEntity::new,
                            ApertureBlocks.CUBE_DROPPER).build());
    public static final BlockEntityType<PedestalButtonBlockEntity> PEDESTAL_BUTTON = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new Identifier(ApertureCraft.MOD_ID, "aperture_pedestal_button_block_entity"),
            BlockEntityType.Builder
                    .create(PedestalButtonBlockEntity::new, ApertureBlocks.PEDESTAL_BUTTON)
                    .build());

    public static void registerBlockEntities() {
    }
}
