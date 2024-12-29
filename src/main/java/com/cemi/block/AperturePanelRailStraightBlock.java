package com.cemi.block;

import com.cemi.block.entity.AperturePanelRailStraightBlockEntity;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class AperturePanelRailStraightBlock extends ApertureBlock implements BlockEntityProvider {

    public AperturePanelRailStraightBlock(Settings settings) {
        super("panel_rail_straight", settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new AperturePanelRailStraightBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

}
