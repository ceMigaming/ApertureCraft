package com.cemi.sound;

import com.cemi.ApertureCraft;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ApertureSoundEvent {
    public static final Identifier RADIO_LOOP_ID =
            new Identifier(ApertureCraft.MOD_ID, "radio_loop");
    public static SoundEvent RADIO_LOOP_EVENT = SoundEvent.of(RADIO_LOOP_ID);

    public static void registerSoundEvents() {
        Registry.register(Registries.SOUND_EVENT, RADIO_LOOP_ID, RADIO_LOOP_EVENT);
    }

}
