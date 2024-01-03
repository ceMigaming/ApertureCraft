package com.cemi.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class TurretEntity extends MobEntity implements GeoEntity {
    protected static final RawAnimation OPEN_ANIM = RawAnimation.begin().thenLoop("open");
    protected static final RawAnimation CLOSE_ANIM = RawAnimation.begin().thenLoop("close");
    protected static final RawAnimation SHOOT_ANIM = RawAnimation.begin().thenLoop("shoot");

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    private boolean wasOpen = false;
    private boolean isOpen = true;
    private boolean isShooting = true;

    protected TurretEntity(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void registerControllers(ControllerRegistrar controllers) {

    }

    protected <E extends TurretEntity> PlayState shootingController(final AnimationState<E> event) {
        if (isOpen)
            return event.setAndContinue(OPEN_ANIM);
        if (wasOpen && !isOpen)
            return event.setAndContinue(CLOSE_ANIM);
        if (isShooting)
            return event.setAndContinue(SHOOT_ANIM);
        return PlayState.STOP;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

}
