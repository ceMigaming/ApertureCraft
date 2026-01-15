package com.cemi.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.cemi.fluid.ApertureFluids;
import com.cemi.registry.tag.ApertureFluidTags;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.FluidState;

@Mixin(BackgroundRenderer.class)
public abstract class BackgroundRendererMixin {

	@Shadow
	private static float red;
	@Shadow
	private static float green;
	@Shadow
	private static float blue;

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;clearColor(FFFF)V", shift = At.Shift.BEFORE))
	private static void neurotoxin$fogColor(
			Camera camera,
			float tickDelta,
			ClientWorld world,
			int viewDistance,
			float skyDarkness,
			CallbackInfo ci) {
		CameraAccessor cam = (CameraAccessor) camera;
		FluidState fluid = cam.getArea().getFluidState(cam.getBlockPos());

		if (fluid.isIn(ApertureFluidTags.NEUROTOXIN)) {
			red = 0.0F;
			green = 0.25F;
			blue = 0.0F;
		}
	}

	@Inject(method = "applyFog", at = @At("HEAD"), cancellable = true)
	private static void neurotoxin$applyFog(
			Camera camera,
			BackgroundRenderer.FogType fogType,
			float viewDistance,
			boolean thickFog,
			float tickDelta,
			CallbackInfo ci) {
		CameraAccessor cam = (CameraAccessor) camera;
		FluidState fluid = cam.getArea().getFluidState(cam.getBlockPos());

		if (fluid.isIn(ApertureFluidTags.NEUROTOXIN)) {
			RenderSystem.setShaderFogStart(0.0F);
			RenderSystem.setShaderFogEnd(2.0F);
			ci.cancel();
		}
	}
}
