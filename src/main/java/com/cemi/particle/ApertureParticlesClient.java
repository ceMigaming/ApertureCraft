package com.cemi.particle;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;

public class ApertureParticlesClient {

    public static void registerParticles() {
        ParticleFactoryRegistry.getInstance().register(ApertureParticleTypes.DRIPPING_NEUROTOXIN,
                NeurotoxinSuspendParticle.UnderNeurotoxinFactory::new);
        ParticleFactoryRegistry.getInstance().register(ApertureParticleTypes.UNDER_NEUROTOXIN,
                NeurotoxinBubbleParticle.Factory::new);
    }

}
