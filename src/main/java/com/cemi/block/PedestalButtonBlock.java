package com.cemi.block;

import org.jetbrains.annotations.Nullable;

import com.cemi.block.entity.PedestalButtonBlockEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;

public class PedestalButtonBlock extends ApertureBlock implements BlockEntityProvider {
    public static final int MAX_POWER = 127;
    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    protected static final VoxelShape PRESSED_SHAPE = Block.createCuboidShape(5.0D, 0.0D, 5.0D, 11.0D, 22.0D, 11.0D);
    protected static final VoxelShape DEFAULT_SHAPE = Block.createCuboidShape(5.0D, 0.0D, 5.0D, 11.0D, 22.0D, 11.0D);

    public PedestalButtonBlock(String name, Settings settings) {
        super(name, settings);
        this.setDefaultState((BlockState) ((BlockState) ((BlockState) ((BlockState) this.stateManager.getDefaultState())
                .with(FACING, Direction.NORTH)).with(POWERED, false)));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return state.get(POWERED) ? PRESSED_SHAPE : DEFAULT_SHAPE;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PedestalButtonBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return (Boolean) state.get(POWERED) ? MAX_POWER : 0;
    }

    @Override
    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return (Boolean) state.get(POWERED) ? MAX_POWER : 0;
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    public void powerOn(BlockState state, World world, BlockPos pos) {
        world.setBlockState(pos, (BlockState) state.with(POWERED, true), 3);
        this.updateNeighbors(state, world, pos);
        world.scheduleBlockTick(pos, this, 20);
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof PedestalButtonBlockEntity pbbe) {
            pbbe.triggerAnim("controller", "press");
        }
    }

    private void updateNeighbors(BlockState state, World world, BlockPos pos) {
        world.updateNeighborsAlways(pos, this);
        world.updateNeighborsAlways(pos.down(), this);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
            BlockHitResult hit) {
        if ((Boolean) state.get(POWERED)) {
            return ActionResult.CONSUME;
        } else {
            this.powerOn(state, world, pos);
            this.playClickSound(player, world, pos, true);
            world.emitGameEvent(player, GameEvent.BLOCK_ACTIVATE, pos);
            return ActionResult.success(world.isClient);
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState) this.getDefaultState()
                .with(FACING, ctx.getPlayerLookDirection());
    }

    protected void playClickSound(@Nullable PlayerEntity player, WorldAccess world, BlockPos pos, boolean powered) {
        world.playSound(powered ? player : null, pos, this.getClickSound(powered), SoundCategory.BLOCKS);
    }

    protected SoundEvent getClickSound(boolean powered) {
        return powered ? BlockSetType.IRON.buttonClickOn() : BlockSetType.IRON.buttonClickOff();
    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> builder) {
        builder.add(new Property[] { POWERED, FACING });
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if ((Boolean) state.get(POWERED)) {
            this.powerOff(state, world, pos);
        }
    }

    protected void powerOff(BlockState state, World world, BlockPos pos) {
        world.setBlockState(pos, (BlockState) state.with(POWERED, false), 3);
        this.updateNeighbors(state, world, pos);
        this.playClickSound((PlayerEntity) null, world, pos, false);
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof PedestalButtonBlockEntity pbbe) {
            pbbe.triggerAnim("controller", "release");
        }
    }
}
