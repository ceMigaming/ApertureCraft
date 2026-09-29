package com.cemi.block;

import org.jetbrains.annotations.Nullable;

import com.cemi.block.entity.ApertureBlockEntities;
import com.cemi.block.entity.FizzlerBlockEntity;
import com.cemi.sound.ApertureSoundEvent;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class FizzlerBlock extends ApertureBlock implements BlockEntityProvider {

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty CONNECTED = BooleanProperty.of("connected");

    public static final int MAX_SEARCH_DISTANCE = 16;

    /** Height of the field plane drawn between two connected emitters. */
    public static final double FIELD_HEIGHT = 2.0;

    private static final double EDGE_EPSILON = 1.0E-4;

    public FizzlerBlock(String name, Settings settings) {
        super(name, settings, true);
        setDefaultState(getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(CONNECTED, false));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new FizzlerBlockEntity(pos, state);
    }

    /**
     * {@code validateTicker} lives on BlockWithEntity, which ApertureBlock does not
     * extend, so the type check is done inline. Connection state is deliberately not
     * tested here - tickers are only re-resolved when the block entity is added, not
     * on every state change, so FizzlerBlockEntity#tick gates on it instead.
     *
     * Both halves get a ticker: the server one runs the field, the client one runs
     * the field hum, which has no business being tied to rendering.
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state,
            BlockEntityType<T> type) {
        if (type != ApertureBlockEntities.FIZZLER) {
            return null;
        }
        if (world.isClient()) {
            return (BlockEntityTicker<T>) (BlockEntityTicker<FizzlerBlockEntity>) FizzlerBlockEntity
                    ::clientTick;
        }
        return (BlockEntityTicker<T>) (BlockEntityTicker<FizzlerBlockEntity>) FizzlerBlockEntity::tick;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState()
                .with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state,
            @Nullable LivingEntity placer, ItemStack itemStack) {
        if (world.isClient)
            return;
        updateConnection(world, pos, state);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction,
            BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (world.isClient())
            return state;
        if (direction.getAxis().isHorizontal()) {
            Direction facing = state.get(FACING);
            if (direction == facing || direction == facing.getOpposite()) {
                scheduleConnectionUpdate(world, pos);
            }
        }
        return state;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos,
            Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world.isClient)
            return;
        updateConnection(world, pos, state);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos,
            BlockState newState, boolean moved) {
        if (!world.isClient && !state.isOf(newState.getBlock())) {
            // Only a field that was actually live has a hum to cut short, so a lone
            // emitter being broken stays silent.
            if (state.get(CONNECTED)) {
                world.playSound(null, pos, ApertureSoundEvent.FIZZLER_SHUTDOWN_EVENT,
                        SoundCategory.BLOCKS, ApertureSoundEvent.FIZZLER_VOLUME, 1.0F);
            }
            notifyPartner(world, pos, state);
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    private void scheduleConnectionUpdate(WorldAccess world, BlockPos pos) {
        if (!world.isClient() && world instanceof World w) {
            w.scheduleBlockTick(pos, this, 1);
        }
    }

    @Override
    public void scheduledTick(BlockState state, net.minecraft.server.world.ServerWorld world,
            BlockPos pos, net.minecraft.util.math.random.Random random) {
        updateConnection(world, pos, state);
    }

    public static void updateConnection(World world, BlockPos pos, BlockState state) {
        if (world.isClient)
            return;

        Direction facing = state.get(FACING);
        BlockPos partnerPos = findPartner(world, pos, facing);

        boolean wasConnected = state.get(CONNECTED);
        boolean isConnected = partnerPos != null;

        if (wasConnected != isConnected) {
            world.setBlockState(pos, state.with(CONNECTED, isConnected), 3);
            if (isConnected) {
                // Keyed to the field coming up rather than to the block arriving, so
                // completing a pair announces itself but half a field does not.
                world.playSound(null, pos, ApertureSoundEvent.FIZZLER_START_EVENT,
                        SoundCategory.BLOCKS, ApertureSoundEvent.FIZZLER_VOLUME, 1.0F);
            }
        }

        FizzlerBlockEntity be = (FizzlerBlockEntity) world.getBlockEntity(pos);
        if (be != null) {
            be.setConnectedPos(partnerPos);
        }

        if (partnerPos != null) {
            BlockState partnerState = world.getBlockState(partnerPos);
            if (partnerState.isOf(state.getBlock())) {
                boolean partnerConnected = partnerState.get(CONNECTED);
                if (!partnerConnected) {
                    world.setBlockState(partnerPos,
                            partnerState.with(CONNECTED, true), 3);
                }
                FizzlerBlockEntity partnerBe = (FizzlerBlockEntity) world
                        .getBlockEntity(partnerPos);
                if (partnerBe != null) {
                    partnerBe.setConnectedPos(pos);
                }
            }
        }
    }

    @Nullable
    public static BlockPos findPartner(World world, BlockPos pos, Direction facing) {
        Direction opposite = facing.getOpposite();

        for (int i = 1; i <= MAX_SEARCH_DISTANCE; i++) {
            BlockPos checkPos = pos.offset(facing, i);
            BlockState checkState = world.getBlockState(checkPos);

            if (checkState.getBlock() instanceof FizzlerBlock) {
                if (checkState.get(FACING) == opposite) {
                    if (isPathClear(world, pos, checkPos, facing)) {
                        return checkPos;
                    }
                }
                return null;
            }

            if (!checkState.isAir() && !checkState.isReplaceable()) {
                return null;
            }
        }
        return null;
    }

    private static boolean isPathClear(World world, BlockPos from, BlockPos to,
            Direction facing) {
        int distance = facing.getAxis() == Direction.Axis.Z
                ? Math.abs(from.getZ() - to.getZ())
                : Math.abs(from.getX() - to.getX());

        for (int i = 1; i < distance; i++) {
            BlockPos betweenPos = from.offset(facing, i);
            BlockState betweenState = world.getBlockState(betweenPos);
            if (!betweenState.isAir() && !betweenState.isReplaceable()) {
                return false;
            }
        }
        return true;
    }

    private void notifyPartner(World world, BlockPos pos, BlockState oldState) {
        FizzlerBlockEntity be = (FizzlerBlockEntity) world.getBlockEntity(pos);
        if (be == null)
            return;

        BlockPos partnerPos = be.getConnectedPos();
        if (partnerPos == null)
            return;

        BlockState partnerState = world.getBlockState(partnerPos);
        if (partnerState.getBlock() instanceof FizzlerBlock) {
            world.setBlockState(partnerPos,
                    partnerState.with(CONNECTED, false), 3);
            FizzlerBlockEntity partnerBe = (FizzlerBlockEntity) world
                    .getBlockEntity(partnerPos);
            if (partnerBe != null) {
                partnerBe.setConnectedPos(null);
            }
        }
    }

    /**
     * Resolves the emitter this one is paired with, or {@code null} when no field
     * exists. Falls back to a fresh scan when the block entity has no cached
     * partner, which happens right after a chunk is loaded.
     */
    @Nullable
    public static BlockPos getConnectedPartner(World world, BlockPos pos, BlockState state) {
        if (!(state.getBlock() instanceof FizzlerBlock) || !state.get(CONNECTED))
            return null;

        if (world.getBlockEntity(pos) instanceof FizzlerBlockEntity be
                && be.getConnectedPos() != null) {
            return be.getConnectedPos();
        }
        return findPartner(world, pos, state.get(FACING));
    }

    /**
     * Whether a live field is anchored at this position. Deliberately reads the
     * synced CONNECTED state rather than the block entity, so it also answers
     * correctly on the client, where the cached partner may never have arrived.
     */
    public static boolean isConnectedAt(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.getBlock() instanceof FizzlerBlock && state.get(CONNECTED);
    }

    /**
     * Volume spanned by the field plane between an emitter and its partner. Both
     * emitters share a row along the connection axis, so only that axis varies.
     */
    public static Box getFieldBounds(BlockPos pos, BlockPos partnerPos) {
        return new Box(
                Math.min(pos.getX(), partnerPos.getX()),
                pos.getY(),
                Math.min(pos.getZ(), partnerPos.getZ()),
                Math.max(pos.getX(), partnerPos.getX()) + 1.0,
                pos.getY() + FIELD_HEIGHT,
                Math.max(pos.getZ(), partnerPos.getZ()) + 1.0);
    }

    /**
     * Whether the given volume overlaps the field of any connected emitter pair.
     *
     * A field is a one-block-wide slab that shares its block row with both of its
     * emitters, so an emitter capable of covering a box must lie on a line running
     * along that box's footprint - never in a solid region around it. Marching
     * those lines keeps this to a few hundred block lookups instead of a
     * MAX_SEARCH_DISTANCE cube. Only ever called on demand, e.g. when the portal
     * gun is fired, so the scan cost is paid at most a few times a second.
     */
    public static boolean isInsideField(World world, Box box) {
        // The max corner is exclusive, so nudge it inwards to avoid claiming the
        // block the box only touches.
        BlockPos min = BlockPos.ofFloored(box.minX, box.minY, box.minZ);
        BlockPos max = BlockPos.ofFloored(box.maxX - EDGE_EPSILON, box.maxY - EDGE_EPSILON,
                box.maxZ - EDGE_EPSILON);

        // Emitters only ever sit on the field's base row, while the field itself
        // spans FIELD_HEIGHT. A box floating in the rows above one would therefore
        // never meet an emitter by marching at its own height, which is what let
        // angled shots cross the top half of a field untouched. March every row an
        // emitter could occupy, which for a field of this height is the box's own
        // row and the rows beneath it.
        int rows = (int) Math.ceil(FIELD_HEIGHT);

        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int y = min.getY(); y <= max.getY(); y++) {
                for (int z = min.getZ(); z <= max.getZ(); z++) {
                    for (int row = 0; row < rows; row++) {
                        BlockPos start = new BlockPos(x, y - row, z);
                        if (coversFieldAlong(world, start, Direction.SOUTH, box)
                                || coversFieldAlong(world, start, Direction.NORTH, box)
                                || coversFieldAlong(world, start, Direction.EAST, box)
                                || coversFieldAlong(world, start, Direction.WEST, box)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Walks one line out of {@code start} looking for the single emitter a field
     * could be anchored to, then reports whether that field covers {@code box}.
     * Stops at the first solid block, mirroring the pairing scan in findPartner.
     */
    private static boolean coversFieldAlong(World world, BlockPos start, Direction direction, Box box) {
        for (int i = 1; i <= MAX_SEARCH_DISTANCE; i++) {
            BlockPos pos = start.offset(direction, i);
            BlockState state = world.getBlockState(pos);

            if (!(state.getBlock() instanceof FizzlerBlock)) {
                if (!state.isAir() && !state.isReplaceable()) {
                    return false;
                }
                continue;
            }

            BlockPos partner = getConnectedPartner(world, pos, state);
            return partner != null && getFieldBounds(pos, partner).intersects(box);
        }
        return false;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, CONNECTED);
    }
}
