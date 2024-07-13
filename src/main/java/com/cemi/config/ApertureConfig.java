package com.cemi.config;

import com.cemi.ApertureCraft;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = ApertureCraft.MOD_ID)
public class ApertureConfig implements ConfigData {
    public boolean enableShaders = true;

    public boolean isEnableShaders() {
        return enableShaders;
    }

    public ApertureConfig withEnableShaders(boolean enableShaders) {
        this.enableShaders = enableShaders;
        return this;
    }
}
