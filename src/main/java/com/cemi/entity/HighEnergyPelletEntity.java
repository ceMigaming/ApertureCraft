package com.cemi.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;

public class HighEnergyPelletEntity extends Entity {

    private static final TrackedData<Integer> LIFE_TIME;

    static {
        LIFE_TIME = DataTracker.registerData(HighEnergyPelletEntity.class,
                TrackedDataHandlerRegistry.INTEGER);

    }

    public HighEnergyPelletEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(LIFE_TIME, 12 * 20);
    }

    @Override
    public void tick() {
        super.tick();
        this.setPos(this.getX() + this.getVelocity().x, this.getY() + this.getVelocity().y,
                this.getZ() + this.getVelocity().z);
        if (this.getWorld().getBlockState(this.getBlockPos()).isSolidBlock(this.getWorld(),
                this.getBlockPos())) {
            this.setVelocity(this.getVelocity().multiply(-1));
        }
        if (this.getLifeTime() <= 0) {
            if (this.getWorld().isClient())
                this.getWorld().addParticle(ParticleTypes.POOF, this.getX(), this.getY(),
                        this.getZ(), 0, 0, 0);
            this.kill();
        } else {
            this.dataTracker.set(LIFE_TIME, this.getLifeTime() - 1);
        }
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        this.dataTracker.set(LIFE_TIME, nbt.getInt("LifeTime"));
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putInt("LifeTime", this.getLifeTime());
    }

    public int getLifeTime() {
        return this.dataTracker.get(LIFE_TIME);
    }
}
