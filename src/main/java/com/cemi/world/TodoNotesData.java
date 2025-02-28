package com.cemi.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;

public class TodoNotesData extends PersistentState {
    private HashMap<BlockPos, String> map = new HashMap<>();

    public TodoNotesData() {
    }

    public TodoNotesData(NbtCompound nbt) {
        this.map = deserializeMap(nbt.getCompound("map"));
    }

    public static TodoNotesData fromNbt(NbtCompound nbt) {
        return new TodoNotesData(nbt);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.put("map", serializeMap(map));
        return nbt;
    }

    public String getNote(BlockPos pos) {
        return map.get(pos);
    }

    public HashMap<BlockPos, String> getNotes() {
        return map;
    }

    public void addNote(BlockPos pos, String note) {
        if (this.map.containsKey(pos)) {
            this.map.remove(pos);
        }
        this.map.put(pos, note);
        this.markDirty();
    }

    public void removeNote(BlockPos pos) {
        if (this.map.containsKey(pos)) {
            this.map.remove(pos);
        }
    }

    public static final PersistentState.Type<TodoNotesData> TYPE = new Type<>(
            TodoNotesData::new,
            TodoNotesData::fromNbt,
            null);

    private static NbtCompound serializeMap(HashMap<BlockPos, String> map) {
        NbtCompound nbt = new NbtCompound();
        for (Map.Entry<BlockPos, String> entry : map.entrySet()) {
            nbt.putString(entry.getKey().toShortString(), entry.getValue());
        }
        return nbt;
    }

    private static HashMap<BlockPos, String> deserializeMap(NbtCompound nbt) {
        HashMap<BlockPos, String> map = new HashMap<>();
        for (String key : nbt.getKeys()) {
            String vals[] = key.split(", ");
            map.put(new BlockPos(Integer.parseInt(vals[0]), Integer.parseInt(vals[1]), Integer.parseInt(vals[2])),
                    nbt.getString(key));
        }
        return map;
    }
}