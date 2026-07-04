package com.cemi.block;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import com.cemi.block.enums.IndicatorLightConnection;
import com.cemi.block.entity.IndicatorLightBlockEntity;
import com.google.common.collect.Sets;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ObserverBlock;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Type;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

class IndicatorLightNetworkHandler {

    private final IndicatorLightBlock block;

    IndicatorLightNetworkHandler(IndicatorLightBlock block) {
        this.block = block;
    }

    IndicatorLightConnection getConnection(BlockView world, BlockPos pos, Direction dir,
            BlockState state) {
        if (!dir.getAxis().isHorizontal()) {
            return IndicatorLightConnection.NONE;
        }

        BlockFace face = state.get(IndicatorLightBlock.FACE);

        boolean canGoUp = !world.getBlockState(pos.up()).isSolidBlock(world, pos);

        BlockPos sidePos = pos.offset(dir);
        BlockState sideState = world.getBlockState(sidePos);

        if (canGoUp) {
            boolean canClimb = sideState.isOf(block)
                    || canRunOnTop(world, sidePos, sideState);

            if (canClimb && connectsTo(world.getBlockState(sidePos.up()))) {
                if (sideState.isSideSolidFullSquare(world, sidePos, dir.getOpposite())) {
                    return IndicatorLightConnection.UP;
                }
                return IndicatorLightConnection.SIDE;
            }
        }

        if (face == BlockFace.CEILING) {
            boolean canGoAround = !world.getBlockState(pos.offset(dir)).isSolidBlock(world, pos);

            if (canGoAround) {
                BlockPos wallPos = pos.offset(dir).up();
                BlockState wallState = world.getBlockState(wallPos);

                if (wallState.isOf(block)
                        && wallState.get(IndicatorLightBlock.FACE) == BlockFace.WALL
                        && wallState.get(IndicatorLightBlock.FACING) == dir.getOpposite()) {
                    return IndicatorLightConnection.SIDE;
                }
            }
        }

        if (sideState.isSolidBlock(world, sidePos) && face != BlockFace.WALL) {
            return IndicatorLightConnection.NONE;
        }

        if (connectsTo(sideState, dir)) {
            return IndicatorLightConnection.SIDE;
        }

        if (hasPerpendicularWallWrap(world, pos, state, dir)) {
            return IndicatorLightConnection.SIDE;
        }

        if (!sideState.isSolidBlock(world, sidePos)
                && world.getBlockState(pos.down()).isSolidBlock(world, pos.down())
                && connectsTo(world.getBlockState(sidePos.down()))) {
            return IndicatorLightConnection.SIDE;
        }

        if (face == BlockFace.WALL && dir == state.get(IndicatorLightBlock.FACING).getOpposite()) {
            boolean canGoAround = !world.getBlockState(pos.down()).isSolidBlock(world, pos);

            if (canGoAround) {
                BlockPos ceilPos = pos.offset(state.get(IndicatorLightBlock.FACING)).down();
                BlockState ceilState = world.getBlockState(ceilPos);

                if (ceilState.isOf(block)
                        && ceilState.get(IndicatorLightBlock.FACE) == BlockFace.CEILING) {
                    return IndicatorLightConnection.UP;
                }
            }
        }

        return IndicatorLightConnection.NONE;
    }

