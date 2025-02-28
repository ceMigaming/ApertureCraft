package com.cemi.block;

import org.jetbrains.annotations.Nullable;

import com.cemi.block.entity.ApertureFloorButtonBlockEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class FloorButtonBlock extends ApertureBlock implements BlockEntityProvider {

    public static final int MAX_POWER = 127;
    public static final BooleanProperty POWERED = Properties.POWERED;
    protected static final Box COLLIDER = new Box(-0.3125D, 0.0D, -0.3125D, 1.3125D, 0.375D, 1.3125D);
    protected static final VoxelShape PRESSED_SHAPE = Block.createCuboidShape(-5.0D, 0.0D, -5.0D, 21.0D, 4.0D, 21.0D);
    protected static final VoxelShape DEFAULT_SHAPE = Block.createCuboidShape(-5.0D, 0.0D, -5.0D, 21.0D, 5.0D, 21.0D);

    // standard, cube, sphere, old buttons
    // public static final IntProperty BUTTON_TYPE = IntProperty.of("button_type",
    // 0, 3);

    public FloorButtonBlock(String name, Settings settings) {
        super(name, settings, true);
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos,
            ShapeContext context) {
        return this.getRedstoneOutput(state) > 0 ? PRESSED_SHAPE : DEFAULT_SHAPE;
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ApertureFloorButtonBlockEntity(pos, state);
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos,
            Direction direction) {
        return this.getRedstoneOutput(state);
    }

    @Override
    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos,
            Direction direction) {
        return direction == Direction.UP ? this.getRedstoneOutput(state) : 0;
    }

    protected int getRedstoneOutput(BlockState state) {
        return (Boolean) state.get(POWERED) ? MAX_POWER : 0;
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!world.isClient) {
            int i = this.getRedstoneOutput(state);
            if (i == 0) {
                this.updatePlateState(entity, world, pos, state, i);
            }
        }
    }

    protected int getRedstoneOutput(World world, BlockPos pos) {
        // Will be added in future update
        // Class<?> detectedEntityClass;
        // switch() {
        // case 1:
        // var10000 = Entity.class;
        // break;
        // case 2:
        // var10000 = LivingEntity.class;
        // break;
        // default:
        // throw new IncompatibleClassChangeError();
        // }

        // Class<? extends Entity> class_ = var10000;
        return getEntityCount(world, COLLIDER.offset(pos), Entity.class/* class_ */) > 0 ? MAX_POWER
                : 0;
    }

    protected static int getEntityCount(World world, Box box, Class<? extends Entity> entityClass) {
        return world.getEntitiesByClass(entityClass, box,
                EntityPredicates.EXCEPT_SPECTATOR.and((entity) -> {
                    return !entity.canAvoidTraps();
                })).size();
    }

    protected BlockState setRedstoneOutput(BlockState state, int rsOut) {
        return (BlockState) state.with(POWERED, rsOut > 0);
    }

    protected void updateNeighbors(World world, BlockPos pos) {
        world.updateNeighborsAlways(pos, this);
        world.updateNeighborsAlways(pos.down(), this);
    }

    private void updatePlateState(@Nullable Entity entity, World world, BlockPos pos,
            BlockState state, int output) {
        int redstoneOutput = this.getRedstoneOutput(world, pos);
        boolean hasPositiveOutput = output > 0;
        boolean isPowered = redstoneOutput > 0;
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ApertureFloorButtonBlockEntity) {
            ((ApertureFloorButtonBlockEntity) blockEntity).triggerAnim("controller",
                    isPowered ? "press" : "release");
        }
        if (output != redstoneOutput) {
            BlockState blockState = this.setRedstoneOutput(state, redstoneOutput);
            world.setBlockState(pos, blockState, 2);
            this.updateNeighbors(world, pos);
            world.scheduleBlockRerenderIfNeeded(pos, state, blockState);
        }

        if (!isPowered && hasPositiveOutput) {
            // world.playSound((PlayerEntity) null, pos,
            // this.blockSetType.pressurePlateClickOff(),
            // SoundCategory.BLOCKS);
            world.emitGameEvent(entity, GameEvent.BLOCK_DEACTIVATE, pos);
        } else if (isPowered && !hasPositiveOutput) {
            // world.playSound((PlayerEntity) null, pos,
            // this.blockSetType.pressurePlateClickOn(),
            // SoundCategory.BLOCKS);
            world.emitGameEvent(entity, GameEvent.BLOCK_ACTIVATE, pos);
        }

        if (isPowered) {
            world.scheduleBlockTick(new BlockPos(pos), this, 0);
        }

    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        int i = this.getRedstoneOutput(state);
        if (i > 0) {
            this.updatePlateState((Entity) null, world, pos, state, i);
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState) this.getDefaultState().with(POWERED,
                this.getRedstoneOutput(ctx.getWorld(), ctx.getBlockPos()) > 0);
    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> builder) {
        builder.add(new Property[] { POWERED });
    }
}
