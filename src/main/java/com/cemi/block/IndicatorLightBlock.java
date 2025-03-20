package com.cemi.block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.Nullable;
import com.cemi.block.entity.IndicatorLightBlockEntity;
import com.cemi.block.enums.IndicatorLightConnection;
import com.cemi.state.property.ApertureProperties;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
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
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
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
import net.minecraft.world.WorldView;

@SuppressWarnings("deprecation")
public class IndicatorLightBlock extends ApertureBlock implements BlockEntityProvider {
    public static final MapCodec<RedstoneWireBlock> CODEC = createCodec(RedstoneWireBlock::new);
    public static final EnumProperty<IndicatorLightConnection> WIRE_CONNECTION_NORTH;
    public static final EnumProperty<IndicatorLightConnection> WIRE_CONNECTION_EAST;
    public static final EnumProperty<IndicatorLightConnection> WIRE_CONNECTION_SOUTH;
    public static final EnumProperty<IndicatorLightConnection> WIRE_CONNECTION_WEST;
    // public static final IntProperty POWER;
    public static final BooleanProperty POWERED;
    public static final Map<Direction, EnumProperty<IndicatorLightConnection>> DIRECTION_TO_WIRE_CONNECTION_PROPERTY;
    protected static final int field_31222 = 1;
    protected static final int field_31223 = 3;
    protected static final int field_31224 = 13;
    protected static final int field_31225 = 3;
    protected static final int field_31226 = 13;
    private static final VoxelShape DOT_SHAPE;
    private static final Map<Direction, VoxelShape> DIRECTION_TO_SIDE_SHAPE;
    private static final Map<Direction, VoxelShape> DIRECTION_TO_UP_SHAPE;
    private static final Map<Direction, VoxelShape> DIRECTION_TO_VERTICAL_SHAPE;
    private static final Map<BlockState, VoxelShape> SHAPES;
    private final BlockState dotState;
    private boolean wiresGivePower = true;

    public MapCodec<RedstoneWireBlock> getCodec() {
        return CODEC;
    }

    public IndicatorLightBlock(AbstractBlock.Settings settings) {
        super("indicator_light", settings);
        this.setDefaultState(
                (BlockState) ((BlockState) ((BlockState) ((BlockState) ((BlockState) ((BlockState) this.stateManager
                        .getDefaultState()).with(WIRE_CONNECTION_NORTH, IndicatorLightConnection.NONE))
                        .with(WIRE_CONNECTION_EAST, IndicatorLightConnection.NONE))
                        .with(WIRE_CONNECTION_SOUTH, IndicatorLightConnection.NONE))
                        .with(WIRE_CONNECTION_WEST, IndicatorLightConnection.NONE))
                        .with(POWERED, false));
        // .with(POWER, 0));
        this.dotState = (BlockState) ((BlockState) ((BlockState) ((BlockState) this
                .getDefaultState().with(WIRE_CONNECTION_NORTH, IndicatorLightConnection.SIDE))
                .with(WIRE_CONNECTION_EAST, IndicatorLightConnection.SIDE))
                .with(WIRE_CONNECTION_SOUTH, IndicatorLightConnection.SIDE))
                .with(WIRE_CONNECTION_WEST, IndicatorLightConnection.SIDE);
        UnmodifiableIterator<BlockState> statesIterator = this.getStateManager().getStates().iterator();

        while (statesIterator.hasNext()) {
            BlockState blockState = (BlockState) statesIterator.next();
            // if ((Integer) blockState.get(POWER) == 0) {
            if ((Boolean) blockState.get(POWERED) == false) {
                SHAPES.put(blockState, this.getShapeForState(blockState));
            }
            // }
        }

    }

