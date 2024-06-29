package com.cemi.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class IndicatorLightBlockEntity extends BlockEntity {

    private int power = 0;

    public IndicatorLightBlockEntity(BlockPos pos, BlockState state) {
        super(ApertureBlockEntities.INDICATOR_LIGHT, pos, state);
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
        markDirty();
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        power = nbt.getInt("power");
        super.readNbt(nbt);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putInt("power", power);
        super.writeNbt(nbt);
    }
}
