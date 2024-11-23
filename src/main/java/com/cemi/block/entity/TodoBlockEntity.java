package com.cemi.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class TodoBlockEntity extends BlockEntity {
    public TodoBlockEntity(BlockPos pos, BlockState state) {
        super(ApertureBlockEntities.TODO, pos, state);
    }
}
