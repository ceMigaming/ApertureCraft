package com.cemi.block;

import java.util.Iterator;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.UnmodifiableIterator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class BlockIndicatorLight extends ApertureBlock {

    public static final EnumProperty<WireConnection> WIRE_CONNECTION_NORTH;
    public static final EnumProperty<WireConnection> WIRE_CONNECTION_EAST;
    public static final EnumProperty<WireConnection> WIRE_CONNECTION_SOUTH;
    public static final EnumProperty<WireConnection> WIRE_CONNECTION_WEST;
    public static final BooleanProperty POWERED;
    public static final Map<Direction, EnumProperty<WireConnection>> DIRECTION_TO_WIRE_CONNECTION_PROPERTY;
    private static final VoxelShape DOT_SHAPE;
    private static final Map<Direction, VoxelShape> DIRECTION_TO_SIDE_SHAPE;
    private static final Map<Direction, VoxelShape> DIRECTION_TO_UP_SHAPE;
    private static final Map<BlockState, VoxelShape> SHAPES;
    private static final Vec3d[] COLORS;

    static {
        WIRE_CONNECTION_NORTH = Properties.NORTH_WIRE_CONNECTION;
        WIRE_CONNECTION_EAST = Properties.EAST_WIRE_CONNECTION;
        WIRE_CONNECTION_SOUTH = Properties.SOUTH_WIRE_CONNECTION;
        WIRE_CONNECTION_WEST = Properties.WEST_WIRE_CONNECTION;
        POWERED = BooleanProperty.of("powered");
        DIRECTION_TO_WIRE_CONNECTION_PROPERTY = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH,
                WIRE_CONNECTION_NORTH, Direction.EAST, WIRE_CONNECTION_EAST, Direction.SOUTH,
                WIRE_CONNECTION_SOUTH, Direction.WEST, WIRE_CONNECTION_WEST));
        DOT_SHAPE = Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 1.0, 13.0);
        DIRECTION_TO_SIDE_SHAPE = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH,
                Block.createCuboidShape(3.0, 0.0, 0.0, 13.0, 1.0, 13.0), Direction.SOUTH,
                Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 1.0, 16.0), Direction.EAST,
                Block.createCuboidShape(3.0, 0.0, 3.0, 16.0, 1.0, 13.0), Direction.WEST,
                Block.createCuboidShape(0.0, 0.0, 3.0, 13.0, 1.0, 13.0), Direction.UP,
                Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 16.0, 13.0), Direction.DOWN,
                Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 1.0, 13.0)));
        DIRECTION_TO_UP_SHAPE =
                Maps.newEnumMap(ImmutableMap.of(Direction.NORTH,
                        VoxelShapes.union((VoxelShape) DIRECTION_TO_SIDE_SHAPE.get(Direction.NORTH),
                                Block.createCuboidShape(3.0, 0.0, 0.0, 13.0, 16.0, 1.0)),
                        Direction.SOUTH,
                        VoxelShapes.union((VoxelShape) DIRECTION_TO_SIDE_SHAPE.get(Direction.SOUTH),
                                Block.createCuboidShape(3.0, 0.0, 15.0, 13.0, 16.0, 16.0)),
                        Direction.EAST,
                        VoxelShapes.union((VoxelShape) DIRECTION_TO_SIDE_SHAPE.get(Direction.EAST),
                                Block.createCuboidShape(15.0, 0.0, 3.0, 16.0, 16.0, 13.0)),
                        Direction.WEST,
                        VoxelShapes.union((VoxelShape) DIRECTION_TO_SIDE_SHAPE.get(Direction.WEST),
                                Block.createCuboidShape(0.0, 0.0, 3.0, 1.0, 16.0, 13.0))));
        SHAPES = Maps.newHashMap();
        COLORS = new Vec3d[] {new Vec3d(0.09f, 0.69f, 0.82f), new Vec3d(1.f, 1.f, 0.13f)};
    }

    public BlockIndicatorLight(String name, Settings settings) {
        super(name, settings);
        this.setDefaultState(
                this.stateManager.getDefaultState().with(WIRE_CONNECTION_NORTH, WireConnection.NONE)
                        .with(WIRE_CONNECTION_EAST, WireConnection.NONE)
                        .with(WIRE_CONNECTION_SOUTH, WireConnection.NONE)
                        .with(WIRE_CONNECTION_WEST, WireConnection.NONE).with(POWERED, false));
        UnmodifiableIterator shapeIterator = this.getStateManager().getStates().iterator();

        while (shapeIterator.hasNext()) {
            BlockState blockState = (BlockState) shapeIterator.next();
            if (blockState.get(POWERED) == false) {
                SHAPES.put(blockState, this.getShapeForState(blockState));
            }
        }
    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> builder) {
        builder.add(WIRE_CONNECTION_NORTH, WIRE_CONNECTION_EAST, WIRE_CONNECTION_SOUTH,
                WIRE_CONNECTION_WEST, POWERED);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos,
            ShapeContext context) {
        return (VoxelShape) SHAPES.get(state.with(POWERED, false));
    }

    private VoxelShape getShapeForState(BlockState state) {
        VoxelShape voxelShape = DOT_SHAPE;
        Iterator var3 = Type.HORIZONTAL.iterator();

        while (var3.hasNext()) {
            Direction direction = (Direction) var3.next();
            WireConnection wireConnection = (WireConnection) state
                    .get((Property) DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction));
            if (wireConnection == WireConnection.SIDE) {
                voxelShape = VoxelShapes.union(voxelShape,
                        (VoxelShape) DIRECTION_TO_SIDE_SHAPE.get(direction));
            } else if (wireConnection == WireConnection.UP) {
                voxelShape = VoxelShapes.union(voxelShape,
                        (VoxelShape) DIRECTION_TO_UP_SHAPE.get(direction));
            }
        }

        return voxelShape;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getPlacementState(ctx.getWorld(), getDefaultState(), ctx.getBlockPos());
    }

    private BlockState getPlacementState(BlockView view, BlockState state, BlockPos pos) {
        Direction[] directions = Direction.values();
        boolean makeVerticalConnection = false;
        boolean verticalConnection = false;
        for (Direction dir : directions) {
            BlockState otherBlockState = view.getBlockState(pos.offset(dir));
            if (otherBlockState.getBlock().equals(this)) {
                if (makeVerticalConnection && !verticalConnection) {
                    verticalConnection = true;
                    state = state.with(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(dir),
                            WireConnection.UP);
                }
                if (dir == Direction.UP || dir == Direction.DOWN) {
                    makeVerticalConnection = true;
                } else {
                    state = state.with(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(dir),
                            WireConnection.SIDE);
                }
            }
        }

        return state;
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState,
            boolean notify) {
        Direction[] directions = Direction.values();
        boolean makeVerticalConnection = false;
        boolean verticalConnection = false;
        for (Direction dir : directions) {
            BlockState otherBlockState = world.getBlockState(pos.offset(dir));
            if (otherBlockState.getBlock().equals(this)) {
                if (dir == Direction.UP || dir == Direction.DOWN) {
                    makeVerticalConnection = true;
                } else {
                    world.setBlockState(pos.offset(dir),
                            otherBlockState.with(
                                    DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(dir.getOpposite()),
                                    WireConnection.SIDE));
                    if (makeVerticalConnection && !verticalConnection) {
                        world.setBlockState(pos.offset(dir), otherBlockState.with(
                                DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(dir.getOpposite()),
                                WireConnection.UP));
                        verticalConnection = true;
                    }
                }
            }
        }
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock,
            BlockPos sourcePos, boolean notify) {
        boolean isReceivingPower =
                world.isReceivingRedstonePower(pos) || world.isReceivingRedstonePower(pos.up());
        boolean isAlreadyPowered = (Boolean) state.get(POWERED);
        if (isReceivingPower && !isAlreadyPowered) {
            world.scheduleBlockTick(pos, this, 4);
            world.setBlockState(pos, (BlockState) state.with(POWERED, true), 2);
            setPowerRecursive(world, pos, true);
        } else if (isAlreadyPowered) {
            world.setBlockState(pos, (BlockState) state.with(POWERED, false), 2);
            setPowerRecursive(world, pos, false);
        }
    }

    private void updateNeighbors(World world, BlockPos pos) {
        if (world.getBlockState(pos).isOf(this)) {
            world.updateNeighborsAlways(pos, this);
            Direction[] var3 = Direction.values();
            int var4 = var3.length;

            for (int var5 = 0; var5 < var4; ++var5) {
                Direction direction = var3[var5];
                world.updateNeighborsAlways(pos.offset(direction), this);
            }

        }
    }

    // TODO notify neighbors
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState,
            boolean moved) {
        System.out.println("onStateReplaced");
    }

    public void setPowerRecursive(World world, BlockPos pos, boolean powered) {
        BlockState state = world.getBlockState(pos);
        if (state.getBlock().equals(this)) {
            world.setBlockState(pos, (BlockState) state.with(POWERED, powered), 2);
            Direction[] directions = Direction.values();
            for (Direction dir : directions) {
                BlockState otherBlockState = world.getBlockState(pos.offset(dir));
                if (otherBlockState.getBlock().equals(this)) {
                    if (otherBlockState.get(POWERED) != powered) {
                        setPowerRecursive(world, pos.offset(dir), powered);
                    }
                }
            }
        }
    }

    @Override
    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos,
            Direction direction) {
        return state.getWeakRedstonePower(world, pos, direction);
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos,
            Direction direction) {
        return state.get(POWERED) ? 15 : 0;
    }

}