    boolean hasVerticalConnection(BlockView world, BlockPos pos, Direction verticalDir,
            BlockState state) {
        BlockFace face = state.get(IndicatorLightBlock.FACE);

        BlockPos directPos = pos.offset(verticalDir);
        BlockState directState = world.getBlockState(directPos);

        if (directState.isOf(block)) {
            BlockFace otherFace = directState.get(IndicatorLightBlock.FACE);

            if (face == otherFace) {
                return switch (face) {
                    case FLOOR -> verticalDir == Direction.UP;
                    case CEILING -> verticalDir == Direction.DOWN;
                    case WALL -> true;
                };
            }

            if (face == BlockFace.FLOOR && verticalDir == Direction.UP
                    && otherFace == BlockFace.WALL)
                return true;
            if (face == BlockFace.WALL && verticalDir == Direction.DOWN
                    && otherFace == BlockFace.FLOOR)
                return true;
            if (face == BlockFace.CEILING && verticalDir == Direction.DOWN
                    && otherFace == BlockFace.WALL)
                return true;
            if (face == BlockFace.WALL && verticalDir == Direction.UP
                    && otherFace == BlockFace.CEILING)
                return true;
        }

        if (face == BlockFace.FLOOR && verticalDir == Direction.UP) {
            for (Direction dir : Type.HORIZONTAL) {
                BlockPos wallPos = pos.offset(dir);
                BlockState wallState = world.getBlockState(wallPos);

                if (!wallState.isOf(block))
                    continue;
                if (wallState.get(IndicatorLightBlock.FACE) != BlockFace.WALL)
                    continue;
                if (wallState.get(IndicatorLightBlock.FACING) != dir.getOpposite())
                    continue;

                if (world.getBlockState(pos.offset(dir)).isSolidBlock(world, pos.offset(dir))) {
                    return true;
                }
            }
        }

        if (face == BlockFace.CEILING && verticalDir == Direction.DOWN) {
            for (Direction dir : Type.HORIZONTAL) {
                BlockPos wallPos = pos.offset(dir);
                BlockState wallState = world.getBlockState(wallPos);

                if (!wallState.isOf(block))
                    continue;
                if (wallState.get(IndicatorLightBlock.FACE) != BlockFace.WALL)
                    continue;
                if (wallState.get(IndicatorLightBlock.FACING) != dir.getOpposite())
                    continue;

                if (world.getBlockState(pos.offset(dir)).isSolidBlock(world, pos.offset(dir))) {
                    return true;
                }
            }
        }

        return false;
    }

    boolean hasSideConnection(BlockState state, Direction dir) {
        EnumProperty<IndicatorLightConnection> prop = IndicatorLightBlock.DIRECTION_TO_WIRE_CONNECTION_PROPERTY
                .get(dir);
        return state.contains(prop) && state.get(prop).isConnected();
    }

    boolean canStepUp(World world, BlockPos pos, Direction dir) {
        BlockPos side = pos.offset(dir);
        BlockPos aboveSide = side.up();
        BlockState aboveSideState = world.getBlockState(aboveSide);

        if (!world.getBlockState(side).isSolidBlock(world, side))
            return false;

        return aboveSideState.isOf(block);
    }

    boolean canStepDown(World world, BlockPos pos, Direction dir) {
        BlockPos below = pos.down();
        BlockPos belowSide = below.offset(dir);
        BlockState belowSideState = world.getBlockState(belowSide);

        if (!world.getBlockState(below).isSolidBlock(world, below))
            return false;

        return belowSideState.isOf(block);
    }

    boolean canConnectUp(World world, BlockPos pos) {
        if (!world.getBlockState(pos).contains(IndicatorLightBlock.UP))
            return false;
        if (!world.getBlockState(pos).get(IndicatorLightBlock.UP))
            return false;

        BlockState aboveSideState = world.getBlockState(pos.up());

        if (!aboveSideState.isOf(block))
            return false;

        return true;
    }

    boolean canConnectDown(World world, BlockPos pos) {
        if (!world.getBlockState(pos).contains(IndicatorLightBlock.DOWN))
            return false;
        if (!world.getBlockState(pos).get(IndicatorLightBlock.DOWN))
            return false;

        BlockState belowSideState = world.getBlockState(pos.down());

        if (!belowSideState.isOf(block))
            return false;

        return true;
    }

    private boolean hasPerpendicularWallWrap(BlockView world, BlockPos pos,
            BlockState state, Direction dir) {
        if (state.get(IndicatorLightBlock.FACE) != BlockFace.WALL)
            return false;

        Direction facing = state.get(IndicatorLightBlock.FACING);

        if (facing.getAxis() == dir.getAxis())
            return false;

        BlockPos support = pos.offset(facing);
        BlockPos neighborPos = support.offset(dir);
        BlockState neighbor = world.getBlockState(neighborPos);

        if (!neighbor.isOf(block))
            return false;

        if (neighbor.get(IndicatorLightBlock.FACE) != BlockFace.WALL)
            return false;

        Direction neighborFacing = neighbor.get(IndicatorLightBlock.FACING);

        return neighborPos.offset(neighborFacing).equals(support);
    }

    private boolean canRunOnTop(BlockView world, BlockPos pos, BlockState floor) {
        return floor.isSideSolidFullSquare(world, pos, Direction.UP)
                || floor.isOf(Blocks.HOPPER);
    }

    private boolean isPerpendicularWallLightOn(BlockState state,
            BlockPos lightPos, BlockPos supportPos, Direction otherFacing) {
        if (!state.isOf(block))
            return false;

        if (state.get(IndicatorLightBlock.FACE) != BlockFace.WALL)
            return false;

        Direction facing = state.get(IndicatorLightBlock.FACING);

        if (!lightPos.offset(facing).equals(supportPos))
            return false;

        return facing.getAxis() != otherFacing.getAxis();
    }

