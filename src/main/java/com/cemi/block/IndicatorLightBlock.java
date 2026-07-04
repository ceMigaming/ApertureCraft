package com.cemi.block;

import java.util.Map;

import com.cemi.block.entity.IndicatorLightBlockEntity;
import com.cemi.block.enums.IndicatorLightConnection;
import com.cemi.state.property.ApertureProperties;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

@SuppressWarnings("deprecation")
public class IndicatorLightBlock extends ApertureBlock implements BlockEntityProvider {
    public static final MapCodec<RedstoneWireBlock> CODEC = createCodec(RedstoneWireBlock::new);
    public static final EnumProperty<BlockFace> FACE = Properties.BLOCK_FACE;
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final EnumProperty<IndicatorLightConnection> WIRE_CONNECTION_NORTH = ApertureProperties.NORTH_WIRE_CONNECTION;
    public static final EnumProperty<IndicatorLightConnection> WIRE_CONNECTION_EAST = ApertureProperties.EAST_WIRE_CONNECTION;
    public static final EnumProperty<IndicatorLightConnection> WIRE_CONNECTION_SOUTH = ApertureProperties.SOUTH_WIRE_CONNECTION;
    public static final EnumProperty<IndicatorLightConnection> WIRE_CONNECTION_WEST = ApertureProperties.WEST_WIRE_CONNECTION;
    public static final BooleanProperty UP = BooleanProperty.of("up");
    public static final BooleanProperty DOWN = BooleanProperty.of("down");
    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final Map<Direction, EnumProperty<IndicatorLightConnection>> DIRECTION_TO_WIRE_CONNECTION_PROPERTY = Maps
            .newEnumMap(ImmutableMap.of(
                    Direction.NORTH, WIRE_CONNECTION_NORTH,
                    Direction.EAST, WIRE_CONNECTION_EAST,
                    Direction.SOUTH, WIRE_CONNECTION_SOUTH,
                    Direction.WEST, WIRE_CONNECTION_WEST));

    boolean wiresGivePower = true;

    private final IndicatorLightNetworkHandler networkHandler;

    public IndicatorLightBlock(AbstractBlock.Settings settings) {
        super("indicator_light", settings);
        this.networkHandler = new IndicatorLightNetworkHandler(this);
        this.setDefaultState(
                this.stateManager.getDefaultState()
                        .with(FACE, BlockFace.FLOOR)
                        .with(FACING, Direction.NORTH)
                        .with(POWERED, false)
                        .with(UP, false)
                        .with(DOWN, false)
                        .with(WIRE_CONNECTION_NORTH, IndicatorLightConnection.NONE)
                        .with(WIRE_CONNECTION_EAST, IndicatorLightConnection.NONE)
                        .with(WIRE_CONNECTION_SOUTH, IndicatorLightConnection.NONE)
                        .with(WIRE_CONNECTION_WEST, IndicatorLightConnection.NONE));
    }

    public MapCodec<RedstoneWireBlock> getCodec() {
        return CODEC;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();
        Direction side = ctx.getSide();

        BlockFace face;

        if (side == Direction.UP) {
            face = BlockFace.FLOOR;
        } else if (side == Direction.DOWN) {
            face = BlockFace.CEILING;
        } else {
            BlockState below = world.getBlockState(pos.down());
            BlockState above = world.getBlockState(pos.up());
            if (below.isSolidBlock(world, pos.down())) {
                face = BlockFace.FLOOR;
            } else if (above.isSolidBlock(world, pos.up())) {
                face = BlockFace.CEILING;
            } else {
                face = BlockFace.WALL;
            }
        }

        Direction facing = face == BlockFace.WALL
                ? side.getOpposite()
                : ctx.getHorizontalPlayerFacing();

        BlockState state = getDefaultState()
                .with(FACE, face)
                .with(FACING, facing);

        for (Direction dir : Direction.Type.HORIZONTAL) {
            state = state.with(
                    DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(dir),
                    networkHandler.getConnection(world, pos, dir, state));
        }

        boolean hasUpConnection = networkHandler.hasVerticalConnection(world, pos, Direction.UP,
                state);
        boolean hasDownConnection = networkHandler.hasVerticalConnection(world, pos, Direction.DOWN,
                state);
        state = state
                .with(UP, hasUpConnection)
                .with(DOWN, hasDownConnection);

        if (hasUpConnection && face != BlockFace.WALL) {
            state = state.with(FACING, world.getBlockState(pos.up()).get(FACING));
        }
        if (hasDownConnection && face != BlockFace.WALL) {
            state = state.with(FACING, world.getBlockState(pos.down()).get(FACING));
        }

        return state;
    }

    @Override
    public BlockState getStateForNeighborUpdate(
            BlockState state,
            Direction direction,
            BlockState neighborState,
            WorldAccess world,
            BlockPos pos,
            BlockPos neighborPos) {

        BlockState updated = state;

        if (direction.getAxis().isHorizontal()) {
            EnumProperty<IndicatorLightConnection> prop = DIRECTION_TO_WIRE_CONNECTION_PROPERTY
                    .get(direction);
            IndicatorLightConnection newConn = networkHandler.getConnection(world, pos, direction,
                    state);

            if (state.get(prop) != newConn) {
                updated = updated.with(prop, newConn);
            }
        }

        if (direction == Direction.UP || direction == Direction.DOWN) {
            boolean hasUpConnection = networkHandler.hasVerticalConnection(world, pos, Direction.UP,
                    state);
            boolean hasDownConnection = networkHandler.hasVerticalConnection(world, pos,
                    Direction.DOWN, state);

            BlockFace face = state.get(FACE);

            updated = updated
                    .with(UP, hasUpConnection)
                    .with(DOWN, hasDownConnection);

            if (hasUpConnection && face != BlockFace.WALL) {
                updated = updated.with(FACING, world.getBlockState(pos.up()).get(FACING));
            }
            if (hasDownConnection && face != BlockFace.WALL) {
                updated = updated.with(FACING, world.getBlockState(pos.down()).get(FACING));
            }
        }

        return updated;
    }

