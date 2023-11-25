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
    // protected boolean isChiselable = true;

    public ApertureBlock(String name, Settings settings) {
        super(settings);
        // TODO Auto-generated constructor stub

        this.name = name;
        if (name.contains("metal")) {
            canPlacePortals = false;
        }
    }

    public void register() {
        Registry.register(Registries.BLOCK, new Identifier(ApertureCraft.MOD_ID, name), this);
        // register itemblock
        Registry.register(Registries.ITEM, new Identifier(ApertureCraft.MOD_ID, name),
                new BlockItem(this, new FabricItemSettings()));
    }

}
