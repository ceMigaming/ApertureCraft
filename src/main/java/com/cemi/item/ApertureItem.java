package com.cemi.item;

import com.cemi.ApertureCraft;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ApertureItem extends Item {

    private final String name;

    public ApertureItem(String name, Settings settings) {
        super(settings);
        this.name = name;
    }

    public void register() {
        Registry.register(Registries.ITEM, new Identifier(ApertureCraft.MOD_ID, name), this);
    }

}
