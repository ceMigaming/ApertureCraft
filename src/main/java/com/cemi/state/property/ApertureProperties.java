package com.cemi.state.property;

import com.cemi.block.enums.IndicatorLightConnection;
import com.cemi.block.enums.PortalSurface;

import net.minecraft.state.property.EnumProperty;

public class ApertureProperties {
    public static final EnumProperty<IndicatorLightConnection> EAST_WIRE_CONNECTION = EnumProperty.of("east", IndicatorLightConnection.class);;
    public static final EnumProperty<IndicatorLightConnection> NORTH_WIRE_CONNECTION = EnumProperty.of("north", IndicatorLightConnection.class);;
    public static final EnumProperty<IndicatorLightConnection> SOUTH_WIRE_CONNECTION = EnumProperty.of("south", IndicatorLightConnection.class);;
    public static final EnumProperty<IndicatorLightConnection> WEST_WIRE_CONNECTION = EnumProperty.of("west", IndicatorLightConnection.class);;

    public static final EnumProperty<PortalSurface> PORTAL_SURFACE_NORTH = EnumProperty.of("portal_surface_north", PortalSurface.class);
    public static final EnumProperty<PortalSurface> PORTAL_SURFACE_EAST = EnumProperty.of("portal_surface_east", PortalSurface.class);
    public static final EnumProperty<PortalSurface> PORTAL_SURFACE_SOUTH = EnumProperty.of("portal_surface_south", PortalSurface.class);
    public static final EnumProperty<PortalSurface> PORTAL_SURFACE_WEST = EnumProperty.of("portal_surface_west", PortalSurface.class);
    public static final EnumProperty<PortalSurface> PORTAL_SURFACE_UP = EnumProperty.of("portal_surface_up", PortalSurface.class);
    public static final EnumProperty<PortalSurface> PORTAL_SURFACE_DOWN = EnumProperty.of("portal_surface_down", PortalSurface.class);

}
