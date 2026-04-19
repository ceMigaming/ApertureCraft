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

    public default float getEntityRotationWhenHolding() {
        return 0;
    }

    public default float getHoldDistance() {
        return 5;
    }

    public default void handlePickedUp(World world, PlayerEntity player, Entity entity) {
        Vec3d newPos = player.getEyePos();
        Vec3d offset = new Vec3d(0, 0, 1).rotateX((float) Math.toRadians(-player.getPitch()))
                .rotateY((float) Math.toRadians(-player.getYaw()));
        newPos = newPos.add(offset.multiply(getHoldDistance()));

        BlockHitResult hitResult = world.raycast(new RaycastContext(player.getEyePos(), newPos,
                ShapeType.COLLIDER, FluidHandling.NONE, entity));

        newPos = hitResult.getPos().subtract(offset);

        entity.setYaw(player.getYaw() + getEntityRotationWhenHolding());
        Vec3d delta = newPos.subtract(entity.getPos()).subtract(new Vec3d(0, entity.getHeight() / 2, 0));
        entity.setVelocity(delta.multiply(1));
        entity.velocityModified = true;

    }
}
