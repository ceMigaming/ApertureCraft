package com.cemi.block;

import com.cemi.ApertureCraft;
import com.cemi.entity.ApertureEntities;
import com.cemi.entity.GhostBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockCollumn extends ApertureBlock {

    public static final BooleanProperty UPPER = BooleanProperty.of("upper");

    public BlockCollumn(String name, Settings settings) {
        super(name, settings);
        setDefaultState(getDefaultState().with(UPPER, false));
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer,
            ItemStack itemStack) {
        if (state.get(UPPER) == true) {
            return;
        }
        BlockPos upperBlockPos = new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ());
        BlockPos lowerBlockPos = new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ());
        if (world.isAir(upperBlockPos)) {
            world.setBlockState(pos, state.with(BlockCollumn.UPPER, false));
            world.setBlockState(upperBlockPos, state.with(UPPER, true));
        } else if (world.isAir(lowerBlockPos)) {
            world.setBlockState(pos, state.with(BlockCollumn.UPPER, true));
            world.setBlockState(lowerBlockPos, state.with(UPPER, false));
        } else {
            GhostBlockEntity upperGhostBlockEntity = ApertureEntities.GHOSTBLOCK.create(world);
            upperGhostBlockEntity.setPos(upperBlockPos.toCenterPos().getX(),
                    upperBlockPos.toCenterPos().getY(), upperBlockPos.toCenterPos().getZ());
            world.spawnEntity(upperGhostBlockEntity);
            placer.sendMessage(Text.translatable(ApertureCraft.MOD_ID + ".block_collumn.no_space")
                    .setStyle(Style.EMPTY.withColor(Formatting.RED)));
            return;
        }
        super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (state.get(UPPER) == true) {
            BlockPos lowerBlockPos = new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ());
            world.setBlockState(lowerBlockPos, Blocks.AIR.getDefaultState());
        } else {
            BlockPos upperBlockPos = new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ());
            world.setBlockState(upperBlockPos, Blocks.AIR.getDefaultState());
        }
        super.onBreak(world, pos, state, player);
    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> builder) {
        builder.add(UPPER);
    }
}
