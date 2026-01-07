package com.cemi.block.enums;

import net.minecraft.util.StringIdentifiable;

public enum IndicatorLightConnection implements StringIdentifiable {
    NONE("none"),
    SIDE("side"),
    UP("up");

    private final String name;

    private IndicatorLightConnection(String name) {
        this.name = name;
    }

    public String toString() {
        return this.asString();
    }

    public String asString() {
        return this.name;
    }

    public boolean isConnected() {
        return this != NONE;
    }
}
