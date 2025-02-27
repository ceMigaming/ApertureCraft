package com.cemi.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class PedestalButtonBlockEntity extends BlockEntity implements GeoBlockEntity {
    protected static final RawAnimation PRESS =
            RawAnimation.begin().thenPlayAndHold("animation.pedestal-button.press");
    protected static final RawAnimation RELEASE =
            RawAnimation.begin().thenPlayAndHold("animation.pedestal-button.release");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public PedestalButtonBlockEntity(BlockPos pos, BlockState state) {
        super(ApertureBlockEntities.PEDESTAL_BUTTON, pos, state);
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
