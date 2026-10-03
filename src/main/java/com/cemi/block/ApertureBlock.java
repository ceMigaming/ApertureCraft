package com.cemi.block;

import java.util.Map;

import com.cemi.ApertureCraft;
import com.cemi.block.enums.PortalSurface;
import com.cemi.item.ApertureGeoItem;
import com.cemi.item.ApertureItems;
import com.cemi.state.property.ApertureProperties;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ApertureBlock extends Block {

    protected String name;
    public static final EnumProperty<PortalSurface> PORTAL_SURFACE_NORTH = ApertureProperties.PORTAL_SURFACE_NORTH;
    public static final EnumProperty<PortalSurface> PORTAL_SURFACE_EAST = ApertureProperties.PORTAL_SURFACE_EAST;
    public static final EnumProperty<PortalSurface> PORTAL_SURFACE_SOUTH = ApertureProperties.PORTAL_SURFACE_SOUTH;
    public static final EnumProperty<PortalSurface> PORTAL_SURFACE_WEST = ApertureProperties.PORTAL_SURFACE_WEST;
    public static final EnumProperty<PortalSurface> PORTAL_SURFACE_UP = ApertureProperties.PORTAL_SURFACE_UP;
    public static final EnumProperty<PortalSurface> PORTAL_SURFACE_DOWN = ApertureProperties.PORTAL_SURFACE_DOWN;

    @SuppressWarnings("null")
    public static final Map<Direction, EnumProperty<PortalSurface>> DIRECTION_TO_PORTAL_SURFACE = Maps
            .newEnumMap(ImmutableMap.of(
                    Direction.NORTH, PORTAL_SURFACE_NORTH,
                    Direction.EAST, PORTAL_SURFACE_EAST,
                    Direction.SOUTH, PORTAL_SURFACE_SOUTH,
                    Direction.WEST, PORTAL_SURFACE_WEST,
                    Direction.UP, PORTAL_SURFACE_UP,
                    Direction.DOWN, PORTAL_SURFACE_DOWN));

    protected boolean usesGeoItem = false;

    public ApertureBlock(String name, Settings settings) {
        super(settings);
        this.name = name;
    }

    public ApertureBlock(String name, Settings settings, boolean usesGeoItem) {
        super(settings);
        this.name = name;
        this.usesGeoItem = usesGeoItem;
    }

    public ApertureBlock(String name, Settings settings, PortalSurface defaultSurface) {
        super(settings);
        this.name = name;
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(PORTAL_SURFACE_NORTH, defaultSurface)
                .with(PORTAL_SURFACE_EAST, defaultSurface)
                .with(PORTAL_SURFACE_SOUTH, defaultSurface)
                .with(PORTAL_SURFACE_WEST, defaultSurface)
                .with(PORTAL_SURFACE_UP, defaultSurface)
                .with(PORTAL_SURFACE_DOWN, defaultSurface));
    }

    public ApertureBlock(String name, Settings settings, boolean usesGeoItem, PortalSurface defaultSurface) {
        super(settings);
        this.name = name;
        this.usesGeoItem = usesGeoItem;
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(PORTAL_SURFACE_NORTH, defaultSurface)
                .with(PORTAL_SURFACE_EAST, defaultSurface)
                .with(PORTAL_SURFACE_SOUTH, defaultSurface)
                .with(PORTAL_SURFACE_WEST, defaultSurface)
                .with(PORTAL_SURFACE_UP, defaultSurface)
                .with(PORTAL_SURFACE_DOWN, defaultSurface));
    }

    public String getBlockName() {
        return name;
    }

    public void register() {
        Registry.register(Registries.BLOCK, new Identifier(ApertureCraft.MOD_ID, name), this);
        if (usesGeoItem) {
            Registry.register(Registries.ITEM, new Identifier(ApertureCraft.MOD_ID, name),
                    new ApertureGeoItem(this, new FabricItemSettings()));
        } else {
            Registry.register(Registries.ITEM, new Identifier(ApertureCraft.MOD_ID, name),
                    new BlockItem(this, new FabricItemSettings()));
        }
    }

    public boolean canPlacePortals(BlockState state, Direction side) {
        return state.get(DIRECTION_TO_PORTAL_SURFACE.get(side)) == PortalSurface.CONCRETE;
    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> builder) {
        builder.add(PORTAL_SURFACE_NORTH, PORTAL_SURFACE_EAST, PORTAL_SURFACE_SOUTH, PORTAL_SURFACE_WEST,
                PORTAL_SURFACE_UP, PORTAL_SURFACE_DOWN);
    }

}
