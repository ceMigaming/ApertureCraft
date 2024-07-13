package com.cemi.world;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PortalData {
    public String uuid = "";
    public String channel = "main";
    public boolean isMaster = false;
    public int color = 0xFF9A00;
    private Vec3d pos = Vec3d.ZERO;
    private PortalData other = null;
    private RegistryKey<World> dimension = World.OVERWORLD;

    public PortalData() {
        this.color = 0xFF9A00;
        this.pos = Vec3d.ZERO;
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

    public PortalData setPos(Vec3d pos) {
        this.pos = pos;
        return this;
    }

    public PortalData setPos(double x, double y, double z) {
        this.pos = new Vec3d(x, y, z);
        return this;
    }

    public Vec3d getPos() {
        return pos;
    }

    public PortalData setOther(PortalData other) {
        this.other = other;
        return this;
    }

    public PortalData getOther() {
        return other;
    }

    public boolean isMaster() {
        return isMaster;
    }

    public String getChannel() {
        return channel;
    }

    public String getUuid() {
        return uuid;
    }

    public int getColor() {
        return color;
    }

    public RegistryKey<World> getDimension() {
        return dimension;
    }

    public PortalData setDimension(RegistryKey<World> dimension) {
        this.dimension = dimension;
        return this;
    }

    public PortalData readFromNBT(final NbtCompound nbt) {
        this.uuid = nbt.getString("uuid");
        this.channel = nbt.getString("channel");
        this.isMaster = nbt.getBoolean("isMaster");
        this.color = nbt.getInt("color");
        this.setPos(nbt.getDouble("x"), nbt.getDouble("y"), nbt.getDouble("z"));
        return this;
    }

    public NbtCompound writeToNBT(final NbtCompound nbt) {
        nbt.putString("uuid", this.uuid);
        nbt.putString("channel", this.channel);
        nbt.putBoolean("isMaster", this.isMaster);
        nbt.putInt("color", this.color);
        nbt.putDouble("x", this.pos.getX());
        nbt.putDouble("y", this.pos.getY());
        nbt.putDouble("z", this.pos.getZ());
        return nbt;
    }

    public static PortalData getPortalData(ItemStack stack, boolean isMaster) {
        NbtCompound nbt = stack.getOrCreateNbt();
        if (isMaster) {
            if (!nbt.contains("portalDataMaster")) {
                nbt.put("portalDataMaster", new PortalData().writeToNBT(new NbtCompound()));
            }
            return new PortalData().readFromNBT(nbt.getCompound("portalDataMaster"));
        } else {
            if (!nbt.contains("portalDataSlave")) {
                nbt.put("portalDataSlave", new PortalData().writeToNBT(new NbtCompound()));
            }
            return new PortalData().readFromNBT(nbt.getCompound("portalDataSlave"));
        }
    }

    public PortalData reset() {
        this.uuid = "";
        this.channel = "main";
        this.isMaster = false;
        this.color = 0xFF9A00;
        this.pos = Vec3d.ZERO;
        this.other = null;
        this.dimension = World.OVERWORLD;
        return this;
    }

}
