package com.cemi.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

public class EntityHelper {

    private static final float TAU = (float) (Math.PI * 2);

    /**
     * Normalizes an angle in radians into the [-PI, PI] range so that differences
     * between two angles can be taken directly without crossing the wrap-around seam.
     */
    public static float wrapAngle(float angle) {
        while (angle < -Math.PI)
            angle += TAU;
        while (angle > Math.PI)
            angle -= TAU;
        return angle;
    }

    /**
     * Interpolates between two angles in radians along the shortest arc, so the
     * rotation never takes the long way around. {@code speed} is the fraction of the
     * remaining angle closed per tick, in (0, 1].
     */
    public static float lerpAngle(float current, float target, float speed) {
        return current + wrapAngle(target - current) * speed;
    }

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
