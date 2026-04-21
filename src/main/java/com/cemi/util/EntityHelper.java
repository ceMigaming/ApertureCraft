package com.cemi.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

public class EntityHelper {
    public static boolean isPlayerInFront(Entity entity, PlayerEntity player) {
        Vec3d forward = entity.getRotationVec(1.0F).normalize();

        Vec3d toPlayer = player.getPos()
                .subtract(entity.getPos())
                .normalize();

        double dot = forward.dotProduct(toPlayer);

        return dot > 0.7;
    }

    public static boolean hasLineOfSight(Entity entity, PlayerEntity player) {
        return entity.getWorld().raycast(new net.minecraft.world.RaycastContext(
                entity.getPos().add(0, entity.getEyeHeight(entity.getPose()), 0),
                player.getPos().add(0, player.getEyeHeight(player.getPose()), 0),
                net.minecraft.world.RaycastContext.ShapeType.COLLIDER,
                net.minecraft.world.RaycastContext.FluidHandling.NONE,
                entity)).getType() == HitResult.Type.MISS;
    }

}
