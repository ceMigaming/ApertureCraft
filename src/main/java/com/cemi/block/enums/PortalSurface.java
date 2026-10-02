package com.cemi.block.enums;

import net.minecraft.util.StringIdentifiable;

public enum PortalSurface implements StringIdentifiable {
    CONCRETE("concrete"),
    METAL("metal");

    private final String name;

    private PortalSurface(String name) {
        this.name = name;
    }

    public String toString() {
        return this.asString();
    }

    public String asString() {
        return this.name;
    }

    public PortalSurface next() {
        return this == CONCRETE ? METAL : CONCRETE;
    }
    
}
