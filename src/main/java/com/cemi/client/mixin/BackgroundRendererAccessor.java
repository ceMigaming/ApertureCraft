package com.cemi.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.render.BackgroundRenderer;

@Mixin(BackgroundRenderer.class)
public interface BackgroundRendererAccessor {

    @Accessor("red")
    static void setRed(float value) {
        throw new AssertionError();
    }

    @Accessor("green")
    static void setGreen(float value) {
        throw new AssertionError();
    }

    @Accessor("blue")
    static void setBlue(float value) {
        throw new AssertionError();
    }
}
