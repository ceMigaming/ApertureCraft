package com.cemi.block.entity;

import com.cemi.ApertureCraft;
import com.cemi.block.ApertureBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ApertureBlockEntities {
    public static final BlockEntityType<LargeTileBlockEntity> CONCRETE_LARGE_TILE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE,
                    new Identifier(ApertureCraft.MOD_ID, "concrete_large_tile_block_entity"),
                    FabricBlockEntityTypeBuilder
                            .create(LargeTileBlockEntity::new, ApertureBlocks.CONCRETE_LARGE_TILE)
                            .build());
    public static final BlockEntityType<HEPLauncherBlockEntity> HEP_LAUNCHER =
            Registry.register(Registries.BLOCK_ENTITY_TYPE,
                    new Identifier(ApertureCraft.MOD_ID, "hep_launcher_block_entity"),
                    FabricBlockEntityTypeBuilder
                            .create(HEPLauncherBlockEntity::new, ApertureBlocks.HEP_LAUNCHER)
                            .build());

    public static void registerBlockEntities() {}
}
