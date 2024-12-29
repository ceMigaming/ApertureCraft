package com.cemi.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.util.math.BlockPos;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ApertureDoorBlockEntity extends BlockEntity implements GeoBlockEntity {

        BlockPos upperLeftBlockPos;
        BlockPos upperRightBlockPos;
        BlockPos lowerLeftBlockPos;
        BlockPos lowerRightBlockPos;

        protected static final RawAnimation OPEN = RawAnimation.begin().thenPlayAndHold("animation.door.open");
        protected static final RawAnimation CLOSE = RawAnimation.begin().thenPlayAndHold("animation.door.close");

        private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

        public ApertureDoorBlockEntity(BlockPos pos, BlockState state) {
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

        @Override
        protected void writeNbt(NbtCompound nbt, WrapperLookup registryLookup) {
                nbt.putIntArray("upperLeftBlockPos", new int[] { upperLeftBlockPos.getX(),
                                upperLeftBlockPos.getY(), upperLeftBlockPos.getZ() });
                nbt.putIntArray("upperRightBlockPos", new int[] { upperRightBlockPos.getX(),
                                upperRightBlockPos.getY(), upperRightBlockPos.getZ() });
                nbt.putIntArray("lowerLeftBlockPos", new int[] { lowerLeftBlockPos.getX(),
                                lowerLeftBlockPos.getY(), lowerLeftBlockPos.getZ() });
                nbt.putIntArray("lowerRightBlockPos", new int[] { lowerRightBlockPos.getX(),
                                lowerRightBlockPos.getY(), lowerRightBlockPos.getZ() });
                super.writeNbt(nbt, registryLookup);
        }

        @Override
        public void readNbt(NbtCompound nbt, WrapperLookup registryLookup) {
                int[] upperLeftBlockPosArray = nbt.getIntArray("upperLeftBlockPos");
                upperLeftBlockPos = new BlockPos(upperLeftBlockPosArray[0], upperLeftBlockPosArray[1],
                                upperLeftBlockPosArray[2]);
                int[] upperRightBlockPosArray = nbt.getIntArray("upperRightBlockPos");
                upperRightBlockPos = new BlockPos(upperRightBlockPosArray[0], upperRightBlockPosArray[1],
                                upperRightBlockPosArray[2]);
                int[] lowerLeftBlockPosArray = nbt.getIntArray("lowerLeftBlockPos");
                lowerLeftBlockPos = new BlockPos(lowerLeftBlockPosArray[0], lowerLeftBlockPosArray[1],
                                lowerLeftBlockPosArray[2]);
                int[] lowerRightBlockPosArray = nbt.getIntArray("lowerRightBlockPos");
                lowerRightBlockPos = new BlockPos(lowerRightBlockPosArray[0], lowerRightBlockPosArray[1],
                                lowerRightBlockPosArray[2]);
                super.readNbt(nbt, registryLookup);
        }

}
