package com.cemi.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.cemi.registry.tag.ApertureFluidTags;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.CameraSubmersionType;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;

@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {

	@Shadow
	private static float red;
	@Shadow
	private static float green;
	@Shadow
	private static float blue;
	@Shadow
	private static long lastWaterFogColorUpdateTime = -1L;

	@Inject(method = "render(Lnet/minecraft/client/render/Camera;FLnet/minecraft/client/world/ClientWorld;IF)V",
			at = @At(value = "TAIL"))
	private static void render(Camera camera, float tickDelta, ClientWorld world, int viewDistance,
			float skyDarkness, CallbackInfo info) {
		CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();

		if (cameraSubmersionType == CameraSubmersionType.NONE) {
			BlockPos blockPos = ((CameraMixin) camera).getBlockPos();
			Vec3d pos = ((CameraMixin) camera).getPos();
			BlockView area = ((CameraMixin) camera).getArea();
			FluidState fluidState = area.getFluidState(blockPos);
			if (fluidState.isIn(ApertureFluidTags.NEUROTOXIN)
					&& pos.y < (double) ((float) blockPos.getY()
							+ fluidState.getHeight(area, blockPos))) {
				red = 0.0F;
				green = 0.2F;
				blue = 0.1F;
				lastWaterFogColorUpdateTime = -1L;
				RenderSystem.clearColor(red, green, blue, 0.0F);
			}
		}
	}
}
