package com.cemi.block;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;

public class HEPLauncherBlock extends ApertureBlock {

    public HEPLauncherBlock(String name, Settings settings) {
        super(name, settings);
    }
    
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }
}
