package com.cemi.block;

import com.cemi.block.entity.ApertureDoorBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class ApertureDoorBlock extends ApertureBlock implements BlockEntityProvider {

    public static final DirectionProperty FACING = Properties.FACING;
    public static final BooleanProperty TRIGGERED = Properties.TRIGGERED;
    public static final IntProperty SIDE = IntProperty.of("side", 0, 3);

    public ApertureDoorBlock(Settings settings) {
        super("door", settings);
        setDefaultState(getDefaultState().with(FACING, Direction.NORTH).with(TRIGGERED, false)
                .with(SIDE, 0));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState) this.getDefaultState()
                .with(FACING, ctx.getPlayerLookDirection().getOpposite()).with(TRIGGERED, false);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, TRIGGERED);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ApertureDoorBlockEntity(pos, state);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos,
            ShapeContext context) {
        if (state.get(TRIGGERED) == false)
            return switch ((Direction) state.get(FACING)) {
                case EAST -> createCuboidShape(4, 0, 0, 12, 16, 16);
                case WEST -> createCuboidShape(4, 0, 0, 12, 16, 16);
                case SOUTH -> createCuboidShape(0, 0, 4, 16, 16, 12);
                case NORTH -> createCuboidShape(0, 0, 4, 16, 16, 12);
                case DOWN -> createCuboidShape(0, 11, 0, 16, 16, 16);
                case UP -> createCuboidShape(0, 0, 0, 16, 5, 16);
                default -> createCuboidShape(0, 0, 0, 16, 16, 16);
            };
        return switch ((Direction) state.get(FACING)) {
            case EAST -> createCuboidShape(4, 0, 11, 12, 16, 16);
            case WEST -> createCuboidShape(4, 0, 0, 12, 16, 5);
            case SOUTH -> createCuboidShape(0, 0, 4, 5, 16, 12);
            case NORTH -> createCuboidShape(11, 0, 4, 16, 16, 12);
            case DOWN -> createCuboidShape(0, 11, 0, 16, 16, 16);
            case UP -> createCuboidShape(0, 0, 0, 16, 5, 16);
            default -> createCuboidShape(0, 0, 0, 16, 16, 16);
        };
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock,
            BlockPos sourcePos, boolean notify) {
        ApertureDoorBlockEntity doorEntity = (ApertureDoorBlockEntity) world.getBlockEntity(pos);
        if (doorEntity == null) {
            return;
        }
        boolean isReceivingRedstonePower =
                world.isReceivingRedstonePower(pos) || world.isReceivingRedstonePower(pos.up());
        boolean isTriggered = (Boolean) state.get(TRIGGERED);
        if (isReceivingRedstonePower && !isTriggered) {
            world.setBlockState(pos, (BlockState) state.with(TRIGGERED, true), 2);
            doorEntity.triggerAnim("controller", "open");
        } else if (!isReceivingRedstonePower && isTriggered) {
            world.setBlockState(pos, (BlockState) state.with(TRIGGERED, false), 2);
            doorEntity.triggerAnim("controller", "close");
        }
    }

}
