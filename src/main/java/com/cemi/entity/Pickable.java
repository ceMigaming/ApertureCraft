package com.cemi.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public interface Pickable {

    public void pickUp(PlayerEntity player);

    public void drop();

    public default void handlePickedUp(PlayerEntity player, Entity entity) {
        Vec3d newPos = player.getPos().add(0, 1, 0)
                .add(new Vec3d(0, 0, 1).rotateX((float) Math.toRadians(-player.getPitch()))
                        .rotateY((float) Math.toRadians(-player.getYaw())).multiply(2));
        entity.setPosition(newPos);
    }
}
