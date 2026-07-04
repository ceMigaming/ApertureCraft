package com.cemi.block;

import java.util.Map;

import com.cemi.block.enums.IndicatorLightConnection;
import com.cemi.state.property.ApertureProperties;
import com.cemi.util.VoxelShapeUtils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

class IndicatorLightShapeProvider {

    private static final Map<BlockFace, VoxelShape> BASE_SHAPE = Map.of(
            BlockFace.FLOOR, Block.createCuboidShape(3, 0, 3, 13, 1, 13),
            BlockFace.CEILING, Block.createCuboidShape(3, 15, 3, 13, 16, 13),
            BlockFace.WALL, Block.createCuboidShape(3, 3, 15, 13, 13, 16));

    private static final VoxelShape WALL_UP_ARM = Block.createCuboidShape(
            3, 8, 15, 13, 16, 16);

    private static final VoxelShape WALL_DOWN_ARM = Block.createCuboidShape(
            3, 0, 15, 13, 8, 16);

    private static final Map<BlockFace, Map<Direction, VoxelShape>> SIDE_SHAPES = Map.of(
            BlockFace.FLOOR, Map.of(
                    Direction.NORTH, Block.createCuboidShape(3, 0, 0, 13, 1, 3),
                    Direction.SOUTH, Block.createCuboidShape(3, 0, 13, 13, 1, 16),
                    Direction.WEST, Block.createCuboidShape(0, 0, 3, 3, 1, 13),
                    Direction.EAST, Block.createCuboidShape(13, 0, 3, 16, 1, 13)),

            BlockFace.CEILING, Map.of(
                    Direction.NORTH, Block.createCuboidShape(3, 15, 0, 13, 16, 3),
                    Direction.SOUTH, Block.createCuboidShape(3, 15, 13, 13, 16, 16),
                    Direction.WEST, Block.createCuboidShape(0, 15, 3, 3, 16, 13),
                    Direction.EAST, Block.createCuboidShape(13, 15, 3, 16, 16, 13)),

            BlockFace.WALL, Map.of(
                    Direction.SOUTH, Block.createCuboidShape(3, 13, 15, 13, 16, 16),
                    Direction.NORTH, Block.createCuboidShape(3, 0, 15, 13, 3, 16),
                    Direction.EAST, Block.createCuboidShape(0, 3, 15, 3, 13, 16),
                    Direction.WEST, Block.createCuboidShape(13, 3, 15, 16, 13, 16)));

    private static final Map<BlockFace, Map<Direction, VoxelShape>> UP_SHAPES = Map.of(
            BlockFace.FLOOR, Map.of(
                    Direction.NORTH, VoxelShapes.union(
                            SIDE_SHAPES.get(BlockFace.FLOOR).get(Direction.NORTH),
                            Block.createCuboidShape(3, 0, 0, 13, 16, 1)),
                    Direction.SOUTH, VoxelShapes.union(
                            SIDE_SHAPES.get(BlockFace.FLOOR).get(Direction.SOUTH),
                            Block.createCuboidShape(3, 0, 15, 13, 16, 16)),
                    Direction.WEST, VoxelShapes.union(
                            SIDE_SHAPES.get(BlockFace.FLOOR).get(Direction.WEST),
                            Block.createCuboidShape(0, 0, 3, 1, 16, 13)),
                    Direction.EAST, VoxelShapes.union(
                            SIDE_SHAPES.get(BlockFace.FLOOR).get(Direction.EAST),
                            Block.createCuboidShape(15, 0, 3, 16, 16, 13))),

            BlockFace.CEILING, Map.of(
                    Direction.NORTH, VoxelShapes.union(
                            SIDE_SHAPES.get(BlockFace.CEILING).get(Direction.NORTH),
                            Block.createCuboidShape(3, 0, 0, 13, 16, 1)),
                    Direction.SOUTH, VoxelShapes.union(
                            SIDE_SHAPES.get(BlockFace.CEILING).get(Direction.SOUTH),
                            Block.createCuboidShape(3, 0, 15, 13, 16, 16)),
                    Direction.WEST, VoxelShapes.union(
                            SIDE_SHAPES.get(BlockFace.CEILING).get(Direction.WEST),
                            Block.createCuboidShape(0, 0, 3, 1, 16, 13)),
                    Direction.EAST, VoxelShapes.union(
                            SIDE_SHAPES.get(BlockFace.CEILING).get(Direction.EAST),
                            Block.createCuboidShape(15, 0, 3, 16, 16, 13))));

    private static final Map<Direction, VoxelShape> FLOOR_UP_WALL_SHAPES = Map.of(
            Direction.NORTH, Block.createCuboidShape(3, 0, 0, 13, 16, 1),
            Direction.SOUTH, Block.createCuboidShape(3, 0, 15, 13, 16, 16),
            Direction.WEST, Block.createCuboidShape(0, 0, 3, 1, 16, 13),
            Direction.EAST, Block.createCuboidShape(15, 0, 3, 16, 16, 13));

