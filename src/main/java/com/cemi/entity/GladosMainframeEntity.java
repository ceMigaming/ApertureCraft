package com.cemi.entity;

import com.cemi.util.EntityHelper;
import com.cemi.ApertureCraft;

import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class GladosMainframeEntity extends MobEntity implements GeoEntity {

    private static final double TARGET_RANGE = 32.0;
    private static final float TURN_SPEED = 0.12f;
    private static final int RETARGET_TICKS = 10;

    /**
     * Model-space aim angles in radians, consumed by GladosMainframeModel to rotate
     * the "mf" and "head" group.
     * The entity's own yaw is deliberately never set: GeoEntityRenderer already
     * applies
     * 180 - getYRot() to the whole model, so a world yaw here would be counted
     * twice.
     */
    public float aimYaw = 0.0f;
    public float aimPitch = 0.0f;

    public float prevAimYaw = 0.0f;
    public float prevAimPitch = 0.0f;

    private PlayerEntity target;
    private int retargetTimer = 0;

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    protected GladosMainframeEntity(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
        setNoGravity(true);
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
    public boolean hasNoGravity() {
        return true;
    }

    @Override
    public boolean isInvulnerable() {
        return true;
    }

    @Override
    public float getEyeHeight(EntityPose pose) {
        return 0.0f;
    }

    @Override
    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return 0.25f;
    }

    @Override
    public void tick() {
        super.tick();

        // Aiming is purely cosmetic, so it is resolved per-viewer instead of being
        // tracked and synced. That keeps the server free of any target scanning.
        if (!getWorld().isClient)
            return;

        prevAimYaw = aimYaw;
        prevAimPitch = aimPitch;

        if (retargetTimer-- <= 0) {
            retargetTimer = RETARGET_TICKS;
            target = getWorld().getClosestPlayer(this, TARGET_RANGE);
        }

        if (target == null || !target.isAlive())
            return;

        setVelocity(0, 0, 0);

        double dx = target.getX() - getX();
        double dy = target.getEyeY() - getEyeY();
        double dz = target.getZ() - getZ();

        double distanceXZ = Math.sqrt(dx * dx + dz * dz);

        float targetYaw = -(float) (Math.atan2(dz, dx) - Math.PI / 2);
        float targetPitch = (float) Math.atan2(dy, distanceXZ);

        aimYaw = EntityHelper.lerpAngle(aimYaw, targetYaw, TURN_SPEED);
        aimPitch = EntityHelper.lerpAngle(aimPitch, targetPitch, TURN_SPEED);
    }
}
