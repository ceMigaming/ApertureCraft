package com.cemi.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class IndicatorBlock extends ApertureBlock {

    public static final DirectionProperty FACING = Properties.FACING;
    public static final BooleanProperty POWERED = Properties.POWERED;

    public IndicatorBlock(Settings settings) {
        super("indicator", settings);
        setDefaultState(getDefaultState().with(FACING, Direction.NORTH).with(POWERED, false));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos,
            ShapeContext context) {
        Direction dir = state.get(FACING);
        switch (dir) {
            case NORTH:
                return VoxelShapes.cuboid(0.25f, 0.25f, 0.96875f, 0.75f, 0.75f, 1.0f);
            case SOUTH:
                return VoxelShapes.cuboid(0.25f, 0.25f, 0.0f, 0.75f, 0.75f, 0.03125f);
            case EAST:
                return VoxelShapes.cuboid(0.0f, 0.25f, 0.25f, 0.03125f, 0.75f, 0.75f);
            case WEST:
                return VoxelShapes.cuboid(0.96875f, 0.25f, 0.25f, 1.0f, 0.75f, 0.75f);
            case DOWN:
                return VoxelShapes.cuboid(0.25f, 0.96875f, 0.25f, 0.75f, 1.f, 0.75f);
            case UP:
                return VoxelShapes.cuboid(0.25f, 0.f, 0.25f, 0.75f, 0.03125f, 0.75f);
            default:
                return VoxelShapes.fullCube();
        }
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock,
            BlockPos sourcePos, boolean notify) {
        boolean powered =
                world.isReceivingRedstonePower(pos) || world.isReceivingRedstonePower(pos.down());
        if (powered != state.get(POWERED)) {
            world.setBlockState(pos, state.with(POWERED, powered), 3);
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite());
    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED);
    }
}
