package com.cemi.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.cemi.item.ApertureItems;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.DamageTypeTags;

@Mixin(LivingEntity.class)
public class EntityMixin {

    @Inject(method = "damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", at = @At("HEAD"), cancellable = true)
    private void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {
        LivingEntity entity = (LivingEntity) (Object) this;
        for (ItemStack stack : entity.getEquippedItems()) {
            if (stack.getItem() == ApertureItems.LONG_FALL_BOOTS
                    && source.isIn(DamageTypeTags.IS_FALL)) {
                info.setReturnValue(false);
                info.cancel();
            }
        }
    }

}
