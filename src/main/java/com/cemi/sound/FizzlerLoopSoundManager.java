package com.cemi.sound;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Keeps one hum alive per live field.
 *
 * The hum repeats itself: FizzlerLoopSoundInstance is built with a repeat delay of
 * 0, which makes the sound engine set AL_LOOPING on the source, so one instance
 * plays the sample over and over with no seam and is never torn down. Nothing here
 * has to retrigger it - the manager only has to guarantee that a live field has
 * exactly one instance, and drop it when the field is no longer worth hearing.
 *
 * Driven by the FizzlerBlockEntity client ticker, which is the only place that
 * knows a field is powered without trusting the block entity's cached partner,
 * which is never re-sent to clients.
 *
 * Instances are not stopped explicitly when a field dies - an emitter broken out
 * from under a loaded chunk cannot report it - so a dead field is noticed on the
 * next tick instead. That keeps the map bounded by the number of fizzlers the
 * client has ever seen rather than leaking a sound per placement.
 */
public final class FizzlerLoopSoundManager {

    private static final Map<BlockPos, FizzlerLoopSoundInstance> LOOPING = new HashMap<>();

    private FizzlerLoopSoundManager() {
    }

    public static void update(World world, BlockPos pos) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world != world) {
            // A dimension change leaves the sound manager alive but the fields behind.
            clear(client);
            return;
        }

        // Copied rather than keyed on the caller's position, which arrives from a
        // block entity ticker and is only immutable by convention.
        BlockPos key = pos.toImmutable();

        if (!FizzlerLoopSoundInstance.shouldPlay(world, key)) {
            stop(key);
            return;
        }

        // Asked of the instance rather than inferred from the map, because one that
        // tore itself down is still in the map and would otherwise block the field
        // from ever humming again.
        FizzlerLoopSoundInstance existing = LOOPING.get(key);
        if (existing != null && !existing.isDone()) {
            return;
        }

        FizzlerLoopSoundInstance instance = new FizzlerLoopSoundInstance(world, key);
        LOOPING.put(key, instance);
        client.getSoundManager().play(instance);
    }

    public static void stop(BlockPos pos) {
        BlockPos key = pos.toImmutable();
        FizzlerLoopSoundInstance instance = LOOPING.remove(key);
        if (instance != null) {
            MinecraftClient.getInstance().getSoundManager().stop(instance);
        }
    }

    public static void clear(MinecraftClient client) {
        LOOPING.values().forEach(client.getSoundManager()::stop);
        LOOPING.clear();
    }
}
