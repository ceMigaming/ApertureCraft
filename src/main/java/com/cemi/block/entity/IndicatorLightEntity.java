package com.cemi.block.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class IndicatorLightEntity extends BlockEntity {

    private boolean isMaster;
    private boolean isPowered;
    private ArrayList<BlockPos> connectedBlocks = new ArrayList<>();

    public IndicatorLightEntity(BlockPos pos, BlockState state) {
        super(ApertureBlockEntities.INDICATOR_LIGHT_ENTITY, pos, state);
    }

    public boolean isPowered() {
        return isPowered;
    }

    public void setPowered(boolean powered) {
        isPowered = powered;
    }

    public boolean isMaster() {
        return isMaster;
    }

    public void setMaster(boolean master) {
        isMaster = master;
    }

    public ArrayList<BlockPos> getConnectedBlocks() {
        return connectedBlocks;
    }

    public void setConnectedBlocks(ArrayList<BlockPos> connectedBlocks) {
        this.connectedBlocks = connectedBlocks;
    }

    public void addConnectedBlock(BlockPos pos) {
        connectedBlocks.add(pos);
    }

    public void removeConnectedBlockRecursive(BlockPos pos) {
        connectedBlocks.remove(pos);
        Direction[] directions = Direction.values();
        for (Direction direction : directions) {
            BlockPos newPos = pos.offset(direction);
            if (connectedBlocks.contains(newPos)) {
                removeConnectedBlockRecursive(newPos);
            }
        }
    }

    public Set<BlockPos> getConnectedBlocksRecursive(BlockPos pos) {
        Set<BlockPos> connectedBlocks = new HashSet<>();
        connectedBlocks.add(pos);
        Direction[] directions = Direction.values();
        for (Direction direction : directions) {
            BlockPos newPos = pos.offset(direction);
            if (!connectedBlocks.contains(newPos)) {
                connectedBlocks.addAll(getConnectedBlocksRecursive(newPos, connectedBlocks));
            }
        }
        System.out.println("--------------------");
        System.out.println(connectedBlocks.size());
        connectedBlocks.stream().forEach(System.out::println);
        return connectedBlocks;
    }

    public Set<BlockPos> getConnectedBlocksRecursive(BlockPos pos, Set<BlockPos> connectedBlocks) {
        connectedBlocks.add(pos);
        Direction[] directions = Direction.values();
        for (Direction direction : directions) {
            BlockPos newPos = pos.offset(direction);
            if (!connectedBlocks.contains(newPos)) {
                connectedBlocks.addAll(getConnectedBlocksRecursive(newPos, connectedBlocks));
            }
        }
        return connectedBlocks;
    }

    public BlockPos findMasterBlock() {
        Direction[] directions = Direction.values();
        for (Direction direction : directions) {
            BlockPos newPos = pos.offset(direction);
            if (world.getBlockEntity(newPos) instanceof IndicatorLightEntity) {
                IndicatorLightEntity entity = (IndicatorLightEntity) world.getBlockEntity(newPos);
                if (entity.isMaster()) {
                    return newPos;
                }
            }
        }
        return null;
    }

    public void clearConnectedBlocks() {
        connectedBlocks.clear();
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        nbt.putBoolean("isMaster", isMaster);
        nbt.putBoolean("isPowered", isPowered);
        nbt.putInt("connectedBlocksSize", connectedBlocks.size());
        for (int i = 0; i < connectedBlocks.size(); i++) {
            nbt.putLong("connectedBlock" + i, connectedBlocks.get(i).asLong());
        }
        super.readNbt(nbt);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        isMaster = nbt.getBoolean("isMaster");
        isPowered = nbt.getBoolean("isPowered");
        int connectedBlocksSize = nbt.getInt("connectedBlocksSize");
        for (int i = 0; i < connectedBlocksSize; i++) {
            connectedBlocks.add(BlockPos.fromLong(nbt.getLong("connectedBlock" + i)));
        }
        super.writeNbt(nbt);
    }
}
