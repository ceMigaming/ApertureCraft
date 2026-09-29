package com.cemi.sound;

import com.cemi.entity.RadioEntity;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;

public class RadioSoundInstance extends MovingSoundInstance {

    private final RadioEntity radio;

    // FIXME sync with server
    public RadioSoundInstance(RadioEntity radio) {
        super(ApertureSoundEvent.RADIO_LOOP_EVENT, SoundCategory.NEUTRAL,
                SoundInstance.createRandom());
        this.radio = radio;
    }

    @Override
    public void tick() {
        if (radio.isPlaying()) {
            this.volume = 1.0F;
        } else {
            this.setDone();
        }
    }

    @Override
    public boolean isRepeatable() {
        return true;
    }

    /**
     * Must stay 0 for the same reason as FizzlerLoopSoundInstance. SoundSystem
     * treats "repeatable with a delay" and "repeatable instantly" as opposite
     * requests: a positive delay routes the sound to the engine's re-arm path,
     * which only fires once a source reports itself stopped, and a static source
     * that has run out of buffer never does - so radio_loop would play once and
     * go quiet. A delay of 0 sets AL_LOOPING on the source instead, which repeats
     * the sample with no seam and is never torn down.
     */
    @Override
    public int getRepeatDelay() {
        return 0;
    }

    @Override
    public boolean shouldAlwaysPlay() {
        return true;
    }

    @Override
    public boolean canPlay() {
        return true;
    }

}
