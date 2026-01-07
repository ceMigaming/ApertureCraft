package com.cemi.block;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import com.cemi.block.entity.IndicatorLightBlockEntity;
import com.cemi.block.enums.IndicatorLightConnection;
import com.cemi.state.property.ApertureProperties;
import com.cemi.util.VoxelShapeUtils;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.serialization.MapCodec;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ObserverBlock;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Type;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

@SuppressWarnings({ "deprecation", "null" })
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
    protected static final int field_31222 = 1;
    protected static final int field_31223 = 3;
    protected static final int field_31224 = 13;
    protected static final int field_31225 = 3;
    protected static final int field_31226 = 13;
    private boolean wiresGivePower = true;

    private static final Map<BlockFace, VoxelShape> BASE_SHAPE = Map.of(
            BlockFace.FLOOR, Block.createCuboidShape(3, 0, 3, 13, 1, 13),
            BlockFace.CEILING, Block.createCuboidShape(3, 15, 3, 13, 16, 13),
            BlockFace.WALL, Block.createCuboidShape(3, 3, 15, 13, 13, 16) // north wall
    );

    private static final VoxelShape WALL_UP_SHAPE = Block.createCuboidShape(3, 0, 15, 13, 16, 16);
    private static final VoxelShape FLOOR_VERTICAL_SHAPE = Block.createCuboidShape(
            7, 1, 7, // start just above the base plate
            9, 16, 9 // up to top of block
    );

    private static final VoxelShape CEILING_VERTICAL_SHAPE = Block.createCuboidShape(
            7, 0, 7, // bottom of block
            9, 15, 9 // stop just before ceiling plate
    );

    // Wall vertical arms BEFORE rotation (facing NORTH)
    private static final VoxelShape WALL_UP_ARM = Block.createCuboidShape(
            3, 8, 15,
            13, 16, 16);

    private static final VoxelShape WALL_DOWN_ARM = Block.createCuboidShape(
            3, 0, 15,
            13, 8, 16);

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
                    Direction.SOUTH, Block.createCuboidShape(3, 3, 14, 13, 13, 15), // forward (away from wall)
                    Direction.NORTH, Block.createCuboidShape(3, 3, 15, 13, 13, 16), // backward (into wall, rarely used)
                    Direction.EAST, Block.createCuboidShape(0, 3, 15, 3, 13, 16), // right
                    Direction.WEST, Block.createCuboidShape(13, 3, 15, 16, 13, 16) // left
            ));

    private static final Map<BlockFace, Map<Direction, VoxelShape>> UP_SHAPES = Map.of(
            BlockFace.FLOOR, Map.of(
                    Direction.NORTH,
                    VoxelShapes.union((VoxelShape) SIDE_SHAPES.get(BlockFace.FLOOR).get(Direction.NORTH),
                            Block.createCuboidShape(3, 0, 0, 13, 16, 1)),
                    Direction.SOUTH,
                    VoxelShapes.union((VoxelShape) SIDE_SHAPES.get(BlockFace.FLOOR).get(Direction.SOUTH),
                            Block.createCuboidShape(3, 0, 15, 13, 16, 16)),
                    Direction.WEST,
                    VoxelShapes.union((VoxelShape) SIDE_SHAPES.get(BlockFace.FLOOR).get(Direction.WEST),
                            Block.createCuboidShape(0, 0, 3, 1, 16, 13)),
                    Direction.EAST,
                    VoxelShapes.union((VoxelShape) SIDE_SHAPES.get(BlockFace.FLOOR).get(Direction.EAST),
                            Block.createCuboidShape(15, 0, 3, 16, 16, 13))),

            BlockFace.CEILING, Map.of(
                    Direction.NORTH,
                    VoxelShapes.union((VoxelShape) SIDE_SHAPES.get(BlockFace.CEILING).get(Direction.NORTH),
                            Block.createCuboidShape(3, 0, 0, 13, 16, 1)),
                    Direction.SOUTH,
                    VoxelShapes.union((VoxelShape) SIDE_SHAPES.get(BlockFace.CEILING).get(Direction.SOUTH),
                            Block.createCuboidShape(3, 0, 15, 13, 16, 16)),
                    Direction.WEST,
                    VoxelShapes.union((VoxelShape) SIDE_SHAPES.get(BlockFace.CEILING).get(Direction.WEST),
                            Block.createCuboidShape(0, 0, 3, 1, 16, 13)),
                    Direction.EAST,
                    VoxelShapes.union((VoxelShape) SIDE_SHAPES.get(BlockFace.CEILING).get(Direction.EAST),
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

    // --------------------
    // 2. Placement logic
    // --------------------

    private IndicatorLightConnection getConnection(
            BlockView world, BlockPos pos, Direction dir, BlockState state) {

        BlockPos sidePos = pos.offset(dir);
        BlockState sideState = world.getBlockState(sidePos);

        BlockFace face = state.get(FACE);

        // Same-face horizontal connection
        if (sideState.isOf(this)
                && sideState.get(FACE) == face
                && connectsTo(sideState, dir)) {
            return IndicatorLightConnection.SIDE;
        }

        // Solid blocks block floor / ceiling routing
        if (sideState.isSolidBlock(world, sidePos) && face != BlockFace.WALL) {
            return IndicatorLightConnection.NONE;
        }

        // Generic redstone-style horizontal connection
        if (connectsTo(sideState, dir)) {
            return IndicatorLightConnection.SIDE;
        }

        return IndicatorLightConnection.NONE;
    }

    public IndicatorLightBlock(AbstractBlock.Settings settings) {
        super("indicator_light", settings);
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
            // Clicked a wall — prefer FLOOR if supported
            BlockState below = world.getBlockState(pos.down());
            if (below.isSolidBlock(world, pos.down())) {
                face = BlockFace.FLOOR;
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

        // Horizontal connections
        for (Direction dir : Direction.Type.HORIZONTAL) {
            state = state.with(
                    DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(dir),
                    getConnection(world, pos, dir, state));
        }

        // Vertical connections
        state = state
                .with(UP, hasVerticalConnection(world, pos, Direction.UP, state))
                .with(DOWN, hasVerticalConnection(world, pos, Direction.DOWN, state));

        return state;
    }

    // --------------------
    // 3. Neighbor update logic
    // --------------------

    @Override
    public BlockState getStateForNeighborUpdate(
            BlockState state,
            Direction direction,
            BlockState neighborState,
            WorldAccess world,
            BlockPos pos,
            BlockPos neighborPos) {

        BlockState updated = state;

        // Horizontal update
        if (direction.getAxis().isHorizontal()) {
            EnumProperty<IndicatorLightConnection> prop = DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction);

            IndicatorLightConnection newConn = getConnection(world, pos, direction, state);

            if (state.get(prop) != newConn) {
                updated = updated.with(prop, newConn);
            }
        }

        // Vertical update (directional!)
        if (direction == Direction.UP || direction == Direction.DOWN) {
            updated = updated
                    .with(UP, hasVerticalConnection(world, pos, Direction.UP, updated))
                    .with(DOWN, hasVerticalConnection(world, pos, Direction.DOWN, updated));
        }

        return updated;
    }

    public void prepare(BlockState state, WorldAccess world, BlockPos pos, int flags,
            int maxUpdateDepth) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        Iterator<Direction> var7 = Type.HORIZONTAL.iterator();
        while (var7.hasNext()) {
            Direction direction = (Direction) var7.next();
            IndicatorLightConnection conn = (IndicatorLightConnection) state
                    .get((Property<IndicatorLightConnection>) DIRECTION_TO_WIRE_CONNECTION_PROPERTY
                            .get(direction));
            if (conn != IndicatorLightConnection.NONE
                    && !world.getBlockState(mutable.set(pos, direction)).isOf(this)) {
                mutable.move(Direction.DOWN);
                BlockState blockState = world.getBlockState(mutable);
                if (blockState.isOf(this)) {
                    BlockPos blockPos = mutable.offset(direction.getOpposite());
                    world.replaceWithStateForNeighborUpdate(direction.getOpposite(),
                            world.getBlockState(blockPos), mutable, blockPos, flags,
                            maxUpdateDepth);
                }
                mutable.set(pos, direction).move(Direction.UP);
                BlockState blockState2 = world.getBlockState(mutable);
                if (blockState2.isOf(this)) {
                    BlockPos blockPos2 = mutable.offset(direction.getOpposite());
                    world.replaceWithStateForNeighborUpdate(direction.getOpposite(),
                            world.getBlockState(blockPos2), mutable, blockPos2, flags,
                            maxUpdateDepth);
                }
            }
        }
    }

    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState,
            boolean notify) {
        if (!oldState.isOf(state.getBlock()) && !world.isClient) {
            this.update(world, pos, state);
            Iterator<Direction> var6 = Type.VERTICAL.iterator();
            while (var6.hasNext()) {
                Direction direction = (Direction) var6.next();
                world.updateNeighborsAlways(pos.offset(direction), this);
            }
            this.updateOffsetNeighbors(world, pos);
        }
    }

    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState,
            boolean moved) {
        if (!moved && !state.isOf(newState.getBlock())) {
            super.onStateReplaced(state, world, pos, newState, moved);
            if (!world.isClient) {
                Direction[] var6 = Direction.values();
                int var7 = var6.length;
                for (int var8 = 0; var8 < var7; ++var8) {
                    Direction direction = var6[var8];
                    world.updateNeighborsAlways(pos.offset(direction), this);
                }
                this.update(world, pos, state);
                this.updateOffsetNeighbors(world, pos);
            }
        }
    }

    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock,
            BlockPos sourcePos, boolean notify) {
        if (!world.isClient) {
            if (state.canPlaceAt(world, pos)) {
                this.update(world, pos, state);
            } else {
                dropStacks(state, world, pos);
                world.removeBlock(pos, false);
            }
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

    private void updateOffsetNeighbors(World world, BlockPos pos) {
        Iterator<Direction> var3 = Type.HORIZONTAL.iterator();
        Direction direction;
        while (var3.hasNext()) {
            direction = (Direction) var3.next();
            this.updateNeighbors(world, pos.offset(direction));
        }
        var3 = Type.HORIZONTAL.iterator();
        while (var3.hasNext()) {
            direction = (Direction) var3.next();
            BlockPos blockPos = pos.offset(direction);
            if (world.getBlockState(blockPos).isSolidBlock(world, blockPos)) {
                this.updateNeighbors(world, blockPos.up());
            } else {
                this.updateNeighbors(world, blockPos.down());
            }
        }
    }

    // --------------------
    // 4. Voxel shape generation
    // --------------------
    @Override
    public VoxelShape getOutlineShape(
            BlockState state, BlockView world, BlockPos pos, ShapeContext context) {

        BlockFace face = state.get(FACE);
        Direction facing = state.get(FACING);

        VoxelShape shape = BASE_SHAPE.get(face);

        if (face == BlockFace.WALL) {
            shape = rotateShape(shape, facing);
        }

        // Horizontal arms
        for (Direction dir : Direction.Type.HORIZONTAL) {
            if (state.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(dir)) != IndicatorLightConnection.SIDE) {
                continue;
            }

            Direction localDir = face == BlockFace.WALL
                    ? toWallLocal(dir, facing)
                    : dir;

            VoxelShape arm = SIDE_SHAPES.get(face).get(localDir);

            if (face == BlockFace.WALL) {
                arm = rotateShape(arm, facing);
            }

            shape = VoxelShapes.union(shape, arm);
        }

        // Vertical arms (FACE-aware)
        switch (face) {
            case FLOOR -> {
                if (state.get(UP)) {
                    Direction wallFacing = getUpWallFacing(world, pos);
                    if (wallFacing != null) {
                        // vertical strip on wall
                        shape = VoxelShapes.union(
                                shape,
                                FLOOR_UP_WALL_SHAPES.get(wallFacing));

                        // short horizontal arm from dot → wall
                        shape = VoxelShapes.union(
                                shape,
                                SIDE_SHAPES.get(BlockFace.FLOOR).get(wallFacing));
                    }
                }
            }

            case CEILING -> {
                if (state.get(DOWN)) {
                    Direction wallFacing = getDownWallFacing(world, pos);
                    if (wallFacing != null) {
                        shape = VoxelShapes.union(
                                shape,
                                CEILING_DOWN_WALL_SHAPES.get(wallFacing));

                        shape = VoxelShapes.union(
                                shape,
                                SIDE_SHAPES.get(BlockFace.CEILING).get(wallFacing.getOpposite()));
                    }
                }
            }

            case WALL -> {
                if (state.get(UP)) {
                    shape = VoxelShapes.union(
                            shape,
                            rotateShape(WALL_UP_ARM, facing));
                }

                if (state.get(DOWN)) {
                    shape = VoxelShapes.union(
                            shape,
                            rotateShape(WALL_DOWN_ARM, facing));
                }
            }
        }

        return shape;
    }

    @Nullable
    private Direction getUpWallFacing(BlockView world, BlockPos pos) {
        BlockState above = world.getBlockState(pos.up());

        if (above.isOf(this) && above.get(FACE) == BlockFace.WALL) {
            return above.get(FACING);
        }

        return null;
    }

    @Nullable
    private Direction getDownWallFacing(BlockView world, BlockPos pos) {
        BlockState below = world.getBlockState(pos.down());
        if (below.isOf(this) && below.get(FACE) == BlockFace.WALL) {
            // CEILING → WALL needs inversion
            return below.get(FACING).getOpposite();
        }
        return null;
    }

    private Direction toWallLocal(Direction worldDir, Direction facing) {
        return switch (facing) {
            case NORTH -> worldDir;
            case SOUTH -> worldDir.getOpposite();
            case EAST -> worldDir.rotateYCounterclockwise();
            case WEST -> worldDir.rotateYClockwise();
            default -> worldDir;
        };
    }

    // private VoxelShape getConnectionShape(
    // BlockFace face,
    // Direction dir,
    // IndicatorLightConnection conn) {

    // return switch (conn) {
    // case SIDE -> SIDE_SHAPES.get(face).get(dir);

    // case UP_FLOOR -> UP_SHAPES.get(BlockFace.FLOOR).get(dir);
    // case UP_CEILING -> UP_SHAPES.get(BlockFace.CEILING).get(dir);

    // case UP_WALL -> WALL_UP_SHAPE;

    // default -> VoxelShapes.empty();
    // };
    // }

    private boolean hasVerticalConnection(
            BlockView world, BlockPos pos, Direction verticalDir, BlockState state) {

        BlockFace face = state.get(FACE);
        BlockPos targetPos = pos.offset(verticalDir);
        BlockState targetState = world.getBlockState(targetPos);

        if (!targetState.isOf(this)) {
            return false;
        }

        BlockFace otherFace = targetState.get(FACE);

        // ----------------------------
        // 1. SAME-FACE vertical stacks
        // ----------------------------
        if (face == otherFace) {
            return switch (face) {
                case FLOOR -> verticalDir == Direction.UP;
                case CEILING -> verticalDir == Direction.DOWN;
                case WALL -> true; // wall ↕ wall always allowed
            };
        }

        // --------------------------------
        // 2. CROSS-FACE vertical transitions
        // --------------------------------

        // FLOOR → WALL (above)
        if (face == BlockFace.FLOOR
                && verticalDir == Direction.UP
                && otherFace == BlockFace.WALL) {
            return true;
        }

        // WALL → FLOOR (below)
        if (face == BlockFace.WALL
                && verticalDir == Direction.DOWN
                && otherFace == BlockFace.FLOOR) {
            return true;
        }

        // CEILING → WALL (below)
        if (face == BlockFace.CEILING
                && verticalDir == Direction.DOWN
                && otherFace == BlockFace.WALL) {
            return true;
        }

        // WALL → CEILING (above)
        if (face == BlockFace.WALL
                && verticalDir == Direction.UP
                && otherFace == BlockFace.CEILING) {
            return true;
        }

        return false;
    }

    // --------------------
    // 5. Redstone power propagation
    // --------------------
    private void update(World world, BlockPos pos, BlockState state) {
        int i = this.getReceivedRedstonePower(world, pos);
        IndicatorLightBlockEntity blockEntity = (IndicatorLightBlockEntity) world.getBlockEntity(pos);
        if (blockEntity != null && blockEntity.getPower() != i) {
            if (world.getBlockState(pos) == state) {
                blockEntity.setPower(i);
                world.setBlockState(pos, state.with(POWERED, i > 0), 2);
            }
            Set<BlockPos> set = Sets.newHashSet();
            set.add(pos);
            Direction[] var6 = Direction.values();
            int var7 = var6.length;
            for (int var8 = 0; var8 < var7; ++var8) {
                Direction direction = var6[var8];
                set.add(pos.offset(direction));
            }
            Iterator<BlockPos> var10 = set.iterator();
            while (var10.hasNext()) {
                BlockPos blockPos = (BlockPos) var10.next();
                world.updateNeighborsAlways(blockPos, this);
            }
        }
    }

    private int getReceivedRedstonePower(World world, BlockPos pos) {
        this.wiresGivePower = false;
        int i = world.getReceivedRedstonePower(pos);
        this.wiresGivePower = true;
        int j = 0;
        if (i < Integer.MAX_VALUE) {
            for (Direction direction : Direction.values()) {
                BlockPos blockPos = pos.offset(direction);
                j = Math.max(j, this.increasePower(world, blockPos));
            }
            return Math.max(i, j - 1);
        } else {
            return Math.max(i, j - 1);
        }
    }

    private int increasePower(World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof IndicatorLightBlockEntity iLBlockEntity) {
            return iLBlockEntity.getPower();
        }
        return 0;
    }

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

        IndicatorLightConnection conn = state.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction));

        return conn.isConnected() ? power : 0;
    }

    protected static boolean connectsTo(BlockState state) {
        return connectsTo(state, (Direction) null);
    }

    protected static boolean connectsTo(BlockState state, @Nullable Direction dir) {
        if (state.isOf(Blocks.REDSTONE_WIRE) || state.isOf(ApertureBlocks.INDICATOR_LIGHT)) {
            return true;
        } else if (state.isOf(Blocks.REPEATER)) {
            Direction direction = (Direction) state.get(RepeaterBlock.FACING);
            return direction == dir || direction.getOpposite() == dir;
        } else if (state.isOf(Blocks.OBSERVER)) {
            return dir == state.get(ObserverBlock.FACING);
        } else {
            return state.emitsRedstonePower() && dir != null;
        }
    }

    public boolean emitsRedstonePower(BlockState state) {
        return this.wiresGivePower;
    }

    // --------------------
    // 6. Rotation / mirroring
    // --------------------
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        switch (InnerIndicatorLightBlock.ROTATION[rotation.ordinal()]) {
            case 1:
                return (BlockState) ((BlockState) ((BlockState) ((BlockState) state.with(
                        WIRE_CONNECTION_NORTH, (IndicatorLightConnection) state.get(WIRE_CONNECTION_SOUTH)))
                        .with(WIRE_CONNECTION_EAST,
                                (IndicatorLightConnection) state.get(WIRE_CONNECTION_WEST)))
                        .with(
                                WIRE_CONNECTION_SOUTH,
                                (IndicatorLightConnection) state.get(WIRE_CONNECTION_NORTH)))
                        .with(WIRE_CONNECTION_WEST,
                                (IndicatorLightConnection) state
                                        .get(WIRE_CONNECTION_EAST));
            case 2:
                return (BlockState) ((BlockState) ((BlockState) ((BlockState) state.with(
                        WIRE_CONNECTION_NORTH, (IndicatorLightConnection) state.get(WIRE_CONNECTION_EAST)))
                        .with(WIRE_CONNECTION_EAST,
                                (IndicatorLightConnection) state.get(WIRE_CONNECTION_SOUTH)))
                        .with(
                                WIRE_CONNECTION_SOUTH,
                                (IndicatorLightConnection) state.get(WIRE_CONNECTION_WEST)))
                        .with(WIRE_CONNECTION_WEST,
                                (IndicatorLightConnection) state.get(
                                        WIRE_CONNECTION_NORTH));
            case 3:
                return (BlockState) ((BlockState) ((BlockState) ((BlockState) state.with(
                        WIRE_CONNECTION_NORTH, (IndicatorLightConnection) state.get(WIRE_CONNECTION_WEST)))
                        .with(WIRE_CONNECTION_EAST,
                                (IndicatorLightConnection) state.get(WIRE_CONNECTION_NORTH)))
                        .with(
                                WIRE_CONNECTION_SOUTH,
                                (IndicatorLightConnection) state.get(WIRE_CONNECTION_EAST)))
                        .with(WIRE_CONNECTION_WEST,
                                (IndicatorLightConnection) state.get(
                                        WIRE_CONNECTION_SOUTH));
            default:
                return state;
        }
    }

    public BlockState mirror(BlockState state, BlockMirror mirror) {
        switch (InnerIndicatorLightBlock.AXIS[mirror.ordinal()]) {
            case 1:
                return (BlockState) ((BlockState) state.with(WIRE_CONNECTION_NORTH,
                        (IndicatorLightConnection) state.get(WIRE_CONNECTION_SOUTH))).with(
                                WIRE_CONNECTION_SOUTH,
                                (IndicatorLightConnection) state.get(WIRE_CONNECTION_NORTH));
            case 2:
                return (BlockState) ((BlockState) state.with(WIRE_CONNECTION_EAST,
                        (IndicatorLightConnection) state.get(WIRE_CONNECTION_WEST))).with(
                                WIRE_CONNECTION_WEST,
                                (IndicatorLightConnection) state.get(WIRE_CONNECTION_EAST));
            default:
                return super.mirror(state, mirror);
        }
    }

    // --------------------
    // 7. Debug logging (and onUse)
    // --------------------
    @Override
    public ActionResult onUse(
            BlockState state, World world, BlockPos pos,
            PlayerEntity player, Hand hand, BlockHitResult hit) {

        if (!player.getAbilities().allowModifyWorld) {
            return ActionResult.PASS;
        }

        BlockState newState = state;

        for (Direction dir : Direction.Type.HORIZONTAL) {
            EnumProperty<IndicatorLightConnection> prop = DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(dir);

            newState = newState.with(
                    prop,
                    state.get(prop) == IndicatorLightConnection.NONE
                            ? IndicatorLightConnection.SIDE
                            : IndicatorLightConnection.NONE);
        }

        if (newState != state) {
            world.setBlockState(pos, newState, Block.NOTIFY_ALL);
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    // --------------------
    // Utility and static methods
    // --------------------
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[] { FACE, FACING, WIRE_CONNECTION_NORTH, WIRE_CONNECTION_EAST,
                WIRE_CONNECTION_SOUTH, WIRE_CONNECTION_WEST, POWERED, UP, DOWN });
    }

    public static class InnerIndicatorLightBlock {
        public static int[] AXIS = new int[BlockMirror.values().length];
        public static int[] ROTATION = new int[BlockRotation.values().length];
        public static int[] SIDE = new int[IndicatorLightConnection.values().length];

        static {
            AXIS[BlockMirror.LEFT_RIGHT.ordinal()] = 1;
            AXIS[BlockMirror.FRONT_BACK.ordinal()] = 2;

            ROTATION[BlockRotation.CLOCKWISE_180.ordinal()] = 1;
            ROTATION[BlockRotation.COUNTERCLOCKWISE_90.ordinal()] = 2;
            ROTATION[BlockRotation.CLOCKWISE_90.ordinal()] = 3;

            SIDE[IndicatorLightConnection.SIDE.ordinal()] = 1;
            SIDE[IndicatorLightConnection.NONE.ordinal()] = 2;
        }
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new IndicatorLightBlockEntity(pos, state);
    }

    private static VoxelShape rotateShape(VoxelShape shape, Direction facing) {
        return switch (facing) {
            case SOUTH -> shape;
            case WEST -> VoxelShapeUtils.rotateShape(Direction.SOUTH, Direction.WEST, shape);
            case NORTH -> VoxelShapeUtils.rotateShape(Direction.SOUTH, Direction.NORTH, shape);
            case EAST -> VoxelShapeUtils.rotateShape(Direction.SOUTH, Direction.EAST, shape);
            default -> shape;
        };
    }
}
