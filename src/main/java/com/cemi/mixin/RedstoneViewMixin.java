package com.cemi.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.cemi.ApertureCraft;
import com.cemi.block.ApertureBlocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.RedstoneView;
import net.minecraft.world.World;

@Mixin(RedstoneView.class)
public interface RedstoneViewMixin {

    @Shadow
    Direction[] DIRECTIONS = Direction.values();

    @Shadow
    int getEmittedRedstonePower(BlockPos pos, Direction face);

    @Inject(method = "getReceivedRedstonePower", at = @At("HEAD"), cancellable = true)
    private void onGetReceivedRedstonePower(BlockPos pos, CallbackInfoReturnable<Integer> info) {
        ApertureCraft.LOGGER.info("Block at " + pos + " is " + ((World) (Object) this).getBlockState(pos).getBlock().getName().getString());
        int maxRedstonePower = 0;
        Direction[] var3 = DIRECTIONS;
        int var4 = var3.length;

        for (int var5 = 0; var5 < var4; ++var5) {
            Direction direction = var3[var5];
            int currentRedstonePower = this.getEmittedRedstonePower(pos.offset(direction), direction);

            if(currentRedstonePower >= 15 && ((World) (Object) this).getBlockState(pos).getBlock() != ApertureBlocks.INDICATOR_LIGHT) {
                info.setReturnValue(15);
                info.cancel();
                return;
            }

            if (currentRedstonePower > maxRedstonePower) {
                maxRedstonePower = currentRedstonePower;
            }
        }


        ApertureCraft.LOGGER.info("RedstoneViewMixin: getReceivedRedstonePower: " + maxRedstonePower);

        info.setReturnValue(maxRedstonePower);
        info.cancel();
        return;
    }

}
