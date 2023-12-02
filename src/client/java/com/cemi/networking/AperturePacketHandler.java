package com.cemi.networking;

import com.cemi.entity.RadioEntity;
import com.cemi.sound.RadioSoundInstance;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class AperturePacketHandler {

    public static void registerPacketHandlers() {
        ClientPlayNetworking.registerGlobalReceiver(ApertureNetworkingConstants.RADIO_PLAY_ID,
                (client, handler, buf, responseSender) -> {
                    client.execute(() -> {
                        client.getSoundManager()
                                .play(new RadioSoundInstance((RadioEntity) client.player.getWorld()
                                        .getEntityById(buf.readInt())));
                        System.out.println("Playing radio");
                    });
                });
    }
}
