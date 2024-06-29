package com.cemi.block.entity;

import com.cemi.ApertureCraft;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation.LoopType;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class HEPLauncherBlockEntity extends BlockEntity implements GeoBlockEntity {

    protected static final RawAnimation IDLE =
            RawAnimation.begin().thenPlayAndHold("animation.hep_launcher.idle");
    protected static final RawAnimation SHOOT =
            RawAnimation.begin().then("animation.hep_launcher.shoot", LoopType.PLAY_ONCE);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public HEPLauncherBlockEntity(BlockPos pos, BlockState state) {
        super(ApertureBlockEntities.HEP_LAUNCHER, pos, state);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", this::deployAnimController)
                .triggerableAnim("idle", IDLE).triggerableAnim("shoot", SHOOT));
    }

    protected <E extends HEPLauncherBlockEntity> PlayState deployAnimController(
            final AnimationState<E> state) {
        return state.setAndContinue(IDLE);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
    }

    public void shootPellet() {
        ApertureCraft.LOGGER.info("Shooting pellet");
        triggerAnim("controller", "shoot");
    }

}
