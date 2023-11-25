package com.cemi.particles;

import com.cemi.particle.ApertureParticleTypes;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.particle.WaterBubbleParticle;
import net.minecraft.client.particle.WaterSuspendParticle;

public class ApertureParticlesClient {

    public static void registerParticles() {
        ParticleFactoryRegistry.getInstance().register(ApertureParticleTypes.DRIPPING_NEUROTOXIN,
                WaterSuspendParticle.UnderwaterFactory::new);
        ParticleFactoryRegistry.getInstance().register(ApertureParticleTypes.UNDER_NEUROTOXIN,
                WaterBubbleParticle.Factory::new);
    }

}
