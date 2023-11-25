package com.cemi.item;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ApertureItem extends Item {

    private String name;

    public ApertureItem(String name, Settings settings) {
        super(settings);
        this.name = name;
    }

    public void register() {
        Registry.register(Registries.ITEM, name, this);
    }

}
