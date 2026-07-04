package com.cemi.block.entity;

import org.jetbrains.annotations.Nullable;

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

public class FizzlerBlockEntity extends BlockEntity implements GeoBlockEntity {

    protected static final RawAnimation IDLE = RawAnimation.begin()
            .then("animation.fizzler.idle", LoopType.LOOP);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @Nullable
    private BlockPos connectedToPos;

    public FizzlerBlockEntity(BlockPos pos, BlockState state) {
        super(ApertureBlockEntities.FIZZLER, pos, state);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(
                new AnimationController<>(this, "controller",
                        event -> event.setAndContinue(IDLE)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Nullable
    public BlockPos getConnectedPos() {
        return connectedToPos;
    }

    public void setConnectedPos(@Nullable BlockPos pos) {
        this.connectedToPos = pos;
    }

    public boolean isConnected() {
        return connectedToPos != null;
    }
}
