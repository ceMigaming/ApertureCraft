package com.cemi.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

public class GhostBlockEntity extends Entity {

    private static final TrackedData<Integer> LIFE_TIME;

    static {
        LIFE_TIME = DataTracker.registerData(GhostBlockEntity.class,
                TrackedDataHandlerRegistry.INTEGER);

    }

    public GhostBlockEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(LIFE_TIME, 5 * 20);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getLifeTime() <= 0) {
            this.kill();
        } else {
            this.dataTracker.set(LIFE_TIME, this.getLifeTime() - 1);
        }
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {}

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {}

    public int getLifeTime() {
        return this.dataTracker.get(LIFE_TIME);
    }

}
