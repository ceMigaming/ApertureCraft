package com.cemi.block.entity;

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

public class ApertureDoorBlockEntity extends BlockEntity implements GeoBlockEntity {

    protected static final RawAnimation OPEN =
            RawAnimation.begin().thenPlayAndHold("animation.door.open");
    protected static final RawAnimation CLOSE =
            RawAnimation.begin().then("animation.door.close", LoopType.PLAY_ONCE);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public ApertureDoorBlockEntity(BlockPos pos, BlockState state) {
        super(ApertureBlockEntities.DOOR, pos, state);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", this::deployAnimController)
                .triggerableAnim("open", OPEN).triggerableAnim("close", CLOSE));
    }

    protected <E extends ApertureDoorBlockEntity> PlayState deployAnimController(
            final AnimationState<E> state) {
        return state.setAndContinue(OPEN);
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

}
