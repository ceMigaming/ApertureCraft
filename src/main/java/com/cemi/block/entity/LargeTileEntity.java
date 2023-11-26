package com.cemi.block.entity;

import com.cemi.ApertureCraft;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class LargeTileEntity extends BlockEntity {

    BlockPos upperLeftBlockPos;
    BlockPos upperRightBlockPos;
    BlockPos lowerLeftBlockPos;
    BlockPos lowerRightBlockPos;

    public LargeTileEntity(BlockPos pos, BlockState state) {
        super(ApertureBlockEntities.CONCRETE_LARGE_TILE_ENTITY, pos, state);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putIntArray("upperLeftBlockPos", new int[] {upperLeftBlockPos.getX(),
                upperLeftBlockPos.getY(), upperLeftBlockPos.getZ()});
        nbt.putIntArray("upperRightBlockPos", new int[] {upperRightBlockPos.getX(),
                upperRightBlockPos.getY(), upperRightBlockPos.getZ()});
        nbt.putIntArray("lowerLeftBlockPos", new int[] {lowerLeftBlockPos.getX(),
                lowerLeftBlockPos.getY(), lowerLeftBlockPos.getZ()});
        nbt.putIntArray("lowerRightBlockPos", new int[] {lowerRightBlockPos.getX(),
                lowerRightBlockPos.getY(), lowerRightBlockPos.getZ()});
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        int[] upperLeftBlockPosArray = nbt.getIntArray("upperLeftBlockPos");
        upperLeftBlockPos = new BlockPos(upperLeftBlockPosArray[0], upperLeftBlockPosArray[1],
                upperLeftBlockPosArray[2]);
        int[] upperRightBlockPosArray = nbt.getIntArray("upperRightBlockPos");
        upperRightBlockPos = new BlockPos(upperRightBlockPosArray[0], upperRightBlockPosArray[1],
                upperRightBlockPosArray[2]);
        int[] lowerLeftBlockPosArray = nbt.getIntArray("lowerLeftBlockPos");
        lowerLeftBlockPos = new BlockPos(lowerLeftBlockPosArray[0], lowerLeftBlockPosArray[1],
                lowerLeftBlockPosArray[2]);
        int[] lowerRightBlockPosArray = nbt.getIntArray("lowerRightBlockPos");
        lowerRightBlockPos = new BlockPos(lowerRightBlockPosArray[0], lowerRightBlockPosArray[1],
                lowerRightBlockPosArray[2]);
        super.readNbt(nbt);
    }

    public void setBlockPoses(BlockPos upperLeftBlockPos, BlockPos upperRightBlockPos,
            BlockPos lowerLeftBlockPos, BlockPos lowerRightBlockPos) {
        this.upperLeftBlockPos = upperLeftBlockPos;
        this.upperRightBlockPos = upperRightBlockPos;
        this.lowerLeftBlockPos = lowerLeftBlockPos;
        this.lowerRightBlockPos = lowerRightBlockPos;
        markDirty();
    }

    public BlockPos getUpperLeftBlockPos() {
        return upperLeftBlockPos;
    }

    public BlockPos getUpperRightBlockPos() {
        return upperRightBlockPos;
    }

    public BlockPos getLowerLeftBlockPos() {
        return lowerLeftBlockPos;
    }

    public BlockPos getLowerRightBlockPos() {
        return lowerRightBlockPos;
    }
}
