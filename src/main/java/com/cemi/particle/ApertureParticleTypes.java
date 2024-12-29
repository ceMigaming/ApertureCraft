package com.cemi.particle;

import com.cemi.ApertureCraft;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ApertureParticleTypes extends SimpleParticleType {
    private String name;

    protected ApertureParticleTypes(boolean alwaysShow) {
        super(alwaysShow);
    }

    protected ApertureParticleTypes(String name, boolean alwaysShow) {
        super(alwaysShow);
        this.name = name;
    }

    public static final SimpleParticleType DRIPPING_NEUROTOXIN = simple("dripping_neurotoxin");
    public static final SimpleParticleType UNDER_NEUROTOXIN = simple("under_neurotoxin");

    private static final SimpleParticleType[] PARTICLES = {DRIPPING_NEUROTOXIN, UNDER_NEUROTOXIN};

    public static void registerParticles() {
        for (SimpleParticleType particle : PARTICLES) {
            Registry.register(Registries.PARTICLE_TYPE,
                    Identifier.of(ApertureCraft.MOD_ID,
                            ((ApertureParticleTypes) particle).getName()),
                    (SimpleParticleType) particle);
        }
    }

    private String getName() {
        return name;
    }

    public static SimpleParticleType simple(String name) {
        SimpleParticleType particle = new ApertureParticleTypes(name, false);
        return particle;
    }
}
