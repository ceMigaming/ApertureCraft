package com.cemi.item;

import com.cemi.ApertureCraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ApertureItem extends Item {

    private final String name;

    public ApertureItem(String name, Settings settings) {
        super(settings);
        this.name = name;
    }

    public void register() {
        Registry.register(Registries.ITEM, new Identifier(ApertureCraft.MOD_ID, name), this);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        onRightClick(world, user, hand);
        return super.use(world, user, hand);
    }

    public void onRightClick(World world, PlayerEntity user, Hand hand) {
        // System.out.println("Right click");
    }
    
    public void onLeftClick(World world, PlayerEntity user, Hand hand) {
        // System.out.println("Left click");
    }
    
    public void onScrollClick(World world, PlayerEntity user, Hand hand) {
        // System.out.println("Middle click");
    }

}
