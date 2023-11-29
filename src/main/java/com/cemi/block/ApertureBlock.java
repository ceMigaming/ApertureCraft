package com.cemi.block;

import com.cemi.ApertureCraft;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ApertureBlock extends Block {

    protected String name;
    protected boolean canPlacePortals = true;

    public ApertureBlock(String name, Settings settings) {
        super(settings);
        this.name = name;
        if (name.contains("metal")) {
            canPlacePortals = false;
        }
    }

    public void register() {
        Registry.register(Registries.BLOCK, new Identifier(ApertureCraft.MOD_ID, name), this);
        Registry.register(Registries.ITEM, new Identifier(ApertureCraft.MOD_ID, name),
                new BlockItem(this, new FabricItemSettings()));
    }

}
