package com.cemi.block;

import org.jetbrains.annotations.Nullable;

import com.cemi.block.entity.FizzlerBlockEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class FizzlerBlock extends ApertureBlock implements BlockEntityProvider {

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty CONNECTED = BooleanProperty.of("connected");

    private static final int MAX_SEARCH_DISTANCE = 16;

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
    private static BlockPos findPartner(World world, BlockPos pos, Direction facing) {
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

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, CONNECTED);
    }
}
