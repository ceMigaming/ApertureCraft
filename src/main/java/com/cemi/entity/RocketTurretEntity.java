package com.cemi.entity;

import java.util.UUID;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class RocketTurretEntity extends MobEntity implements GeoEntity {

    private int shootCooldown = 0;
    private static final int MAX_COOLDOWN = 40; // 2 seconds (20 ticks/sec)
    private static final float AIM_THRESHOLD = 0.05f; // radians (~3 degrees)

    private UUID rocketUuid;

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    // private boolean wasOpen = false;
    // private boolean isOpen = true;
    // private boolean isShooting = true;

    public Vec3d laserEnd = null;
    public Vec3d lastLaserEnd = null;

    public float yaw = 0.0f; // Rotation around the y-axis (pitch)
    public float pitch = 0.0f; // Rotation around the x-axis (roll)

    public float lastYaw = 0.0f; // Rotation around the y-axis (pitch) before the last update
    public float lastPitch = 0.0f; // Rotation around the x-axis (roll) before the last update

    protected RocketTurretEntity(EntityType<? extends MobEntity> entityType, World world) {
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

        if (shootCooldown > 0) {
            shootCooldown--;
        }

        PlayerEntity target = getClosestPlayer();
        if (target == null || rocketUuid != null)
            return;

        // Positions
        double dx = target.getX() - getX();
        double dy = (target.getEyeY()) - getEyeY() - target.getEyeHeight(target.getPose()) * 0.25f;
        double dz = target.getZ() - getZ();

        // Horizontal distance
        double distanceXZ = Math.sqrt(dx * dx + dz * dz);

        // Yaw (left/right)
        yaw = (float) (Math.atan2(dz, dx) - Math.PI / 2);
        yaw = lerpAngle(lastYaw, -yaw, 0.03f);

        // Pitch (up/down)
        pitch = (float) (-Math.atan2(dy, distanceXZ));
        pitch = lerpAngle(lastPitch, -pitch, 0.03f);

        this.setVelocity(0, 0, 0);

        Vec3d start = this.getPos().add(0, getEyeHeight(getPose()), 0);

        // Determine intended target
        Vec3d targetPos;
        targetPos = this.getRotationVec(1.0f)
                .rotateY(yaw)
                .rotateX(pitch)
                .multiply(10.0)
                .add(start);

        BlockHitResult hit = this.getWorld().raycast(new RaycastContext(
                start,
                targetPos,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                this));

        Vec3d finalTarget;

        // If we hit a block, clamp the beam
        if (hit.getType() == HitResult.Type.BLOCK) {
            // small offset so it doesn't clip into the block
            finalTarget = hit.getPos().add(Vec3d.of(hit.getSide().getVector()).multiply(0.01f));
            lastLaserEnd = finalTarget; // snap to block immediately to avoid weird lerp behavior when looking at walls
            laserEnd = finalTarget;
        } else {
            finalTarget = targetPos;
        }

        lastLaserEnd = laserEnd == null ? finalTarget : laserEnd;

        laserEnd = lastLaserEnd.lerp(finalTarget, 0.5);

        lastYaw = yaw;
        lastPitch = pitch;

        float targetYaw = (float) (Math.atan2(dz, dx) - Math.PI / 2);
        float targetPitch = (float) (-Math.atan2(dy, distanceXZ));

        float yawDiff = Math.abs(wrapAngle(targetYaw + yaw));
        float pitchDiff = Math.abs(wrapAngle(targetPitch + pitch));

        boolean isLockedOn = yawDiff < AIM_THRESHOLD && pitchDiff < AIM_THRESHOLD;

        if (isLockedOn && shootCooldown == 0) {
            shootRocket(target);
            shootCooldown = MAX_COOLDOWN;
        }
    }

    private float wrapAngle(float angle) {
        while (angle < -Math.PI)
            angle += Math.PI * 2;
        while (angle > Math.PI)
            angle -= Math.PI * 2;
        return angle;
    }

    private void shootRocket(PlayerEntity target) {
        if (this.getWorld().isClient)
            return;

        RocketEntity rocket = new RocketEntity(ApertureEntities.ROCKET, this.getWorld());

        Vec3d spawnPos = this.getPos().add(0, getEyeHeight(getPose()), 0);
        rocket.setPosition(spawnPos);

        // Direction toward player's eyes AT FIRE TIME
        Vec3d targetPos = target.getPos().add(0, target.getEyeHeight(target.getPose()), 0);
        Vec3d direction = targetPos.subtract(spawnPos).normalize();

        double speed = 0.7; // tweak this
        rocket.setVelocity(direction.multiply(speed));

        // Rotate rocket to face direction
        rocket.setYaw((float) (Math.atan2(direction.z, direction.x) * 180 / Math.PI) - 90f);
        rocket.setPitch((float) (-Math.atan2(direction.y,
                Math.sqrt(direction.x * direction.x + direction.z * direction.z)) * 180 / Math.PI));

        this.getWorld().spawnEntity(rocket);
        rocket.setOwner(this);
        if (!this.getWorld().isClient)
            rocketUuid = rocket.getUuid();
    }

    public PlayerEntity getClosestPlayer() {
        return this.getWorld().getClosestPlayer(this, 32);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        return source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY) ? super.damage(source, amount) : false;
    }

    private float lerpAngle(float current, float target, float speed) {
        float delta = target - current;

        // Wrap into [-PI, PI]
        while (delta < -Math.PI)
            delta += Math.PI * 2;
        while (delta > Math.PI)
            delta -= Math.PI * 2;

        return current + delta * speed;
    }

    public void setRocketUuid(UUID uuid) {
        this.rocketUuid = uuid;
    }
}
