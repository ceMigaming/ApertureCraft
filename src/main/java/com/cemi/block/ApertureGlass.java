package com.cemi.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class ApertureGlass extends ApertureBlock {

    public static final DirectionProperty FACING = Properties.FACING;

    public ApertureGlass(String name, Settings settings) {
        super(name, settings);
        setDefaultState(getDefaultState().with(FACING, Direction.NORTH));

    }

    @Override
    public VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
    }

    @Override
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        Direction dir = state.get(FACING);
        switch (dir) {
            case NORTH:
                return VoxelShapes.cuboid(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.125f);
            case SOUTH:
                return VoxelShapes.cuboid(0.0f, 0.0f, 0.875f, 1.0f, 1.0f, 1.0f);
            case EAST:
                return VoxelShapes.cuboid(0.0f, 0.0f, 0.0f, 0.125f, 1.0f, 1.0f);
            case WEST:
                return VoxelShapes.cuboid(0.875f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
            case DOWN:
                return VoxelShapes.cuboid(0.f, 0.875f, 0.f, 1.f, 1.f, 1.f);
            case UP:
                return VoxelShapes.cuboid(0.f, 0.f, 0.f, 1.f, 0.125f, 1.f);
            default:
                return VoxelShapes.fullCube();
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState) this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);

    }

}
