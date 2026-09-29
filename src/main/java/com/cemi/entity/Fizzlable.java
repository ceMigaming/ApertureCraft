package com.cemi.entity;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;

/**
 * Marks an entity that a fizzler field destroys. Being discarded is the default
 * outcome, so the hook is only for what has to happen afterwards.
 */
public interface Fizzlable {

    /**
     * Runs after this entity has been discarded, while the server is still inside
     * the field that fizzled it. {@code field} is the volume of that field.
     */
    public default void onFizzled(ServerWorld world, Box field) {
    }
}
