// Source code is decompiled from a .class file using FernFlower decompiler.
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
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class NeurotoxinBubbleParticle extends SpriteBillboardParticle {
    NeurotoxinBubbleParticle(ClientWorld clientWorld, double d, double e, double f, double g,
            double h, double i) {
        super(clientWorld, d, e, f);
        this.setBoundingBoxSpacing(0.02F, 0.02F);
        this.scale *= this.random.nextFloat() * 0.6F + 0.2F;
        this.velocityX =
                g * 0.20000000298023224 + (Math.random() * 2.0 - 1.0) * 0.019999999552965164;
        this.velocityY =
                h * 0.20000000298023224 + (Math.random() * 2.0 - 1.0) * 0.019999999552965164;
        this.velocityZ =
                i * 0.20000000298023224 + (Math.random() * 2.0 - 1.0) * 0.019999999552965164;
        this.maxAge = (int) (8.0 / (Math.random() * 0.8 + 0.2));
    }

    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        if (this.maxAge-- <= 0) {
            this.markDead();
        } else {
            this.velocityY += 0.002;
            this.move(this.velocityX, this.velocityY, this.velocityZ);
            this.velocityX *= 0.8500000238418579;
            this.velocityY *= 0.8500000238418579;
            this.velocityZ *= 0.8500000238418579;
            if (!this.world.getFluidState(BlockPos.ofFloored(this.x, this.y, this.z))
                    .isIn(FluidTags.WATER)) {
                this.markDead();
            }

        }
    }

    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(DefaultParticleType defaultParticleType,
                ClientWorld clientWorld, double d, double e, double f, double g, double h,
                double i) {
            NeurotoxinBubbleParticle waterBubbleParticle =
                    new NeurotoxinBubbleParticle(clientWorld, d, e, f, g, h, i);
            waterBubbleParticle.setSprite(this.spriteProvider);
            waterBubbleParticle.setColor(0.06F, 0.17F, 0.04F);
            return waterBubbleParticle;
        }
    }
}
