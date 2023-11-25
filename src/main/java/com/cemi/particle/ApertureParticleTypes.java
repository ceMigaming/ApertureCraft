package com.cemi.particle;

import com.cemi.ApertureCraft;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ApertureParticleTypes extends DefaultParticleType {
    private String name;

    protected ApertureParticleTypes(boolean alwaysShow) {
        super(alwaysShow);
    }

    protected ApertureParticleTypes(String name, boolean alwaysShow) {
        super(alwaysShow);
        this.name = name;
    }

    public static final DefaultParticleType DRIPPING_NEUROTOXIN = simple("dripping_neurotoxin");
    public static final DefaultParticleType UNDER_NEUROTOXIN = simple("under_neurotoxin");

    private static final DefaultParticleType[] PARTICLES = {DRIPPING_NEUROTOXIN, UNDER_NEUROTOXIN};

    public static void registerParticles() {
        for (DefaultParticleType particle : PARTICLES) {
            Registry.register(Registries.PARTICLE_TYPE,
                    new Identifier(ApertureCraft.MOD_ID,
                            ((ApertureParticleTypes) particle).getName()),
                    (DefaultParticleType) particle);
        }
    }

    private String getName() {
        return name;
    }

    public static DefaultParticleType simple(String name) {
        DefaultParticleType particle = new ApertureParticleTypes(name, false);
        return particle;
    }
}
