package com.cemi.block;

import com.cemi.block.entity.AperturePanelRailSupportBlockEntity;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class AperturePanelRailSupportBlock extends ApertureBlock implements BlockEntityProvider {

    public AperturePanelRailSupportBlock(Settings settings) {
        super("panel_rail_support", settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new AperturePanelRailSupportBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

}
