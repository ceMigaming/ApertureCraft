package com.cemi.entity;

import java.util.Comparator;

import com.cemi.particle.ApertureParticleTypes;
import com.cemi.util.EntityHelper;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class TurretEntity extends MobEntity implements GeoEntity, Pickable {
    protected static final RawAnimation OPEN_ANIM = RawAnimation.begin().thenPlayAndHold("open");
    protected static final RawAnimation CLOSE_ANIM = RawAnimation.begin().thenPlayAndHold("close");
    protected static final RawAnimation SHOOT_ANIM = RawAnimation.begin().thenLoop("shoot");

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    private PlayerEntity holder = null;

    private boolean isOpen = false;
    private int attackCooldown = 0;
    private int particleCooldown = 0;
    private static final int PARTICLE_COOLDOWN_TIME = 4;
    private static final int ATTACK_COOLDOWN_TIME = 10;

    public Vec3d laserEnd = null;
    public Vec3d lastLaserEnd = null;

    private PlayerEntity player = null; // current target

    private int openTimer = 0;
    private static final int OPEN_DURATION = 20; // 1 second (adjust to your animation)

    protected TurretEntity(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void registerControllers(ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", e -> PlayState.STOP)
                .triggerableAnim("open", OPEN_ANIM)
                .triggerableAnim("close", CLOSE_ANIM)
                .triggerableAnim("shoot", SHOOT_ANIM));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (holder == null || !holder.equals(player)) {
            pickUp(player);
        } else {
            drop();
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void pickUp(PlayerEntity player) {
        holder = player;
        setNoGravity(true);
        fallDistance = 0;
    }

    @Override
    public void drop() {
        holder = null;
        setNoGravity(false);
        fallDistance = 0;
    }

    @Override
    public void tick() {
        super.tick();

        if (attackCooldown > 0) {
            attackCooldown--;
        }

        if (openTimer > 0) {
            openTimer--;
        }

        if (particleCooldown > 0) {
            particleCooldown--;
        }

        if (holder != null) {
            handlePickedUp(getWorld(), holder, this);
        }

        Box box = getBoundingBox().expand(8.5); // 17 block radius

        player = getWorld().getEntitiesByClass(
                PlayerEntity.class,
                box,
                plr -> !plr.isSpectator()).stream().min(Comparator.comparingDouble(this::squaredDistanceTo))
                .orElse(null);

        if (player != null
                && EntityHelper.isPlayerInFront(this, player)
                && EntityHelper.hasLineOfSight(this, player)) {

            if (!isOpen) {
                triggerAnim("controller", "open");
                isOpen = true;
                openTimer = OPEN_DURATION;
            }

            // spawn particles every 4 ticks while open
            if (openTimer == 0 && particleCooldown == 0) {
                SpawnParticles();
                particleCooldown = PARTICLE_COOLDOWN_TIME;
            }

            // Attack every 10 ticks (0.5 sec)
            if (attackCooldown == 0 && openTimer == 0) {
                AttackPlayer(player);
                attackCooldown = ATTACK_COOLDOWN_TIME;
                triggerAnim("controller", "shoot");
            }

        } else {
            if (isOpen) {
                triggerAnim("controller", "close");
                isOpen = false;
                player = null;
                lastLaserEnd = null;
            }
        }

        Vec3d start = this.getPos().add(0, getEyeHeight(getPose()), 0);

        // Determine intended target
        Vec3d targetPos;
        if (player != null && isOpen) {
            targetPos = player.getPos().add(0, player.getEyeHeight(player.getPose()) * 0.75, 0);
        } else {
            targetPos = this.getRotationVec(1.0f)
                    .multiply(10.0)
                    .add(start);
        }

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

        double alpha = (player != null && isOpen) ? 0.5 : 0.1;

        laserEnd = lastLaserEnd.lerp(finalTarget, alpha);
    }

    private void AttackPlayer(PlayerEntity player) {
        if (!this.getWorld().isClient) {
            float damage = (float) this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);

            player.damage(
                    this.getDamageSources().mobAttack(this),
                    damage);
        }
    }

    private void SpawnParticles() {

        if(this.getWorld().isClient) {
            return;
        }

        // spawn particles when shooting
        Vec3d forward = this.getRotationVec(1.0f);
        Vec3d right = new Vec3d(-forward.z, 0, forward.x).normalize();

        double sideOffset = 0.25; // distance from center (tweak this)
        double heightOffset = 0.1; // adjust if guns are higher/lower

        Vec3d basePos = this.getPos().add(0, getEyeHeight(getPose()), 0).add(right.multiply(-0.06))
                .add(forward.multiply(0.4));

        // Left and right gun positions
        Vec3d leftGun = basePos.add(right.multiply(-sideOffset));
        Vec3d rightGun = basePos.add(right.multiply(sideOffset));

        // Spawn particles at BOTH guns
        ServerWorld world = (ServerWorld) this.getWorld();

        world.spawnParticles(
                ApertureParticleTypes.TURRET_FIRE,
                leftGun.x, leftGun.y + heightOffset, leftGun.z,
                2, 0.01, 0.05, 0.01, 0.0);

        world.spawnParticles(
                ApertureParticleTypes.TURRET_FIRE,
                rightGun.x, rightGun.y + heightOffset, rightGun.z,
                2, 0.01, 0.05, 0.01, 0.0);
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        if (!this.getWorld().isClient && holder != player)
            this.kill();
        super.onPlayerCollision(player);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        return source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY) ? super.damage(source, amount) : false;
    }

    public static DefaultAttributeContainer.Builder createTurretAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 2.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 8.0);
    }

    public PlayerEntity getTarget() {
        if (isOpen)
            return player;
        return null;
    }
}
