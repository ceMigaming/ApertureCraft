package com.cemi.fluid;

import java.util.Optional;
import java.util.Random;
import org.jetbrains.annotations.Nullable;
import com.cemi.block.ApertureBlocks;
import com.cemi.item.ApertureItems;
import com.cemi.particle.ApertureParticleTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public abstract class NeurotoxinFluid extends FlowableFluid {
    public NeurotoxinFluid() {}

    public Fluid getFlowing() {
        return ApertureFluids.FLOWING_NEUROTOXIN;
    }

    public Fluid getStill() {
        return ApertureFluids.NEUROTOXIN_STILL;
    }

    public Item getBucketItem() {
        return ApertureItems.NEUROTOXIN_BUCKET;
    }

    public void randomDisplayTick(World world, BlockPos pos, FluidState state, Random random) {
        if (!state.isStill() && !(Boolean) state.get(FALLING)) {
            if (random.nextInt(64) == 0) {
                world.playSound((double) pos.getX() + 0.5, (double) pos.getY() + 0.5,
                        (double) pos.getZ() + 0.5, SoundEvents.BLOCK_WATER_AMBIENT,
                        SoundCategory.BLOCKS, random.nextFloat() * 0.25F + 0.75F,
                        random.nextFloat() + 0.5F, false);
            }
        } else if (random.nextInt(10) == 0) {
            world.addParticle(ApertureParticleTypes.UNDER_NEUROTOXIN,
                    (double) pos.getX() + random.nextDouble(),
                    (double) pos.getY() + random.nextDouble(),
                    (double) pos.getZ() + random.nextDouble(), 0.0, 0.0, 0.0);
        }

    }

    @Nullable
    public ParticleEffect getParticle() {
        return ApertureParticleTypes.DRIPPING_NEUROTOXIN;
    }

    protected boolean isInfinite(World world) {
        return world.getGameRules().getBoolean(GameRules.WATER_SOURCE_CONVERSION);
    }

    protected void beforeBreakingBlock(WorldAccess world, BlockPos pos, BlockState state) {
        BlockEntity blockEntity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;
        Block.dropStacks(state, world, pos, blockEntity);
    }

    public int getFlowSpeed(WorldView world) {
        return 4;
    }

    public BlockState toBlockState(FluidState state) {
        return (BlockState) ApertureBlocks.NEUROTOXIN.getDefaultState().with(FluidBlock.LEVEL,
                getBlockStateLevel(state));
    }

    public boolean matchesType(Fluid fluid) {
        return fluid == ApertureFluids.FLOWING_NEUROTOXIN
                || fluid == ApertureFluids.NEUROTOXIN_STILL;
    }

    public int getLevelDecreasePerBlock(WorldView world) {
        return 1;
    }

    public int getTickRate(WorldView world) {
        return 5;
    }

    public boolean canBeReplacedWith(FluidState state, BlockView world, BlockPos pos, Fluid fluid,
            Direction direction) {
        return direction == Direction.DOWN && !fluid.isIn(FluidTags.WATER);
    }

    protected float getBlastResistance() {
        return 100.0F;
    }

    public Optional<SoundEvent> getBucketFillSound() {
        return Optional.of(SoundEvents.ITEM_BUCKET_FILL);
    }

    public static class Still extends NeurotoxinFluid {
        public Still() {}

        public int getLevel(FluidState state) {
            return 8;
        }

        public boolean isStill(FluidState state) {
            return true;
        }
    }

    public static class Flowing extends NeurotoxinFluid {
        public Flowing() {}

        protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
            super.appendProperties(builder);
            builder.add(new Property[] {LEVEL});
        }

        public int getLevel(FluidState state) {
            return (Integer) state.get(LEVEL);
        }

        public boolean isStill(FluidState state) {
            return false;
        }
    }
}
