package com.cemi.block;

import com.cemi.ApertureCraft;
import com.cemi.block.entity.LargeTileEntity;
import com.cemi.entity.ApertureEntities;
import com.cemi.entity.GhostBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.IntProperty;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class LargeTile extends ApertureBlock implements BlockEntityProvider {

    public static final IntProperty SIDE = IntProperty.of("side", 0, 3);

    public LargeTile(String name, Settings settings) {
        super(name, settings);
        setDefaultState(getDefaultState().with(SIDE, 0));
    }


    // TODO move most of it to onBlockAdded
    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer,
            ItemStack itemStack) {
        if (!world.isClient()) {
            Direction facing = Direction.getEntityFacingOrder(placer)[0];
            int dx = 0;
            int dy = 0;
            int dz = 0;
            int dw = 0;
            switch (facing) {
                case NORTH:
                    dx++;
                    dy++;
                    break;
                case SOUTH:
                    dx--;
                    dy++;
                    break;
                case EAST:
                    dy++;
                    dz++;
                    break;
                case WEST:
                    dy++;
                    dz--;
                    break;
                case UP:
                    dx++;
                    dw++;
                    break;
                default:
                case DOWN:
                    dx++;
                    dw--;
                    break;
            }
            BlockPos upperRightBlockPos =
                    new BlockPos(pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz + dw);
            BlockPos lowerLeftBlockPos = new BlockPos(pos.getX(), pos.getY() + dy, pos.getZ() + dw);
            BlockPos lowerRightBlockPos =
                    new BlockPos(pos.getX() + dx, pos.getY(), pos.getZ() + dz);

            if (!world.isAir(upperRightBlockPos) || !world.isAir(lowerLeftBlockPos)
                    || !world.isAir(lowerRightBlockPos)) {
                placer.sendMessage(Text.translatable(ApertureCraft.MOD_ID + ".large_tile.no_space")
                        .setStyle(Style.EMPTY.withColor(Formatting.RED)));
                world.setBlockState(pos, Blocks.AIR.getDefaultState());
                if (!world.isAir(upperRightBlockPos)) {
                    GhostBlockEntity upperRightGhostBlockEntity =
                            ApertureEntities.GHOSTBLOCK.create(world);
                    upperRightGhostBlockEntity.setPos(upperRightBlockPos.toCenterPos().getX(),
                            upperRightBlockPos.toCenterPos().getY(),
                            upperRightBlockPos.toCenterPos().getZ());
                    world.spawnEntity(upperRightGhostBlockEntity);
                }
                if (!world.isAir(lowerLeftBlockPos)) {
                    GhostBlockEntity lowerLeftGhostBlockEntity =
                            ApertureEntities.GHOSTBLOCK.create(world);
                    lowerLeftGhostBlockEntity.setPos(lowerLeftBlockPos.toCenterPos().getX(),
                            lowerLeftBlockPos.toCenterPos().getY(),
                            lowerLeftBlockPos.toCenterPos().getZ());
                    world.spawnEntity(lowerLeftGhostBlockEntity);
                }
                if (!world.isAir(lowerRightBlockPos)) {

                    GhostBlockEntity lowerRightGhostBlockEntity =
                            ApertureEntities.GHOSTBLOCK.create(world);
                    lowerRightGhostBlockEntity.setPos(lowerRightBlockPos.toCenterPos().getX(),
                            lowerRightBlockPos.toCenterPos().getY(),
                            lowerRightBlockPos.toCenterPos().getZ());
                    world.spawnEntity(lowerRightGhostBlockEntity);
                }
                return;
            }
            world.setBlockState(pos, state.with(SIDE, 0));
            world.setBlockState(upperRightBlockPos, state.with(SIDE, 1));
            world.setBlockState(lowerLeftBlockPos, state.with(SIDE, 2));
            world.setBlockState(lowerRightBlockPos, state.with(SIDE, 3));

            BlockEntity upperLeftBlockEntity = world.getBlockEntity(pos);
            BlockEntity upperRightBlockEntity = world.getBlockEntity(upperRightBlockPos);
            BlockEntity lowerLeftBlockEntity = world.getBlockEntity(lowerLeftBlockPos);
            BlockEntity lowerRightBlockEntity = world.getBlockEntity(lowerRightBlockPos);

            if (world.getBlockState(pos).get(SIDE) == 0) {
                if (upperLeftBlockEntity instanceof LargeTileEntity
                        && upperRightBlockEntity instanceof LargeTileEntity
                        && lowerLeftBlockEntity instanceof LargeTileEntity
                        && lowerRightBlockEntity instanceof LargeTileEntity) {
                    ((LargeTileEntity) upperLeftBlockEntity).setBlockPoses(pos, upperRightBlockPos,
                            lowerLeftBlockPos, lowerRightBlockPos);
                    ((LargeTileEntity) upperRightBlockEntity).setBlockPoses(pos, upperRightBlockPos,
                            lowerLeftBlockPos, lowerRightBlockPos);
                    ((LargeTileEntity) lowerLeftBlockEntity).setBlockPoses(pos, upperRightBlockPos,
                            lowerLeftBlockPos, lowerRightBlockPos);
                    ((LargeTileEntity) lowerRightBlockEntity).setBlockPoses(pos, upperRightBlockPos,
                            lowerLeftBlockPos, lowerRightBlockPos);
                }
            }
        }
        super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient()) {
            BlockEntity currentBlockEntity = world.getBlockEntity(pos);
            if (currentBlockEntity instanceof LargeTileEntity) {
                BlockPos upperLeftBlockPos =
                        ((LargeTileEntity) currentBlockEntity).getUpperLeftBlockPos();
                BlockPos upperRightBlockPos =
                        ((LargeTileEntity) currentBlockEntity).getUpperRightBlockPos();
                BlockPos lowerLeftBlockPos =
                        ((LargeTileEntity) currentBlockEntity).getLowerLeftBlockPos();
                BlockPos lowerRightBlockPos =
                        ((LargeTileEntity) currentBlockEntity).getLowerRightBlockPos();

                world.removeBlock(upperLeftBlockPos, false);
                world.removeBlock(upperRightBlockPos, false);
                world.removeBlock(lowerLeftBlockPos, false);
                world.removeBlock(lowerRightBlockPos, false);
            }
        }
        return super.onBreak(world, pos, state, player);
    }

    @Override
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {

        super.onBroken(world, pos, state);
    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> builder) {
        builder.add(SIDE);
        super.appendProperties(builder);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new LargeTileEntity(pos, state);
    }
}
