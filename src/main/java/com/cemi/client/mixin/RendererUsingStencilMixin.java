package com.cemi.client.mixin;

import com.cemi.client.render.PortalOverlayRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qouteall.imm_ptl.core.render.PortalRenderable;
import qouteall.imm_ptl.core.render.renderer.RendererUsingStencil;

@Mixin(RendererUsingStencil.class)
public class RendererUsingStencilMixin {

    @Inject(
            method = "doRenderPortal",
            at = @At(
                    value = "INVOKE",
                    target = "Lqouteall/imm_ptl/core/render/renderer/RendererUsingStencil;clampStencilValue(I)V",
                    shift = At.Shift.AFTER,
                    remap = false))
    private void aperturecraft$renderPortalOverlay(PortalRenderable portal, MatrixStack matrixStack, CallbackInfo ci) {
        PortalOverlayRenderer.renderAfterImmersivePortal(portal, matrixStack);
    }
}