    private VoxelShape getShapeForState(BlockState state) {
        VoxelShape voxelShape = DOT_SHAPE;
        Iterator<Direction> var3 = Type.HORIZONTAL.iterator();

        while (var3.hasNext()) {
            Direction direction = (Direction) var3.next();
            IndicatorLightConnection conn = (IndicatorLightConnection) state
                    .get((Property<IndicatorLightConnection>) DIRECTION_TO_WIRE_CONNECTION_PROPERTY
                            .get(direction));
            // if (conn == IndicatorLightConnection.SIDE) {
            // voxelShape = VoxelShapes.union(voxelShape,
            // (VoxelShape) DIRECTION_TO_SIDE_SHAPE.get(direction));
            // } else if (conn == IndicatorLightConnection.UP) {
            // voxelShape = VoxelShapes.union(voxelShape,
            // (VoxelShape) DIRECTION_TO_UP_SHAPE.get(direction));
            // }
            switch (conn) {
                case SIDE:
                    voxelShape = VoxelShapes.union(voxelShape,
                            (VoxelShape) DIRECTION_TO_SIDE_SHAPE.get(direction));
                    break;
                case UP:
                    voxelShape = VoxelShapes.union(voxelShape,
                            (VoxelShape) DIRECTION_TO_UP_SHAPE.get(direction));
                    break;
                case SIDE_VERTICAL:
                    voxelShape = (VoxelShape) DIRECTION_TO_VERTICAL_SHAPE.get(direction);
                    break;
                default:
                    break;
            }
        }

        return voxelShape;
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos,
            ShapeContext context) {
        return (VoxelShape) SHAPES.get(state.with(POWERED, false)/* .with(POWER, 0) */);
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getPlacementState(ctx.getWorld(), this.dotState, ctx.getBlockPos(), ctx.getPlayer());
    }

    private BlockState getPlacementState(BlockView world, BlockState state, BlockPos pos,
            @Nullable PlayerEntity player) {
        boolean bl = isNotConnected(state);
        var newState = this.getDefaultWireState(world,
                (BlockState) this.getDefaultState().with(POWERED,
                        (Boolean) state.get(POWERED))/* .with(POWER, (Integer) state.get(POWER)) */,
                pos);
        if (bl && isNotConnected(newState)) {
            return newState;
        } else {
            boolean bl2 = ((IndicatorLightConnection) newState.get(WIRE_CONNECTION_NORTH)).isConnected();
            boolean bl3 = ((IndicatorLightConnection) newState.get(WIRE_CONNECTION_SOUTH)).isConnected();
            boolean bl4 = ((IndicatorLightConnection) newState.get(WIRE_CONNECTION_EAST)).isConnected();
            boolean bl5 = ((IndicatorLightConnection) newState.get(WIRE_CONNECTION_WEST)).isConnected();
            boolean bl6 = !bl2 && !bl3;
            boolean bl7 = !bl4 && !bl5;
            boolean hasBlockUnder = world.getBlockState(pos.down()).isSolidBlock(world, pos);
            boolean hasConnectionAbove = world.getBlockState(pos.up()).isOf(this);

            Direction newDir = Direction.NORTH;

            if (!hasBlockUnder) {
                state = state.with(WIRE_CONNECTION_NORTH, IndicatorLightConnection.NONE)
                        .with(WIRE_CONNECTION_EAST, IndicatorLightConnection.NONE)
                        .with(WIRE_CONNECTION_SOUTH, IndicatorLightConnection.NONE)
                        .with(WIRE_CONNECTION_WEST, IndicatorLightConnection.NONE);
                if (player != null
                        && world.getBlockState(pos.offset(player.getHorizontalFacing())).isSolidBlock(world, pos)) {
                    newDir = player.getHorizontalFacing();
                } else {
                    boolean allAir = true;
                    for (Direction dir : Type.HORIZONTAL) {
                        if (world.getBlockState(pos.offset(dir)).isSolidBlock(world, pos)) {
                            allAir = false;
                            newDir = dir;
                            break;
                        }
                    }
                    if (allAir) {
                        return Blocks.AIR.getDefaultState();
                    }
                }
                return state.with(
                        (Property<IndicatorLightConnection>) DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(newDir),
                        IndicatorLightConnection.SIDE_VERTICAL);
            }

            if (hasConnectionAbove) {
                state = state.with(WIRE_CONNECTION_NORTH, IndicatorLightConnection.NONE)
                        .with(WIRE_CONNECTION_EAST, IndicatorLightConnection.NONE)
                        .with(WIRE_CONNECTION_SOUTH, IndicatorLightConnection.NONE)
                        .with(WIRE_CONNECTION_WEST, IndicatorLightConnection.NONE);
                newDir = getDirectionFromBlockState(world.getBlockState(pos.up()));
                if (player != null
                        && world.getBlockState(pos.offset(player.getHorizontalFacing())).isSolidBlock(world, pos)) {
                    newDir = player.getHorizontalFacing();
                } else {
                    if (world.getBlockState(pos.offset(newDir)).isSolidBlock(world, pos)) {
                        return state.with(
                                (Property<IndicatorLightConnection>) DIRECTION_TO_WIRE_CONNECTION_PROPERTY
                                        .get(newDir),
                                IndicatorLightConnection.UP);
                    } else {
                        boolean allAir = true;
                        for (Direction dir : Type.HORIZONTAL) {
                            if (world.getBlockState(pos.offset(dir)).isSolidBlock(world, pos)) {
                                allAir = false;
                                newDir = dir;
                                break;
                            }
                        }
                        if (!allAir) {
                            if (world.getBlockState(pos.down()).isSolidBlock(world, pos.down())) {
                                return state.with(
                                        (Property<IndicatorLightConnection>) DIRECTION_TO_WIRE_CONNECTION_PROPERTY
                                                .get(newDir),
                                        IndicatorLightConnection.UP);
                            }
                            return state.with(
                                    (Property<IndicatorLightConnection>) DIRECTION_TO_WIRE_CONNECTION_PROPERTY
                                            .get(newDir),
                                    IndicatorLightConnection.SIDE_VERTICAL);
                        }
                    }
                }
            }

            if (!bl5 && bl6) {
                newState = (BlockState) newState.with(WIRE_CONNECTION_WEST, IndicatorLightConnection.SIDE);
            }

            if (!bl4 && bl6) {
                newState = (BlockState) newState.with(WIRE_CONNECTION_EAST, IndicatorLightConnection.SIDE);
            }

            if (!bl2 && bl7) {
                newState = (BlockState) newState.with(WIRE_CONNECTION_NORTH, IndicatorLightConnection.SIDE);
            }

            if (!bl3 && bl7) {
                newState = (BlockState) newState.with(WIRE_CONNECTION_SOUTH, IndicatorLightConnection.SIDE);
            }

            return newState;
        }
    }