    @Override
    public void prepare(BlockState state, WorldAccess world, BlockPos pos, int flags,
            int maxUpdateDepth) {
        networkHandler.prepare(state, world, pos, flags, maxUpdateDepth);
    }

    @Override
    public void onBlockAdded(
            BlockState state, World world, BlockPos pos,
            BlockState oldState, boolean notify) {

        if (!oldState.isOf(state.getBlock()) && !world.isClient) {
            networkHandler.update(world, pos, state);

            for (Direction dir : Direction.Type.HORIZONTAL) {
                BlockPos side = pos.offset(dir);

                world.updateNeighborsAlways(side, this);
                world.updateNeighborsAlways(side.up(), this);
                world.updateNeighborsAlways(side.down(), this);
                world.updateNeighborsAlways(side.offset(dir.rotateYClockwise()), this);
                world.updateNeighborsAlways(side.offset(dir.rotateYCounterclockwise()), this);
            }
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState,
            boolean moved) {
        if (!moved && !state.isOf(newState.getBlock())) {
            super.onStateReplaced(state, world, pos, newState, moved);
            if (!world.isClient) {
                for (Direction direction : Direction.values()) {
                    world.updateNeighborsAlways(pos.offset(direction), this);
                }
                networkHandler.update(world, pos, state);
                networkHandler.updateOffsetNeighbors(world, pos);
            }
        }
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock,
            BlockPos sourcePos, boolean notify) {
        if (!world.isClient) {
            if (state.canPlaceAt(world, pos)) {
                networkHandler.update(world, pos, state);
            } else {
                dropStacks(state, world, pos);
                world.removeBlock(pos, false);
            }
        }
    }

    @Override
    public VoxelShape getOutlineShape(
            BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return IndicatorLightShapeProvider.getOutlineShape(state, world, pos, context, this);
    }

    @Override
    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos,
            Direction direction) {
        return !this.wiresGivePower ? 0 : state.getWeakRedstonePower(world, pos, direction);
    }

    @Override
    public int getWeakRedstonePower(
            BlockState state,
            BlockView world,
            BlockPos pos,
            Direction direction) {
        if (!this.wiresGivePower) {
            return 0;
        }

        IndicatorLightBlockEntity be = (IndicatorLightBlockEntity) world.getBlockEntity(pos);
        int power = be != null ? be.getPower() : 0;

        if (power <= 0) {
            return 0;
        }

        EnumProperty<IndicatorLightConnection> connProp = DIRECTION_TO_WIRE_CONNECTION_PROPERTY
                .get(direction);

        if (connProp == null || !state.contains(connProp)) {
            return 0;
        }

        IndicatorLightConnection conn = state.get(connProp);

        return conn.isConnected() ? power : 0;
    }

    public boolean emitsRedstonePower(BlockState state) {
        return this.wiresGivePower;
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return switch (rotation) {
            case CLOCKWISE_180 -> state
                    .with(WIRE_CONNECTION_NORTH, state.get(WIRE_CONNECTION_SOUTH))
                    .with(WIRE_CONNECTION_SOUTH, state.get(WIRE_CONNECTION_NORTH))
                    .with(WIRE_CONNECTION_EAST, state.get(WIRE_CONNECTION_WEST))
                    .with(WIRE_CONNECTION_WEST, state.get(WIRE_CONNECTION_EAST));
            case COUNTERCLOCKWISE_90 -> state
                    .with(WIRE_CONNECTION_NORTH, state.get(WIRE_CONNECTION_EAST))
                    .with(WIRE_CONNECTION_EAST, state.get(WIRE_CONNECTION_SOUTH))
                    .with(WIRE_CONNECTION_SOUTH, state.get(WIRE_CONNECTION_WEST))
                    .with(WIRE_CONNECTION_WEST, state.get(WIRE_CONNECTION_NORTH));
            case CLOCKWISE_90 -> state
                    .with(WIRE_CONNECTION_NORTH, state.get(WIRE_CONNECTION_WEST))
                    .with(WIRE_CONNECTION_EAST, state.get(WIRE_CONNECTION_NORTH))
                    .with(WIRE_CONNECTION_SOUTH, state.get(WIRE_CONNECTION_EAST))
                    .with(WIRE_CONNECTION_WEST, state.get(WIRE_CONNECTION_SOUTH));
            default -> state;
        };
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return switch (mirror) {
            case LEFT_RIGHT -> state
                    .with(WIRE_CONNECTION_NORTH, state.get(WIRE_CONNECTION_SOUTH))
                    .with(WIRE_CONNECTION_SOUTH, state.get(WIRE_CONNECTION_NORTH));
            case FRONT_BACK -> state
                    .with(WIRE_CONNECTION_EAST, state.get(WIRE_CONNECTION_WEST))
                    .with(WIRE_CONNECTION_WEST, state.get(WIRE_CONNECTION_EAST));
            default -> super.mirror(state, mirror);
        };
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACE, FACING, WIRE_CONNECTION_NORTH, WIRE_CONNECTION_EAST,
                WIRE_CONNECTION_SOUTH, WIRE_CONNECTION_WEST, POWERED, UP, DOWN);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new IndicatorLightBlockEntity(pos, state);
    }
}
