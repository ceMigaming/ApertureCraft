package com.cemi.block;

import com.cemi.block.entity.ApertureDoorBlockEntity;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class ApertureDoorBlock extends ApertureBlock implements BlockEntityProvider {

    public ApertureDoorBlock(Settings settings) {
        super("door", settings);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ApertureDoorBlockEntity(pos, state);
    }
}
