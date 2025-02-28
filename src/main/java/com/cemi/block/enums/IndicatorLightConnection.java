package com.cemi.block.enums;

import net.minecraft.util.StringIdentifiable;

public enum IndicatorLightConnection implements StringIdentifiable {
    UP("up"),
    SIDE("side"),
    NONE("none"),
    SIDE_HORIZONTAL("side_horizontal"),
    SIDE_VERTICAL("side_vertical"),
    SIDE_UP_LEFT("side_up_left"),
    SIDE_UP_RIGHT("side_up_right"),
    SIDE_DOWN_RIGHT("side_down_right"),
    SIDE_DOWN_LEFT("side_down_left");

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
