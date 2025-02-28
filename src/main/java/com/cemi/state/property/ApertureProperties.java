package com.cemi.state.property;

import com.cemi.block.enums.IndicatorLightConnection;

import net.minecraft.state.property.EnumProperty;

public class ApertureProperties {
    public static final EnumProperty<IndicatorLightConnection> EAST_WIRE_CONNECTION;
    public static final EnumProperty<IndicatorLightConnection> NORTH_WIRE_CONNECTION;
    public static final EnumProperty<IndicatorLightConnection> SOUTH_WIRE_CONNECTION;
    public static final EnumProperty<IndicatorLightConnection> WEST_WIRE_CONNECTION;

    static {
        EAST_WIRE_CONNECTION = EnumProperty.of("east", IndicatorLightConnection.class);
        NORTH_WIRE_CONNECTION = EnumProperty.of("north", IndicatorLightConnection.class);
        SOUTH_WIRE_CONNECTION = EnumProperty.of("south", IndicatorLightConnection.class);
        WEST_WIRE_CONNECTION = EnumProperty.of("west", IndicatorLightConnection.class);
    }
}