    static boolean connectsTo(BlockState state) {
        return connectsTo(state, null);
    }

    static boolean connectsTo(BlockState state, @Nullable Direction dir) {
        if (state.isOf(Blocks.REDSTONE_WIRE)
                || state.isOf(ApertureBlocks.INDICATOR_LIGHT)) {
            return true;
        } else if (state.isOf(Blocks.REPEATER)) {
            Direction direction = state.get(RepeaterBlock.FACING);
            return direction == dir || direction.getOpposite() == dir;
        } else if (state.isOf(Blocks.OBSERVER)) {
            return dir == state.get(ObserverBlock.FACING);
        } else {
            return state.emitsRedstonePower() && dir != null;
        }
    }

    void update(World world, BlockPos pos, BlockState state) {
        int power = getReceivedRedstonePower(world, pos);
        IndicatorLightBlockEntity blockEntity = (IndicatorLightBlockEntity) world
                .getBlockEntity(pos);
        if (blockEntity != null && blockEntity.getPower() != power) {
            if (world.getBlockState(pos) == state) {
                blockEntity.setPower(power);
                world.setBlockState(pos, state.with(IndicatorLightBlock.POWERED, power > 0), 2);
            }
            Set<BlockPos> set = Sets.newHashSet();
            set.add(pos);
            for (Direction direction : Direction.values()) {
                set.add(pos.offset(direction));
            }
            for (BlockPos blockPos : set) {
                world.updateNeighborsAlways(blockPos, block);
            }
        }
    }

    void updateNeighbors(World world, BlockPos pos) {
        if (world.getBlockState(pos).isOf(block)) {
            world.updateNeighborsAlways(pos, block);
            for (Direction direction : Direction.values()) {
                world.updateNeighborsAlways(pos.offset(direction), block);
            }
        }
    }

    void updateOffsetNeighbors(World world, BlockPos pos) {
        for (Direction direction : Type.HORIZONTAL) {
            updateNeighbors(world, pos.offset(direction));
        }
        for (Direction direction : Type.HORIZONTAL) {
            BlockPos blockPos = pos.offset(direction);
            if (world.getBlockState(blockPos).isSolidBlock(world, blockPos)) {
                updateNeighbors(world, blockPos.up());
            } else {
                updateNeighbors(world, blockPos.down());
            }
        }
    }

    int getReceivedRedstonePower(World world, BlockPos pos) {
        block.wiresGivePower = false;
        int direct = world.getReceivedRedstonePower(pos);
        block.wiresGivePower = true;
        int propagated = scanNetwork(world, pos);
        return Math.max(direct, propagated);
    }

    private int scanNetwork(World world, BlockPos origin) {
        Queue<BlockPos> queue = new ArrayDeque<>();
        Map<BlockPos, Integer> bestDistance = new HashMap<>();

        queue.add(origin);
        bestDistance.put(origin, 0);

        int bestPowerAtOrigin = 0;

        while (!queue.isEmpty()) {
            BlockPos pos = queue.poll();
            int dist = bestDistance.get(pos);

            block.wiresGivePower = false;
            int external = world.getReceivedRedstonePower(pos);
            block.wiresGivePower = true;

            int powerHere = Math.max(0, external - dist);
            bestPowerAtOrigin = Math.max(bestPowerAtOrigin, powerHere);

            BlockState state = world.getBlockState(pos);

            for (Direction dir : Type.HORIZONTAL) {
                if (hasSideConnection(state, dir)) {
                    relax(world, pos.offset(dir), dist, bestDistance, queue);
                }
            }

            for (Direction dir : Type.HORIZONTAL) {
                if (hasSideConnection(state, dir) && canStepUp(world, pos, dir)) {
                    relax(world, pos.offset(dir).up(), dist, bestDistance, queue);
                }
            }

            for (Direction dir : Type.HORIZONTAL) {
                if (hasSideConnection(state, dir) && canStepDown(world, pos, dir)) {
                    relax(world, pos.offset(dir).down(), dist, bestDistance, queue);
                }
            }

            if (state.isOf(block) && state.get(IndicatorLightBlock.FACE) == BlockFace.WALL) {
                Direction facing = state.get(IndicatorLightBlock.FACING);
                BlockPos support = pos.offset(facing);

                for (Direction dir : Type.HORIZONTAL) {
                    BlockPos neighbor = support.offset(dir);
                    if (neighbor.equals(pos))
                        continue;

                    BlockState neighborState = world.getBlockState(neighbor);

                    if (isPerpendicularWallLightOn(neighborState, neighbor, support, facing)) {
                        relax(world, neighbor, dist, bestDistance, queue);
                    }
                }
            }

            if (canConnectUp(world, pos)) {
                relax(world, pos.up(), dist, bestDistance, queue);
            }

            if (canConnectDown(world, pos)) {
                relax(world, pos.down(), dist, bestDistance, queue);
            }
        }

        return bestPowerAtOrigin;
    }