    @Nullable
    private Direction getDirectionFromBlockState(BlockState blockState) {
        for (Direction direction : Type.HORIZONTAL) {
            var conn = blockState.get((Property<IndicatorLightConnection>) DIRECTION_TO_WIRE_CONNECTION_PROPERTY
                    .get(direction));
            if (Arrays.asList(IndicatorLightConnection.SIDE_VERTICAL,
                    IndicatorLightConnection.SIDE_DOWN_LEFT, IndicatorLightConnection.SIDE_DOWN_RIGHT,
                    IndicatorLightConnection.UP)
                    .contains(conn)) {
                return direction;
            }
        }
        return null;
    }

    private BlockState getDefaultWireState(BlockView world, BlockState state, BlockPos pos) {
        boolean bl = !world.getBlockState(pos.up()).isSolidBlock(world, pos);
        Iterator<Direction> var5 = Type.HORIZONTAL.iterator();

        while (var5.hasNext()) {
            Direction direction = (Direction) var5.next();
            if (!((IndicatorLightConnection) state
                    .get((Property<IndicatorLightConnection>) DIRECTION_TO_WIRE_CONNECTION_PROPERTY
                            .get(direction)))
                    .isConnected()) {
                IndicatorLightConnection IndicatorLightConnection = this.getRenderConnectionType(world, pos, pos,
                        direction,
                        bl);
                state = (BlockState) state
                        .with((Property<IndicatorLightConnection>) DIRECTION_TO_WIRE_CONNECTION_PROPERTY
                                .get(direction), IndicatorLightConnection);
            }
        }

        return state;
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction,
            BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        // if (direction == Direction.DOWN) {
        // return !this.canRunOnTop(world, neighborPos, neighborState)
        // ? Blocks.AIR.getDefaultState()
        // : state;
        // } else if (direction == Direction.UP) {
        // return this.getPlacementState(world, state, pos);
        // } else {
        Direction newDir = direction;
        if (direction == Direction.UP || direction == Direction.DOWN) {
            var offsetPos = pos.offset(direction);
            if (!world.getBlockState(offsetPos).isOf(this)) {
                var tempDir = getDirectionFromBlockState(state);
                if (tempDir != null) {
                    var s = (Property<IndicatorLightConnection>) DIRECTION_TO_WIRE_CONNECTION_PROPERTY
                            .get(tempDir);
                    return state.with(
                            s,
                            isFullyConnected(state) || isNotConnected(state) ? IndicatorLightConnection.SIDE : IndicatorLightConnection.NONE);
                } else
                    return state;
            }
            newDir = getDirectionFromBlockState(world.getBlockState(pos.offset(direction)));
            if (newDir != null && world.getBlockState(pos.down()).isSolidBlock(world, pos.down())) {
                return state.with(
                        (Property<IndicatorLightConnection>) DIRECTION_TO_WIRE_CONNECTION_PROPERTY
                                .get(newDir),
                        IndicatorLightConnection.UP);
            }
            boolean allAir = true;
            for (Direction dir : Type.HORIZONTAL) {
                if (!world.isAir(pos.offset(dir))) {
                    allAir = false;
                    newDir = dir;
                    break;
                }
            }
            if (allAir) {
                return Blocks.AIR.getDefaultState();
            }
        }
        IndicatorLightConnection conn = this.getRenderConnectionType(world, pos, neighborPos,
                newDir);
        return conn
                .isConnected() == ((IndicatorLightConnection) state
                        .get((Property<IndicatorLightConnection>) DIRECTION_TO_WIRE_CONNECTION_PROPERTY
                                .get(newDir)))
                        .isConnected()
                && !isFullyConnected(state)
                        ? (BlockState) state.with(
                                (Property<IndicatorLightConnection>) DIRECTION_TO_WIRE_CONNECTION_PROPERTY
                                        .get(newDir),
                                conn)
                        : this.getPlacementState(world, (BlockState) ((BlockState) this.dotState
                                .with(POWERED, (Boolean) state.get(POWERED))).with(
                                        (Property<IndicatorLightConnection>) DIRECTION_TO_WIRE_CONNECTION_PROPERTY
                                                .get(newDir),
                                        conn),
                                pos, null);
        // }
    }

