package com.cemi.entity;

import java.util.UUID;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class RocketEntity extends MobEntity implements GeoEntity {

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    private UUID ownerUuid;

    protected RocketEntity(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void registerControllers(ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();

        // Keep moving forward without changing direction
        this.setVelocity(this.getVelocity());
        this.velocityModified = true;

        spawnTrailParticles();

        // Optional: remove after some time
        if (this.age > 100) { // 5 seconds
            this.discard();
        }

        if (this.horizontalCollision || this.verticalCollision) {
            explode();
        }

    }

    private void explode() {
        this.getWorld().createExplosion(
                this,
                getX(),
                getY(),
                getZ(),
                2.0f,
                World.ExplosionSourceType.MOB);
        this.discard();
    }

    @Override
    public void remove(RemovalReason reason) {
        if (!this.getWorld().isClient) {
            var owner = ((ServerWorld) this.getWorld()).getEntity(ownerUuid);
            if (owner instanceof RocketTurretEntity o) {
                o.setRocketUuid(null);
            }
        }
        super.remove(reason);
    }

    private void spawnTrailParticles() {
        if (this.getWorld().isClient) {
            Vec3d back = this.getVelocity().normalize().multiply(-0.5);

            this.getWorld().addParticle(
                    net.minecraft.particle.ParticleTypes.SMOKE,
                    this.getX() + back.x,
                    this.getY() + back.y,
                    this.getZ() + back.z,
                    0, 0, 0);

            this.getWorld().addParticle(
                    net.minecraft.particle.ParticleTypes.FLAME,
                    this.getX() + back.x,
                    this.getY() + back.y,
                    this.getZ() + back.z,
                    0, 0, 0);
        }
    }

    @Override
    public boolean hasNoGravity() {
        return true;
    }

    @Override
    public boolean hasNoDrag() {
        return true;
    }

    public void setOwner(RocketTurretEntity owner) {
        this.ownerUuid = owner.getUuid();
    }
}
