package com.cemi.client.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.cemi.ApertureCraft;
import com.cemi.client.gui.screen.ApertureTitleScreen;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {

    @Inject(method = "setScreen", at = @At("HEAD"))
    public void setScreen(CallbackInfo ci, @Local LocalRef<@Nullable Screen> localScreen) {
        if (localScreen.get() == null || localScreen.get().getClass() != TitleScreen.class) {
            return;
        }

        ApertureCraft.LOGGER.info("Trying to redirect to Aperture Title Screen...");
        localScreen.set(new ApertureTitleScreen());
        ApertureCraft.LOGGER.info("Successfully redirected to Aperture Title Screen!");
    }


}
