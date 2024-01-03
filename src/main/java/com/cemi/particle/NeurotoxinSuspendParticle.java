package com.cemi.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

@Environment(EnvType.CLIENT)
public class NeurotoxinSuspendParticle extends SpriteBillboardParticle {
    NeurotoxinSuspendParticle(ClientWorld world, SpriteProvider spriteProvider, double x, double y,
            double z) {
        super(world, x, y - 0.125, z);
        this.setBoundingBoxSpacing(0.01F, 0.01F);
        this.setSprite(spriteProvider);
        this.scale *= this.random.nextFloat() * 0.6F + 0.2F;
        this.maxAge = (int) (16.0 / (Math.random() * 0.8 + 0.2));
        this.collidesWithWorld = false;
        this.velocityMultiplier = 1.0F;
        this.gravityStrength = 0.0F;
    }

    NeurotoxinSuspendParticle(ClientWorld world, SpriteProvider spriteProvider, double x, double y,
            double z, double velocityX, double velocityY, double velocityZ) {
        super(world, x, y - 0.125, z, velocityX, velocityY, velocityZ);
        this.setBoundingBoxSpacing(0.01F, 0.01F);
        this.setSprite(spriteProvider);
        this.scale *= this.random.nextFloat() * 0.6F + 0.6F;
        this.maxAge = (int) (16.0 / (Math.random() * 0.8 + 0.2));
        this.collidesWithWorld = false;
        this.velocityMultiplier = 1.0F;
        this.gravityStrength = 0.0F;
    }

    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    @Environment(EnvType.CLIENT)
    public static class UnderNeurotoxinFactory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public UnderNeurotoxinFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(DefaultParticleType defaultParticleType,
                ClientWorld clientWorld, double d, double e, double f, double g, double h,
                double i) {
            NeurotoxinSuspendParticle waterSuspendParticle =
                    new NeurotoxinSuspendParticle(clientWorld, this.spriteProvider, d, e, f);
            waterSuspendParticle.setColor(0.06F, 0.17F, 0.04F);
            return waterSuspendParticle;
        }
    }
}
