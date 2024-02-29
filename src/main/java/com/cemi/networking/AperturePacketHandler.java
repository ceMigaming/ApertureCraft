package com.cemi.networking;

import com.cemi.item.ApertureItem;
import com.cemi.item.PortalGunItem;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Hand;

public class AperturePacketHandler {
    public static void registerPacketHandlers() {
        ServerPlayNetworking.registerGlobalReceiver(ApertureNetworkingConstants.LEFT_CLICK_ID,
                (server, player, handler, buf, responseSender) -> {
                    server.execute(() -> {
                        Hand hand = buf.readEnumConstant(Hand.class);
                        if (player.getStackInHand(hand).getItem() instanceof ApertureItem) {
                            ((ApertureItem) player.getStackInHand(hand).getItem())
                                    .onLeftClick(player.getWorld(), player, hand);
                        }
                    });
                });
        ServerPlayNetworking.registerGlobalReceiver(ApertureNetworkingConstants.RESET_PORTALS_ID,
                (server, player, handler, buf, responseSender) -> {
                    server.execute(() -> {
                        Hand hand = buf.readEnumConstant(Hand.class);
                        if (player.getStackInHand(hand).getItem() instanceof PortalGunItem) {
                            ((PortalGunItem) player.getStackInHand(hand).getItem())
                                    .onResetPortals(player.getWorld(), player, hand);
                        }
                    });
                });
    }
}
