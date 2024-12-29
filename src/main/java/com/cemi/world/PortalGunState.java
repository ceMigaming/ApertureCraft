package com.cemi.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.cemi.component.PortalComponent;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.world.PersistentState;

public class PortalGunState extends PersistentState {

    public static final String DATA_ID = "PortalGunState";
    public HashSet<PortalComponent> portalList;
    public HashMap<String, ArrayList<ChannelData>> channelList;
    public boolean initialized;

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, WrapperLookup registryLookup) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'writeNbt'");
    }

}
