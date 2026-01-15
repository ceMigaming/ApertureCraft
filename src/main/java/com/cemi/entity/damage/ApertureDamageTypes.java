package com.cemi.entity.damage;

import com.cemi.ApertureCraft;

import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ApertureDamageTypes {
    public static final RegistryKey<DamageType> NEUROTOXIN = RegistryKey.of(RegistryKeys.DAMAGE_TYPE,
            new Identifier(ApertureCraft.MOD_ID, "neurotoxin"));
}
