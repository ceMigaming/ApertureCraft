package com.cemi.client.mixin;

import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import blue.endless.jankson.annotation.Nullable;
import net.minecraft.client.WindowEventHandler;
import net.minecraft.client.WindowSettings;
import net.minecraft.client.util.MonitorTracker;

@Mixin(net.minecraft.client.util.Window.class)
public class WindowMixin implements AutoCloseable {
    @Inject(
        method = "<init>",
        at = @At(
            value = "INVOKE",
            target = "Lorg/lwjgl/glfw/GLFW;glfwDefaultWindowHints()V",
            shift = At.Shift.AFTER
        )
    )
    private void enableDebugContext(WindowEventHandler eventHandler, MonitorTracker monitorTracker,
            WindowSettings settings, @Nullable String videoMode, String title, CallbackInfo ci) {
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_DEBUG_CONTEXT, GLFW.GLFW_TRUE);
    }

    @Override
    public void close() {
    }
}