    private static boolean isFullyConnected(BlockState state) {
        return ((IndicatorLightConnection) state.get(WIRE_CONNECTION_NORTH)).isConnected()
                && ((IndicatorLightConnection) state.get(WIRE_CONNECTION_SOUTH)).isConnected()
                && ((IndicatorLightConnection) state.get(WIRE_CONNECTION_EAST)).isConnected()
                && ((IndicatorLightConnection) state.get(WIRE_CONNECTION_WEST)).isConnected();
    }

    private static boolean isNotConnected(BlockState state) {
        return !((IndicatorLightConnection) state.get(WIRE_CONNECTION_NORTH)).isConnected()
                && !((IndicatorLightConnection) state.get(WIRE_CONNECTION_SOUTH)).isConnected()
                && !((IndicatorLightConnection) state.get(WIRE_CONNECTION_EAST)).isConnected()
                && !((IndicatorLightConnection) state.get(WIRE_CONNECTION_WEST)).isConnected();
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

    private IndicatorLightConnection getRenderConnectionType(BlockView world, BlockPos pos, BlockPos neighborPos,
            Direction direction) {
        return this.getRenderConnectionType(world, pos, neighborPos, direction,
                !world.getBlockState(pos.up()).isSolidBlock(world, pos));
    }

    private IndicatorLightConnection getRenderConnectionType(BlockView world, BlockPos pos, BlockPos neighborPos,
            Direction direction, boolean bl) {
        BlockPos blockPos = pos.offset(direction);
        BlockState blockState = world.getBlockState(blockPos);
        if (bl) {
            boolean bl2 = blockState.getBlock() instanceof TrapdoorBlock
                    || this.canRunOnTop(world, blockPos, blockState);
            if (bl2 && connectsTo(world.getBlockState(blockPos.up()))) {
                if (blockState.isSideSolidFullSquare(world, blockPos, direction.getOpposite())) {
                    if (!world.getBlockState(pos.down()).isSolidBlock(world, blockPos.down())) {
                        return IndicatorLightConnection.SIDE_VERTICAL;
                    }
                    return IndicatorLightConnection.UP;
                }

                return IndicatorLightConnection.SIDE;
            }
        }

        return !connectsTo(blockState, direction) && (blockState.isSolidBlock(world, blockPos)
                || !connectsTo(world.getBlockState(blockPos.down()))) ? IndicatorLightConnection.NONE
                        : IndicatorLightConnection.SIDE;
    }

    // public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
    // BlockPos blockPos = pos.down();
    // BlockState blockState = world.getBlockState(blockPos);
    // return this.canRunOnTop(world, blockPos, blockState);
    // }

    private boolean canRunOnTop(BlockView world, BlockPos pos, BlockState floor) {
        return true;
        // return floor.isSideSolidFullSquare(world, pos, Direction.UP) ||
        // floor.isOf(Blocks.HOPPER);
    }

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
            Iterator<Direction> var5 = Type.HORIZONTAL.iterator();

            while (true) {
                while (var5.hasNext()) {
                    Direction direction = (Direction) var5.next();
                    BlockPos blockPos = pos.offset(direction);
                    BlockState blockState = world.getBlockState(blockPos);
                    j = Math.max(j, this.increasePower(world, blockPos));
                    BlockPos blockPos2 = pos.up();
                    if (blockState.isSolidBlock(world, blockPos)
                            && !world.getBlockState(blockPos2).isSolidBlock(world, blockPos2)) {
                        j = Math.max(j, this.increasePower(world, blockPos.up()));
                    } else if (!blockState.isSolidBlock(world, blockPos)) {
                        j = Math.max(j, this.increasePower(world, blockPos.down()));
                    }
                }

                return Math.max(i, j - 1);
            }
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

    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos,
            Direction direction) {
        return !this.wiresGivePower ? 0 : state.getWeakRedstonePower(world, pos, direction);
    }

    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos,
            Direction direction) {
        if (this.wiresGivePower && direction != Direction.DOWN) {
            // int i = (Integer) state.get(POWER);
            IndicatorLightBlockEntity blockEntity = (IndicatorLightBlockEntity) world.getBlockEntity(pos);
            int i = blockEntity != null ? blockEntity.getPower() : 0;
            if (i == 0) {
                return 0;
            } else {
                return direction != Direction.UP
                        && !((IndicatorLightConnection) this.getPlacementState(world, state, pos, null).get(
                                (Property<IndicatorLightConnection>) DIRECTION_TO_WIRE_CONNECTION_PROPERTY
                                        .get(direction.getOpposite())))
                                .isConnected() ? 0 : i;
            }
        } else {
            return 0;
        }
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

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[] { WIRE_CONNECTION_NORTH, WIRE_CONNECTION_EAST,
                WIRE_CONNECTION_SOUTH, WIRE_CONNECTION_WEST, POWERED /* , POWER */ });
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player,
            Hand hand, BlockHitResult hit) {
        if (!player.getAbilities().allowModifyWorld) {
            return ActionResult.PASS;
        } else {
            if (isFullyConnected(state) || isNotConnected(state)) {
                BlockState blockState = isFullyConnected(state) ? this.getDefaultState() : this.dotState;
                // blockState = (BlockState) blockState.with(POWER, (Integer) state.get(POWER));
                blockState = (BlockState) blockState.with(POWERED, (Boolean) state.get(POWERED));
                blockState = this.getPlacementState(world, blockState, pos, null);
                if (blockState != state) {
                    world.setBlockState(pos, blockState, 3);
                    this.updateForNewState(world, pos, state, blockState);
                    return ActionResult.SUCCESS;
                }
            }

            return ActionResult.PASS;
        }
    }

    private void updateForNewState(World world, BlockPos pos, BlockState oldState,
            BlockState newState) {
        Iterator<Direction> var5 = Type.HORIZONTAL.iterator();

        while (var5.hasNext()) {
            Direction direction = (Direction) var5.next();
            BlockPos blockPos = pos.offset(direction);
            if (((IndicatorLightConnection) oldState
                    .get((Property<IndicatorLightConnection>) DIRECTION_TO_WIRE_CONNECTION_PROPERTY
                            .get(direction)))
                    .isConnected() != ((IndicatorLightConnection) newState.get(
                            (Property<IndicatorLightConnection>) DIRECTION_TO_WIRE_CONNECTION_PROPERTY
                                    .get(direction)))
                            .isConnected()
                    && world.getBlockState(blockPos).isSolidBlock(world, blockPos)) {
                world.updateNeighborsExcept(blockPos, newState.getBlock(), direction.getOpposite());
            }
        }

    }

    static {
        WIRE_CONNECTION_NORTH = ApertureProperties.NORTH_WIRE_CONNECTION;
        WIRE_CONNECTION_EAST = ApertureProperties.EAST_WIRE_CONNECTION;
        WIRE_CONNECTION_SOUTH = ApertureProperties.SOUTH_WIRE_CONNECTION;
        WIRE_CONNECTION_WEST = ApertureProperties.WEST_WIRE_CONNECTION;
        // POWER = Properties.POWER;
        POWERED = Properties.POWERED;
        DIRECTION_TO_WIRE_CONNECTION_PROPERTY = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH,
                WIRE_CONNECTION_NORTH, Direction.EAST, WIRE_CONNECTION_EAST, Direction.SOUTH,
                WIRE_CONNECTION_SOUTH, Direction.WEST, WIRE_CONNECTION_WEST));
        DOT_SHAPE = Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 1.0, 13.0);
        DIRECTION_TO_SIDE_SHAPE = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH,
                Block.createCuboidShape(3.0, 0.0, 0.0, 13.0, 1.0, 13.0), Direction.SOUTH,
                Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 1.0, 16.0), Direction.EAST,
                Block.createCuboidShape(3.0, 0.0, 3.0, 16.0, 1.0, 13.0), Direction.WEST,
                Block.createCuboidShape(0.0, 0.0, 3.0, 13.0, 1.0, 13.0)));
        DIRECTION_TO_VERTICAL_SHAPE = Maps.newEnumMap(ImmutableMap.of(
                Direction.NORTH, Block.createCuboidShape(3.0, 0.0, 0.0, 13.0, 16.0, 1.0),
                Direction.SOUTH, Block.createCuboidShape(3.0, 0.0, 15.0, 13.0, 16.0, 16.0),
                Direction.EAST, Block.createCuboidShape(15.0, 0.0, 3.0, 16.0, 16.0, 13.0),
                Direction.WEST, Block.createCuboidShape(0.0, 0.0, 3.0, 1.0, 16.0, 13.0)));
        DIRECTION_TO_UP_SHAPE = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH,
                VoxelShapes.union((VoxelShape) DIRECTION_TO_SIDE_SHAPE.get(Direction.NORTH),
                        (VoxelShape) DIRECTION_TO_VERTICAL_SHAPE.get(Direction.NORTH)),
                Direction.SOUTH,
                VoxelShapes.union((VoxelShape) DIRECTION_TO_SIDE_SHAPE.get(Direction.SOUTH),
                        (VoxelShape) DIRECTION_TO_VERTICAL_SHAPE.get(Direction.SOUTH)),
                Direction.EAST,
                VoxelShapes.union((VoxelShape) DIRECTION_TO_SIDE_SHAPE.get(Direction.EAST),
                        (VoxelShape) DIRECTION_TO_VERTICAL_SHAPE.get(Direction.EAST)),
                Direction.WEST,
                VoxelShapes.union((VoxelShape) DIRECTION_TO_SIDE_SHAPE.get(Direction.WEST),
                        (VoxelShape) DIRECTION_TO_VERTICAL_SHAPE.get(Direction.WEST))));
        SHAPES = Maps.newHashMap();
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
}
