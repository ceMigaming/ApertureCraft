package com.cemi.world;

import com.cemi.ApertureCraft;

import net.minecraft.server.world.ServerWorld;

public class WorldDataManager {
    public static TodoNotesData getData(ServerWorld world) {
        return world.getPersistentStateManager().getOrCreate(
                TodoNotesData.TYPE,
                ApertureCraft.MOD_ID
        );
    }
}