    private void relax(World world, BlockPos to, int fromDistance,
            Map<BlockPos, Integer> bestDistance, Queue<BlockPos> queue) {
        BlockState state = world.getBlockState(to);
        if (!state.isOf(block))
            return;

        int newDist = fromDistance + 1;
        int oldDist = bestDistance.getOrDefault(to, Integer.MAX_VALUE);

        if (newDist < oldDist) {
            bestDistance.put(to, newDist);
            queue.add(to);
        }
    }

    void prepare(BlockState state, WorldAccess world, BlockPos pos, int flags,
            int maxUpdateDepth) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (Direction direction : Type.HORIZONTAL) {
            IndicatorLightConnection conn = state.get(
                    IndicatorLightBlock.DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction));
            if (conn == IndicatorLightConnection.NONE
                    || world.getBlockState(mutable.set(pos, direction)).isOf(block)) {
                continue;
            }

            propagateNeighborUpdate(world, pos, direction, mutable, flags, maxUpdateDepth);

            if (state.contains(IndicatorLightBlock.FACING)
                    && state.get(IndicatorLightBlock.FACE) == BlockFace.WALL) {
                mutable.set(pos).move(Direction.DOWN)
                        .move(state.get(IndicatorLightBlock.FACING));
                BlockState ceilState = world.getBlockState(mutable);
                if (ceilState.isOf(block)) {
                    BlockPos ceilPos = mutable
                            .offset(state.get(IndicatorLightBlock.FACING).getOpposite());
                    world.replaceWithStateForNeighborUpdate(
                            state.get(IndicatorLightBlock.FACING).getOpposite(),
                            world.getBlockState(ceilPos), mutable, ceilPos, flags,
                            maxUpdateDepth);
                }
            }

            if (state.get(IndicatorLightBlock.FACE) == BlockFace.CEILING) {
                mutable.set(pos, direction).move(Direction.UP);
                BlockState wallState = world.getBlockState(mutable);
                if (wallState.isOf(block)) {
                    BlockPos wallPos = mutable.offset(Direction.DOWN);
                    world.replaceWithStateForNeighborUpdate(direction,
                            world.getBlockState(wallPos), mutable, wallPos, flags,
                            maxUpdateDepth);
                }
            }
        }
    }

    private void propagateNeighborUpdate(WorldAccess world, BlockPos pos,
            Direction direction, BlockPos.Mutable mutable, int flags, int maxUpdateDepth) {
        tryUpdateAt(world, mutable.set(pos, direction).move(Direction.DOWN),
                direction.getOpposite(), flags, maxUpdateDepth);
        tryUpdateAt(world, mutable.set(pos, direction).move(Direction.UP),
                direction.getOpposite(), flags, maxUpdateDepth);
        tryUpdateAt(world, mutable.set(pos, direction).move(direction.rotateYClockwise()),
                direction.rotateYClockwise().getOpposite(), flags, maxUpdateDepth);
        tryUpdateAt(world, mutable.set(pos, direction).move(direction.rotateYCounterclockwise()),
                direction.rotateYCounterclockwise().getOpposite(), flags, maxUpdateDepth);
        tryUpdateAt(world, mutable.set(pos, Direction.UP).move(direction), Direction.DOWN,
                flags, maxUpdateDepth);
        tryUpdateAt(world, mutable.set(pos, Direction.DOWN).move(direction), Direction.UP,
                flags, maxUpdateDepth);
    }

    private void tryUpdateAt(WorldAccess world, BlockPos checkPos, Direction sourceDir,
            int flags, int maxUpdateDepth) {
        BlockState state = world.getBlockState(checkPos);
        if (state.isOf(block)) {
            BlockPos sourcePos = checkPos.offset(sourceDir);
            world.replaceWithStateForNeighborUpdate(sourceDir,
                    world.getBlockState(sourcePos), checkPos, sourcePos, flags,
                    maxUpdateDepth);
        }
    }
}
