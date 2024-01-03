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
        System.out.println(radio.isPlaying());
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

    @Override
    public boolean shouldAlwaysPlay() {
        return true;
    }

    @Override
    public boolean canPlay() {
        return true;
    }

}
