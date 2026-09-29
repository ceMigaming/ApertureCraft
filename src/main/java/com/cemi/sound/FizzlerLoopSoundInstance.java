package com.cemi.sound;

import com.cemi.block.FizzlerBlock;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * The hum a live field plays, looping for as long as the field is powered.
 *
 * Self-terminating on purpose: a field can die because its emitter was broken or
 * its chunk unloaded, neither of which can reach the client as a stop event, and
 * every block entity is too narrow a place to hang a sound's lifetime. The state
 * it needs to test is the synced CONNECTED flag, which survives that case.
 */
public class FizzlerLoopSoundInstance extends MovingSoundInstance {

    private static final double MAX_DISTANCE_SQ = 32.0 * 32.0;

    private final World world;
    private final BlockPos pos;

    public FizzlerLoopSoundInstance(World world, BlockPos pos) {
        super(ApertureSoundEvent.FIZZLER_LOOP_EVENT, SoundCategory.BLOCKS,
                SoundInstance.createRandom());
        this.world = world;
        // Handed straight from a block entity ticker, but the manager keeps it as a
        // map key, so it must not be a mutable cursor owned by something else.
        this.pos = pos.toImmutable();
        // repeatDelay of 0 is what selects OpenAL-level looping. SoundSystem treats
        // "repeatable with a delay" and "repeatable instantly" as opposite
        // requests: a positive delay routes the sound to the engine's re-arm path,
        // which only fires once a source reports itself stopped, and a static
        // source that has run out of buffer never does. A delay of 0 instead sets
        // AL_LOOPING on the source, so the sample repeats with no seam and the
        // engine never tears it down. Do not "fix" this to a positive number.
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = ApertureSoundEvent.FIZZLER_VOLUME;
        this.pitch = 1.0f;
        this.x = pos.getX() + 0.5;
        this.y = pos.getY() + 0.5;
        this.z = pos.getZ() + 0.5;
        this.attenuationType = AttenuationType.LINEAR;
    }

    /**
     * Whether a field at this position should be humming right now. Shared with
     * FizzlerLoopSoundManager so the two can never disagree - if the manager kept
     * restarting a hum that the instance was about to tear down, walking out of
     * earshot would stutter it twenty times a second instead of going quiet.
     */
    public static boolean shouldPlay(World world, BlockPos pos) {
        if (!FizzlerBlock.isConnectedAt(world, pos)) {
            return false;
        }
        double distSq = distanceToListener(pos);
        return distSq >= 0.0 && distSq <= MAX_DISTANCE_SQ;
    }

    @Override
    public void tick() {
        if (shouldPlay(world, pos)) {
            return;
        }
        this.setDone();
    }

    private static double distanceToListener(BlockPos pos) {
        // The listener is always the local player, so earshot is measured from them
        // rather than from every player in the world.
        PlayerEntity player = MinecraftClient.getInstance().player;
        return player == null ? -1.0 : player.squaredDistanceTo(
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }

    @Override
    public boolean canPlay() {
        return true;
    }
}
