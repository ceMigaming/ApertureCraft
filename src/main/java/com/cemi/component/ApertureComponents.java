package com.cemi.component;

import com.cemi.ApertureCraft;

import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ApertureComponents {

    public static final ComponentType<PortalComponent> PORTAL_COMPONENT = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(ApertureCraft.MOD_ID, "portal"),
            ComponentType.<PortalComponent>builder().codec(PortalComponent.CODEC).build());

    public static void registerComponents() {
    }
}
