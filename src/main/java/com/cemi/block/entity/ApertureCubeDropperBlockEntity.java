package com.cemi.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.util.math.BlockPos;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.Animation.LoopType;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ApertureCubeDropperBlockEntity extends BlockEntity implements GeoBlockEntity {

    protected static final RawAnimation IDLE_OPEN = RawAnimation.begin().then("animation.cube_dropper.idle_open",
            LoopType.LOOP);
    protected static final RawAnimation OPEN = RawAnimation.begin().then("animation.cube_dropper.open",
            LoopType.PLAY_ONCE);
    protected static final RawAnimation IDLE_CLOSED = RawAnimation.begin().then("animation.cube_dropper.idle_closed",
            LoopType.LOOP);
    protected static final RawAnimation CLOSE = RawAnimation.begin().then("animation.cube_dropper.close",
            LoopType.PLAY_ONCE);
    protected static final RawAnimation DISPENSE = RawAnimation.begin().then("animation.cube_dropper.dispense",
            LoopType.PLAY_ONCE);

    private BlockPos masterPos;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public ApertureCubeDropperBlockEntity(BlockPos pos, BlockState state) {
        super(ApertureBlockEntities.CUBE_DROPPER_BLOCK_ENTITY, pos, state);
    }

    public void setMasterPos(BlockPos masterPos) {
        this.masterPos = masterPos;
    }

    public BlockPos getMasterPos() {
        return this.masterPos;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(
                new AnimationController<>(this, "controller", event -> event.setAndContinue(IDLE_CLOSED))
                        .triggerableAnim("open", OPEN).triggerableAnim("close", CLOSE).triggerableAnim("idle_open",
                                IDLE_OPEN)
                        .triggerableAnim("idle_close", IDLE_CLOSED));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void readNbt(NbtCompound nbt, WrapperLookup registryLookup) {
        this.masterPos = BlockPos.fromLong(nbt.getLong("masterPos"));
        super.readNbt(nbt, registryLookup);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, WrapperLookup registryLookup) {
        // TODO Auto-generated method stub
        nbt.putLong("masterPos", this.masterPos.asLong());
        super.writeNbt(nbt, registryLookup);
    }
}
