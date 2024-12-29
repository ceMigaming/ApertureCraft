package com.cemi.world;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;

public class ChannelData {
    public String uuid;
    public String channel;
    public int masterColor;
    public int slaveColor;

    public ChannelData(String uuid, String channel) {
        this.uuid = uuid;
        this.channel = channel;
    }

    public ChannelData setColors(int masterColor, int slaveColor) {
        this.masterColor = masterColor;
        this.slaveColor = slaveColor;
        return this;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof ChannelData && ((ChannelData) object).uuid.equals(this.uuid)
                && ((ChannelData) object).channel.equals(this.channel);
    }

    public ChannelData readFromNbt(NbtCompound nbt, WrapperLookup registryLookup) {
        this.uuid = nbt.getString("uuid");
        this.channel = nbt.getString("channel");
        this.masterColor = nbt.getInt("masterColor");
        this.slaveColor = nbt.getInt("slaveColor");
        return this;
    }

    public NbtCompound writeToNbt(NbtCompound nbt, WrapperLookup registryLookup) {
        nbt.putString("uuid", this.uuid);
        nbt.putString("channel", this.channel);
        nbt.putInt("masterColor", this.masterColor);
        nbt.putInt("slaveColor", this.slaveColor);
        return nbt;
    }
}
