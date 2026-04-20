package com.cemi.entity;

import java.util.Comparator;

import com.cemi.util.EntityHelper;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
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

    private boolean wasOpen = false;
    private boolean isOpen = false;
    private boolean isShooting = false;

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
        if (holder != null) {
            handlePickedUp(getWorld(), holder, this);
        }
        super.tick();
        Box box = getBoundingBox().expand(5.0); // 10 block radius

        var player = getWorld().getEntitiesByClass(
                PlayerEntity.class,
                box,
                plr -> !plr.isSpectator()).stream().min(Comparator.comparingDouble(this::squaredDistanceTo))
                .orElse(null);

        if (player != null && EntityHelper.isPlayerInFront(this, player)) {
            if (!isOpen) {
                triggerAnim("controller", "open");
                isOpen = true;
            }
        } else {
            if (isOpen) {
                triggerAnim("controller", "close");
                isOpen = false;
            }
        }
    }

    private void AttackPlayer(PlayerEntity player) {
        if (!isShooting) {
            triggerAnim("controller", "shoot");
            isShooting = true;
        }
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

}
