package com.cemi.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.PersistentState;

public class PortalGunState extends PersistentState {

    public static final String DATA_ID = "PortalGunState";
    public HashSet<PortalData> portalList;
    public HashMap<String, ArrayList<ChannelData>> channelList;
    public boolean initialized;

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'writeNbt'");
    }

}
