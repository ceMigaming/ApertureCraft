package com.cemi.block.entity;

import java.util.UUID;

import com.cemi.block.HEPCatcherBlock;

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

public class HEPCatcherBlockEntity extends BlockEntity implements GeoBlockEntity {

    protected static final RawAnimation IDLE = RawAnimation.begin().then("animation.hep_catcher.idle",
            LoopType.HOLD_ON_LAST_FRAME);
    protected static final RawAnimation CATCH = RawAnimation.begin().then("animation.hep_catcher.catch",
            LoopType.HOLD_ON_LAST_FRAME);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public HEPCatcherBlockEntity(BlockPos pos, BlockState state) {
        super(ApertureBlockEntities.HEP_CATCHER, pos, state);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(
                new AnimationController<>(this, "controller", event -> event.setAndContinue(IDLE))
                        .triggerableAnim("catch", CATCH));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    public void onPelletCaught() {
        if (this.world == null || this.world.isClient)
            return;

        BlockState state = world.getBlockState(pos);

        if (!state.get(HEPCatcherBlock.TRIGGERED)) {
            world.setBlockState(pos, state.with(HEPCatcherBlock.TRIGGERED, true), 3);

            // Play animation
            triggerAnim("controller", "catch");
        }
    }
}
