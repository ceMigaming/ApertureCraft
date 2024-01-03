package com.cemi.registry.tag;

import com.cemi.ApertureCraft;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ApertureFluidTags {
    public static final TagKey<Fluid> NEUROTOXIN =
            TagKey.of(RegistryKeys.FLUID, new Identifier(ApertureCraft.MOD_ID, "neurotoxin"));

    public static void registerFluidTags() {}
}
