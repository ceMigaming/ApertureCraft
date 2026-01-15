package com.cemi.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.cemi.entity.damage.ApertureDamageTypes;
import com.cemi.registry.tag.ApertureFluidTags;

import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void aperturecraft$neurotoxinDamage(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;

        if (self.getWorld().isClient)
            return;

        BlockPos legsPos = self.getBlockPos();
        FluidState fluid = self.getWorld().getFluidState(legsPos);

        if (fluid.isIn(ApertureFluidTags.NEUROTOXIN)) {
            self.damage(
                    self.getWorld().getDamageSources()
                            .create(ApertureDamageTypes.NEUROTOXIN),
                    12.0F);
        }
    }
}
