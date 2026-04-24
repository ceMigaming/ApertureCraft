package com.cemi.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class SlopeBlock extends ApertureBlock {

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    public SlopeBlock(String name, Settings settings) {
        super(name, settings);
    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction facing = state.get(FACING);

        return switch (facing) {
            case NORTH -> createNorthSlope();
            case SOUTH -> createSouthSlope();
            case WEST -> createWestSlope();
            case EAST -> createEastSlope();
            default -> VoxelShapes.fullCube();
        };
    }

    private static VoxelShape createNorthSlope() {
        VoxelShape shape = VoxelShapes.empty();

        // 16 slices = smoother slope
        for (int z = 0; z < 16; z++) {
            double height = (z + 1) / 16.0;

            shape = VoxelShapes.union(
                    shape,
                    Block.createCuboidShape(
                            0, 0, z,
                            16, height * 16, z + 1));
        }

        return shape;
    }

    private static VoxelShape createEastSlope() {
        VoxelShape shape = VoxelShapes.empty();

        for (int x = 0; x < 16; x++) {
            double height = (x + 1) / 16.0;

            shape = VoxelShapes.union(
                    shape,
                    Block.createCuboidShape(
                            15 - x, 0, 0,
                            16 - x, height * 16, 16));
        }

        return shape;
    }

    private static VoxelShape createSouthSlope() {
        VoxelShape shape = VoxelShapes.empty();

        for (int z = 0; z < 16; z++) {
            double height = (z + 1) / 16.0;

            shape = VoxelShapes.union(
                    shape,
                    Block.createCuboidShape(
                            0, 0, 15 - z,
                            16, height * 16, 16 - z));
        }

        return shape;
    }

    private static VoxelShape createWestSlope() {
        VoxelShape shape = VoxelShapes.empty();

        for (int x = 0; x < 16; x++) {
            double height = (x + 1) / 16.0;

            shape = VoxelShapes.union(
                    shape,
                    Block.createCuboidShape(
                            x, 0, 0,
                            x + 1, height * 16, 16));
        }

        return shape;
    }
}
