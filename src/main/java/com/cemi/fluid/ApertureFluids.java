package com.cemi.fluid;

import com.cemi.ApertureCraft;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ApertureFluids {
    public static FlowableFluid FLOWING_NEUROTOXIN;
    public static FlowableFluid STILL_NEUROTOXIN;

    public static void registerFluids() {
        FLOWING_NEUROTOXIN = Registry.register(Registries.FLUID,
                new Identifier(ApertureCraft.MOD_ID, "flowing_neurotoxin"),
                new NeurotoxinFluid.Flowing());
        STILL_NEUROTOXIN = Registry.register(Registries.FLUID,
                new Identifier(ApertureCraft.MOD_ID, "neurotoxin"), new NeurotoxinFluid.Still());
    }
}
