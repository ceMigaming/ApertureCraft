package com.cemi.world;

import net.minecraft.util.math.BlockPos;

public class PortalData {
    public String uuid;
    public String channel;
    public boolean isMaster;
    public int color;
    private BlockPos pos;
    private PortalData other;

    public PortalData() {
        this.color = 0xFF9A00;
        this.pos = BlockPos.ORIGIN;
    }

    public PortalData setPortalData(String uuid, String channel, boolean isMaster) {
        this.uuid = uuid;
        this.channel = channel;
        this.isMaster = isMaster;
        return this;
    }

    public PortalData setColor(int color) {
        this.color = color;
        return this;
    }

    public PortalData setPos(BlockPos pos) {
        this.pos = pos;
        return this;
    }

    public PortalData setPos(int x, int y, int z) {
        this.pos = new BlockPos(x, y, z);
        return this;
    }

    public BlockPos getPos() {
        return pos;
    }

    public PortalData setOther(PortalData other) {
        this.other = other;
        return this;
    }

    public PortalData getOther() {
        return other;
    }

}
