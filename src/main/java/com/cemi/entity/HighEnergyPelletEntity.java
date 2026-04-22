package com.cemi.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
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

        Vec3d velocity = this.getVelocity();

        this.move(net.minecraft.entity.MovementType.SELF, velocity);

        // ✅ Manual collision detection
        if (!this.getWorld().isClient()) {
            for (PlayerEntity player : this.getWorld().getPlayers()) {
                if (player.getBoundingBox().intersects(this.getBoundingBox())) {
                    player.damage(this.getDamageSources().magic(), 100.0F);
                    this.kill();
                    return; // stop further processing after hit
                }
            }
        }

        if (this.horizontalCollision) {
            velocity = new Vec3d(-velocity.x, velocity.y, -velocity.z);
        }

        if (this.verticalCollision) {
            velocity = new Vec3d(velocity.x, -velocity.y, velocity.z);
        }

        this.setVelocity(velocity);

        // Lifetime logic
        if (this.getLifeTime() <= 0) {
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

    @Override
    public void remove(RemovalReason reason) {
        if (!this.getWorld().isClient()) {
            ((ServerWorld) this.getWorld()).spawnParticles(
                    ParticleTypes.POOF,
                    this.getX(), this.getY(), this.getZ(),
                    10,
                    0.1, 0.1, 0.1,
                    0.01);
        }

        super.remove(reason);
    }
}
