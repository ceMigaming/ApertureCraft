package com.cemi.block.entity;

import java.util.UUID;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation.LoopType;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class HEPLauncherBlockEntity extends BlockEntity implements GeoBlockEntity {

    private UUID activePelletUuid;

    protected static final RawAnimation IDLE = RawAnimation.begin().then("animation.hep_launcher.idle",
            LoopType.PLAY_ONCE);
    protected static final RawAnimation SHOOT = RawAnimation.begin().then("animation.hep_launcher.shoot",
            LoopType.PLAY_ONCE);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public HEPLauncherBlockEntity(BlockPos pos, BlockState state) {
        super(ApertureBlockEntities.HEP_LAUNCHER, pos, state);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(
                new AnimationController<>(this, "controller", event -> event.setAndContinue(IDLE))
                        .triggerableAnim("shoot", SHOOT));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    public void setActivePellet(UUID uuid) {
        this.activePelletUuid = uuid;
    }

    public UUID getActivePelletUuid() {
        return activePelletUuid;
    }

    public void clearActivePellet() {
        this.activePelletUuid = null;
    }
}
