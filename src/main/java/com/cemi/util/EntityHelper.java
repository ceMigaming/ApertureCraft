package com.cemi.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
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

}
