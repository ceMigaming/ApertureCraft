package com.cemi.component;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public record PortalComponent(String uuid, String channel, boolean isMaster, int color, Vec3d pos,
        Optional<PortalComponent> other,
        RegistryKey<World> dimension) {
    public static final Codec<PortalComponent> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(
                Codec.STRING.fieldOf("uuid").forGetter(PortalComponent::uuid),
                Codec.STRING.fieldOf("channel").forGetter(PortalComponent::channel),
                Codec.BOOL.fieldOf("isMaster").forGetter(PortalComponent::isMaster),
                Codec.INT.fieldOf("color").forGetter(PortalComponent::color),
                Vec3d.CODEC.fieldOf("pos").forGetter(PortalComponent::pos),
                PortalComponent.CODEC.optionalFieldOf("other").forGetter(PortalComponent::other),
                World.CODEC.fieldOf("dimension").forGetter(PortalComponent::dimension))
                .apply(builder, PortalComponent::new);
    });

    public PortalComponent() {
        this("", "", false, 0, new Vec3d(0, 0, 0), Optional.empty(), World.OVERWORLD);
    }

    public static PortalComponent fromNBT(NbtCompound nbt) {
        String uuid = nbt.getString("uuid");
        String channel = nbt.getString("channel");
        boolean isMaster = nbt.getBoolean("isMaster");
        int color = nbt.getInt("color");
        Vec3d pos = new Vec3d(nbt.getDouble("x"), nbt.getDouble("y"), nbt.getDouble("z"));
        RegistryKey<World> dimension = RegistryKey.of(RegistryKeys.WORLD, Identifier.ofVanilla(nbt.getString("dimension")));
        if (nbt.contains("other")) {
            PortalComponent data = fromNBT(nbt.getCompound("other"));
            Optional<PortalComponent> other = Optional.of(data);
            return new PortalComponent(uuid, channel, isMaster, color, pos, other, dimension);
        }
        return new PortalComponent(uuid, channel, isMaster, color, pos, Optional.empty(), dimension);
    }

    public static NbtCompound toNBT(PortalComponent data) {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("uuid", data.uuid());
        nbt.putString("channel", data.channel());
        nbt.putBoolean("isMaster", data.isMaster());
        nbt.putInt("color", data.color());
        nbt.putDouble("x", data.pos().x);
        nbt.putDouble("y", data.pos().y);
        nbt.putDouble("z", data.pos().z);
        nbt.putString("dimension", data.dimension().getValue().toString());
        data.other().ifPresent(other -> nbt.put("other", toNBT(other)));
        return nbt;
    }
}
