package com.cemi.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import net.minecraft.world.World;

public interface Pickable {

    public void pickUp(PlayerEntity player);

    public void drop();

    public default void handlePickedUp(World world, PlayerEntity player, Entity entity) {
        Vec3d newPos = player.getPos().add(0, 1, 0);
        Vec3d offset = new Vec3d(0, 0, 1).rotateX((float) Math.toRadians(-player.getPitch()))
                .rotateY((float) Math.toRadians(-player.getYaw()));
        newPos = newPos.add(offset.multiply(3));

        BlockHitResult hitResult = world.raycast(new RaycastContext(player.getPos(), newPos,
                ShapeType.COLLIDER, FluidHandling.NONE, entity));

        newPos = hitResult.getPos().subtract(offset);

        entity.setYaw(player.getYaw());
        entity.setPosition(newPos);
    }
}
