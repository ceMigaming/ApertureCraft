package com.cemi.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ApertureFloorButtonBlockEntity extends BlockEntity implements GeoBlockEntity {

    protected static final RawAnimation PRESS =
            RawAnimation.begin().thenPlayAndHold("animation.floor-button.press");
    protected static final RawAnimation RELEASE =
            RawAnimation.begin().thenPlayAndHold("animation.floor-button.release");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public ApertureFloorButtonBlockEntity(BlockPos pos, BlockState state) {
        super(ApertureBlockEntities.FLOOR_BUTTON, pos, state);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", e -> PlayState.STOP)
                .triggerableAnim("press", PRESS).triggerableAnim("release", RELEASE));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
