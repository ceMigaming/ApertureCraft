package com.cemi.sound;

import com.cemi.ApertureCraft;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ApertureSoundEvent {

    /**
     * Volume every fizzler sound plays at. The field is loud and constantly
     * audible, so its one-shots are kept well under the held hum rather than at
     * full volume, where they were startling next to it.
     */
    public static final float FIZZLER_VOLUME = 0.25F;

    public static final Identifier RADIO_LOOP_ID =
            new Identifier(ApertureCraft.MOD_ID, "radio_loop");
    public static final SoundEvent RADIO_LOOP_EVENT = SoundEvent.of(RADIO_LOOP_ID);

    /** Field hum, held open for as long as a paired field is live. */
    public static final Identifier FIZZLER_LOOP_ID =
            new Identifier(ApertureCraft.MOD_ID, "fizzler_lp_01");
    public static final SoundEvent FIZZLER_LOOP_EVENT = SoundEvent.of(FIZZLER_LOOP_ID);

    /** Played as a field comes up, i.e. when an emitter finds a partner. */
    public static final Identifier FIZZLER_START_ID =
            new Identifier(ApertureCraft.MOD_ID, "fizzler_start_01");
    public static final SoundEvent FIZZLER_START_EVENT = SoundEvent.of(FIZZLER_START_ID);

    /** Played as a field dies with the emitter anchoring it. */
    public static final Identifier FIZZLER_SHUTDOWN_ID =
            new Identifier(ApertureCraft.MOD_ID, "fizzler_shutdown_01");
    public static final SoundEvent FIZZLER_SHUTDOWN_EVENT = SoundEvent.of(FIZZLER_SHUTDOWN_ID);

    /** Shared by everything a field liberates: cubes, turrets, radios. */
    public static final Identifier MATERIAL_EMANCIPATION_ID =
            new Identifier(ApertureCraft.MOD_ID, "material_emancipation_01");
    public static final SoundEvent MATERIAL_EMANCIPATION_EVENT =
            SoundEvent.of(MATERIAL_EMANCIPATION_ID);

    public static void registerSoundEvents() {
        Registry.register(Registries.SOUND_EVENT, RADIO_LOOP_ID, RADIO_LOOP_EVENT);
        Registry.register(Registries.SOUND_EVENT, FIZZLER_LOOP_ID, FIZZLER_LOOP_EVENT);
        Registry.register(Registries.SOUND_EVENT, FIZZLER_START_ID, FIZZLER_START_EVENT);
        Registry.register(Registries.SOUND_EVENT, FIZZLER_SHUTDOWN_ID, FIZZLER_SHUTDOWN_EVENT);
        Registry.register(Registries.SOUND_EVENT, MATERIAL_EMANCIPATION_ID,
                MATERIAL_EMANCIPATION_EVENT);
    }

}