    private static final Map<Direction, VoxelShape> CEILING_DOWN_WALL_SHAPES = Map.of(
            Direction.NORTH, Block.createCuboidShape(3, 0, 15, 13, 16, 16),
            Direction.SOUTH, Block.createCuboidShape(3, 0, 0, 13, 16, 1),
            Direction.WEST, Block.createCuboidShape(15, 0, 3, 16, 16, 13),
            Direction.EAST, Block.createCuboidShape(0, 0, 3, 1, 16, 13));

    static VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos,
            ShapeContext context, Block selfBlock) {
        BlockFace face = state.get(IndicatorLightBlock.FACE);
        Direction facing = state.get(IndicatorLightBlock.FACING);

        VoxelShape shape = BASE_SHAPE.get(face);

        if (face == BlockFace.WALL) {
            shape = rotateShape(shape, facing);
        }

        for (Direction dir : Direction.Type.HORIZONTAL) {
            EnumProperty<IndicatorLightConnection> prop = IndicatorLightBlock.DIRECTION_TO_WIRE_CONNECTION_PROPERTY
                    .get(dir);
            IndicatorLightConnection conn = state.get(prop);

            if (conn == IndicatorLightConnection.NONE) {
                continue;
            }

            Direction localDir = (face == BlockFace.WALL
                    && (conn == IndicatorLightConnection.SIDE || conn == IndicatorLightConnection.UP))
                            ? toWallLocal(dir, facing)
                            : dir;

            VoxelShape arm = SIDE_SHAPES.get(face).get(localDir);

            if (face == BlockFace.WALL && conn == IndicatorLightConnection.UP) {
                arm = SIDE_SHAPES.get(face).get(localDir.getOpposite());
                arm = rotateShape(arm, facing);
            }

            if (face == BlockFace.WALL && conn == IndicatorLightConnection.SIDE) {
                arm = rotateShape(arm, facing);
            }

            if (face == BlockFace.FLOOR && conn == IndicatorLightConnection.UP) {
                VoxelShape upShape = UP_SHAPES.get(face).get(localDir);
                if (face == BlockFace.WALL) {
                    upShape = rotateShape(upShape, facing);
                }
                shape = VoxelShapes.union(shape, upShape);
            }

            shape = VoxelShapes.union(shape, arm);
        }

        switch (face) {
            case FLOOR -> {
                if (!state.get(IndicatorLightBlock.UP)) {
                    break;
                }

                BlockPos wallPos = pos.up();
                BlockState wallState = world.getBlockState(wallPos);

                if (!wallState.isOf(selfBlock)) {
                    break;
                }

                shape = VoxelShapes.union(shape,
                        FLOOR_UP_WALL_SHAPES.get(wallState.get(IndicatorLightBlock.FACING)));

                shape = VoxelShapes.union(shape,
                        SIDE_SHAPES.get(BlockFace.FLOOR).get(wallState.get(IndicatorLightBlock.FACING)));
            }
            case CEILING -> {
                if (state.get(IndicatorLightBlock.DOWN)) {
                    Direction wallFacing = getDownWallFacing(world, pos);
                    if (wallFacing != null) {
                        shape = VoxelShapes.union(shape,
                                CEILING_DOWN_WALL_SHAPES.get(wallFacing.getOpposite()));
                        shape = VoxelShapes.union(shape,
                                SIDE_SHAPES.get(BlockFace.CEILING).get(wallFacing));
                    }
                }
            }
            case WALL -> {
                if (state.get(IndicatorLightBlock.UP)) {
                    shape = VoxelShapes.union(shape, rotateShape(WALL_UP_ARM, facing));
                }
                if (state.get(IndicatorLightBlock.DOWN)) {
                    shape = VoxelShapes.union(shape, rotateShape(WALL_DOWN_ARM, facing));
                }
            }
        }

        return shape;
    }

    static Direction getUpWallFacing(BlockView world, BlockPos pos) {
        BlockPos up = pos.up();
        for (Direction dir : Direction.Type.HORIZONTAL) {
            BlockState side = world.getBlockState(up.offset(dir));
            if (side.isSideSolidFullSquare(world, up.offset(dir), dir.getOpposite())) {
                return dir;
            }
        }
        return null;
    }

    static Direction getDownWallFacing(BlockView world, BlockPos pos) {
        BlockPos down = pos.down();
        for (Direction dir : Direction.Type.HORIZONTAL) {
            BlockState side = world.getBlockState(down.offset(dir));
            if (side.isSideSolidFullSquare(world, down.offset(dir), dir.getOpposite())) {
                return dir;
            }
        }
        return null;
    }

    static Direction toWallLocal(Direction worldDir, Direction facing) {
        return switch (facing) {
            case NORTH -> worldDir;
            case SOUTH -> worldDir.getOpposite();
            case EAST -> worldDir.rotateYCounterclockwise();
            case WEST -> worldDir.rotateYClockwise();
            default -> worldDir;
        };
    }

    static VoxelShape rotateShape(VoxelShape shape, Direction facing) {
        return switch (facing) {
            case SOUTH -> shape;
            case WEST -> VoxelShapeUtils.rotateShape(Direction.SOUTH, Direction.WEST, shape);
            case NORTH -> VoxelShapeUtils.rotateShape(Direction.SOUTH, Direction.NORTH, shape);
            case EAST -> VoxelShapeUtils.rotateShape(Direction.SOUTH, Direction.EAST, shape);
            default -> shape;
        };
    }
}
