package com.cemi.block;

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
                    Direction.NORTH, Block.createCuboidShape(3, 0, 0, 13, 16, 1),
                    Direction.SOUTH, Block.createCuboidShape(3, 0, 15, 13, 16, 16),
                    Direction.WEST, Block.createCuboidShape(0, 0, 3, 1, 16, 13),
                    Direction.EAST, Block.createCuboidShape(15, 0, 3, 16, 16, 13)),

            BlockFace.CEILING, Map.of(
                    Direction.NORTH, Block.createCuboidShape(3, 0, 0, 13, 16, 1),
                    Direction.SOUTH, Block.createCuboidShape(3, 0, 15, 13, 16, 16),
                    Direction.WEST, Block.createCuboidShape(0, 0, 3, 1, 16, 13),
                    Direction.EAST, Block.createCuboidShape(15, 0, 3, 16, 16, 13)),

            BlockFace.WALL, Map.of(
                    Direction.NORTH, Block.createCuboidShape(3, 0, 15, 13, 3, 16), // down
                    Direction.SOUTH, Block.createCuboidShape(3, 13, 15, 13, 16, 16), // up
                    Direction.WEST, Block.createCuboidShape(13, 3, 15, 16, 13, 16), // left-up
                    Direction.EAST, Block.createCuboidShape(0, 3, 15, 3, 13, 16) // right-up
            ));

    // --------------------
    // 2. Placement logic
    // --------------------

    private IndicatorLightConnection getConnection(
            BlockView world, BlockPos pos, Direction dir, BlockState state) {

        BlockPos sidePos = pos.offset(dir);
        BlockState sideState = world.getBlockState(sidePos);

        // 1. Direct horizontal connection
        if (connectsTo(sideState, dir)) {
            return IndicatorLightConnection.SIDE;
        }

        if (sideState.isSolidBlock(world, sidePos)) {
            return IndicatorLightConnection.NONE;
        }

        BlockFace face = state.get(FACE);

        // 2. Vertical / "up" connection depends on mounting face
        BlockPos upPos = null;

        if (face == BlockFace.FLOOR) {
            // Redstone goes down the block edge
            upPos = sidePos.down();
        } else if (face == BlockFace.CEILING) {
            // Redstone goes up the block edge
            upPos = sidePos.up();
        } else if (face == BlockFace.WALL) {
            // For walls, "up" means behind the wall
            Direction wallFacing = state.get(FACING);
            upPos = sidePos.offset(wallFacing.getOpposite());
        }

        if (upPos != null) {
            BlockState upState = world.getBlockState(upPos);
            if (connectsTo(upState, dir)) {
                return IndicatorLightConnection.UP;
            }
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
        BlockFace face = switch (ctx.getSide()) {
            case UP -> BlockFace.FLOOR;
            case DOWN -> BlockFace.CEILING;
            default -> BlockFace.WALL;
        };

        Direction facing = face == BlockFace.WALL
                ? ctx.getSide().getOpposite()
                : ctx.getHorizontalPlayerFacing();

        BlockState state = getDefaultState()
                .with(FACE, face)
                .with(FACING, facing);

        for (Direction dir : Direction.Type.HORIZONTAL) {
            state = state.with(
                    DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(dir),
                    getConnection(ctx.getWorld(), ctx.getBlockPos(), dir, state));
        }

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

        // Vertical changes only matter for support
        if (direction == Direction.UP || direction == Direction.DOWN) {
            return state;
        }

        // Only horizontal directions affect connections
        if (!direction.getAxis().isHorizontal()) {
            return state;
        }

        IndicatorLightConnection newConn = getConnection(world, pos, direction, state);

        EnumProperty<IndicatorLightConnection> prop = DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction);

        if (state.get(prop) == newConn) {
            return state;
        }

        return state.with(prop, newConn);
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

        for (Direction dir : Direction.Type.HORIZONTAL) {
            IndicatorLightConnection conn = state.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(dir));

            if (conn == IndicatorLightConnection.NONE)
                continue;

            Direction localDir = dir;
            if (face == BlockFace.WALL) {
                localDir = toWallLocal(dir, facing);
            }

            VoxelShape arm = getConnectionShape(face, localDir, conn);

            if (face == BlockFace.WALL) {
                arm = rotateShape(arm, facing);
            }

            shape = VoxelShapes.union(shape, arm);
        }

        if (face == BlockFace.WALL) {
            shape = VoxelShapes.union(shape, getWallVerticalShape(state, world, pos));
        }

        return shape;
    }

    private VoxelShape getWallVerticalShape(
            BlockState state, BlockView world, BlockPos pos) {

        Direction facing = state.get(FACING);
        VoxelShape result = VoxelShapes.empty();

        // Above
        BlockPos upPos = pos.up();
        BlockState upState = world.getBlockState(upPos);
        if (upState.isOf(this) && upState.get(FACE) == BlockFace.WALL
                && upState.get(FACING) == facing) {

            VoxelShape up = UP_SHAPES.get(BlockFace.WALL).get(Direction.SOUTH);
            result = VoxelShapes.union(result, rotateShape(up, facing));
        }

        // Below
        BlockPos downPos = pos.down();
        BlockState downState = world.getBlockState(downPos);
        if (downState.isOf(this) && downState.get(FACE) == BlockFace.WALL
                && downState.get(FACING) == facing) {

            VoxelShape down = UP_SHAPES.get(BlockFace.WALL).get(Direction.NORTH);
            result = VoxelShapes.union(result, rotateShape(down, facing));
        }

        return result;
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

    private VoxelShape getConnectionShape(
            BlockFace face, Direction dir, IndicatorLightConnection conn) {

        if (conn == IndicatorLightConnection.SIDE) {
            Map<Direction, VoxelShape> byDir = SIDE_SHAPES.get(face);
            Objects.requireNonNull(byDir, "Missing SIDE_SHAPES for face " + face);
            return byDir.get(dir);
        }

        if (conn == IndicatorLightConnection.UP) {
            Map<Direction, VoxelShape> byDir = UP_SHAPES.get(face);
            Objects.requireNonNull(byDir, "Missing UP_SHAPES for face " + face);
            return byDir.get(dir);
        }

        return VoxelShapes.empty();
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
                WIRE_CONNECTION_SOUTH, WIRE_CONNECTION_WEST, POWERED });
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

            SIDE[IndicatorLightConnection.UP.ordinal()] = 1;
            SIDE[IndicatorLightConnection.SIDE.ordinal()] = 2;
            SIDE[IndicatorLightConnection.NONE.ordinal()] = 3;
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
