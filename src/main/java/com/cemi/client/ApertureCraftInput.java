package com.cemi.client;

import org.lwjgl.glfw.GLFW;
import com.cemi.ApertureCraft;
import com.cemi.networking.ApertureNetworkingConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.client.player.ClientPreAttackCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Hand;

public class ApertureCraftInput {

    public static KeyBinding portalGunReset;

    public static void registerInput() {
        portalGunReset = KeyBindingHelper
                .registerKeyBinding(new KeyBinding("key." + ApertureCraft.MOD_ID + ".reset",
                        InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, ApertureCraft.MOD_ID));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (portalGunReset.wasPressed()) {
                PacketByteBuf buf = PacketByteBufs.create();
                Hand hand = client.player.getActiveHand();
                buf.writeEnumConstant(hand);
                ClientPlayNetworking.send(ApertureNetworkingConstants.RESET_PORTALS_ID, buf);
            }
        });
        ClientPreAttackCallback.EVENT.register(new ClientPreAttackCallback() {

            @Override
            public boolean onClientPlayerPreAttack(MinecraftClient client,
                    ClientPlayerEntity player, int clickCount) {
                if (clickCount != 0)
                    return false;

                PacketByteBuf buf = PacketByteBufs.create();
                Hand hand = player.getActiveHand();
                buf.writeEnumConstant(hand);
                ClientPlayNetworking.send(ApertureNetworkingConstants.LEFT_CLICK_ID, buf);

                return false;
            }
        });
    }
}
