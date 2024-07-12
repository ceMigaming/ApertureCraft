package com.cemi.block;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class ApertureFloorButton extends ApertureBlock implements BlockEntityProvider {

    public ApertureFloorButton(String name, Settings settings) {
        super(name, settings);
        // TODO Auto-generated constructor stub
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos arg0, BlockState arg1) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createBlockEntity'");
    }

}
