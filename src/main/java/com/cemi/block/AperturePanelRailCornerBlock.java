package com.cemi.block;

import com.cemi.block.entity.AperturePanelRailCornerBlockEntity;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class AperturePanelRailCornerBlock extends ApertureBlock implements BlockEntityProvider {

    public AperturePanelRailCornerBlock(Settings settings) {
        super("panel_rail_corner", settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new AperturePanelRailCornerBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

}
