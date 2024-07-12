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

public class ApertureFloorButtonBlockEntity extends BlockEntity implements GeoBlockEntity {
    BlockPos upperLeftBlockPos;
    BlockPos upperRightBlockPos;
    BlockPos lowerLeftBlockPos;
    BlockPos lowerRightBlockPos;

    protected static final RawAnimation OPEN =
            RawAnimation.begin().thenPlayAndHold("animation.floor-button.press");
    protected static final RawAnimation CLOSE =
            RawAnimation.begin().thenPlayAndHold("animation.door.close");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public ApertureFloorButtonBlockEntity(BlockPos pos, BlockState state) {
        super(ApertureBlockEntities.DOOR, pos, state);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", e -> PlayState.STOP)
                .triggerableAnim("open", OPEN).triggerableAnim("close", CLOSE));
    }

    public void setBlockPoses(BlockPos upperLeftBlockPos, BlockPos upperRightBlockPos,
            BlockPos lowerLeftBlockPos, BlockPos lowerRightBlockPos) {
        this.upperLeftBlockPos = upperLeftBlockPos;
        this.upperRightBlockPos = upperRightBlockPos;
        this.lowerLeftBlockPos = lowerLeftBlockPos;
        this.lowerRightBlockPos = lowerRightBlockPos;
        markDirty();
    }

    public BlockPos getUpperLeftBlockPos() {
        return upperLeftBlockPos;
    }

    public BlockPos getUpperRightBlockPos() {
        return upperRightBlockPos;
    }

    public BlockPos getLowerLeftBlockPos() {
        return lowerLeftBlockPos;
    }

    public BlockPos getLowerRightBlockPos() {
        return lowerRightBlockPos;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
