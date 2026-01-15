package com.cemi.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.cemi.registry.tag.ApertureFluidTags;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.CameraSubmersionType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

@Mixin(Camera.class)
public abstract class CameraSubmersionMixin {

    @Inject(method = "getSubmersionType", at = @At("RETURN"), cancellable = true)
    private void aperturecraft$customSubmersionType(
            CallbackInfoReturnable<CameraSubmersionType> cir) {

        Camera camera = (Camera) (Object) this;

        // Use your accessor mixin
        BlockView world = ((CameraAccessor) camera).getArea();
        BlockPos pos = ((CameraAccessor) camera).getBlockPos();

        if (isInMyCustomFluid(world, pos)) {
            cir.setReturnValue(CameraSubmersionType.WATER);
        }
    }

    private boolean isInMyCustomFluid(BlockView world, BlockPos pos) {
        return world.getFluidState(pos).isIn(ApertureFluidTags.NEUROTOXIN);
    }
}
