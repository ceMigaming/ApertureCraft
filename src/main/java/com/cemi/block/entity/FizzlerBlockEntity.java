package com.cemi.block.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import com.cemi.block.FizzlerBlock;
import com.cemi.entity.Fizzlable;
import com.cemi.item.PortalGunItem;
import com.cemi.sound.ApertureSoundEvent;
import com.cemi.sound.FizzlerLoopSoundManager;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation.LoopType;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class FizzlerBlockEntity extends BlockEntity implements GeoBlockEntity {

    protected static final RawAnimation IDLE = RawAnimation.begin()
            .then("animation.fizzler.idle", LoopType.LOOP);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @Nullable
    private BlockPos connectedToPos;

    /**
     * Players standing in the field last tick. Diffing against the current tick is
     * what turns a per-tick reset into a once-per-pass-through one, without any
     * global state to keep in sync across chunk loads or dimension changes.
     */
    private Set<UUID> lastTickOccupants = new HashSet<>();

    public FizzlerBlockEntity(BlockPos pos, BlockState state) {
        super(ApertureBlockEntities.FIZZLER, pos, state);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(
                new AnimationController<>(this, "controller",
                        event -> event.setAndContinue(IDLE)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    public static void tick(World world, BlockPos pos, BlockState state,
            FizzlerBlockEntity fizzler) {
        if (!(world instanceof ServerWorld serverWorld)) {
            return;
        }

        BlockPos partnerPos = fizzler.connectedToPos;
        if (partnerPos == null || !isPrimary(pos, partnerPos)) {
            return;
        }

        Box field = FizzlerBlock.getFieldBounds(pos, partnerPos);
        Set<UUID> occupants = new HashSet<>();

        for (PlayerEntity player : world.getEntitiesByClass(PlayerEntity.class, field,
                EntityPredicates.EXCEPT_SPECTATOR)) {
            if (fizzler.lastTickOccupants.add(player.getUuid())) {
                PortalGunItem.resetPortals(serverWorld, player);
            }
            occupants.add(player.getUuid());
        }
        fizzler.lastTickOccupants = occupants;

        for (MobEntity mob : world.getEntitiesByClass(MobEntity.class, field,
                EntityPredicates.EXCEPT_SPECTATOR)) {
            if (mob instanceof Fizzlable fizzlable) {
                // Played per entity rather than per field: a row of cubes going at
                // once should read as a row of cubes letting go, not one clap.
                serverWorld.playSound(null, mob.getX(), mob.getY(), mob.getZ(),
                        ApertureSoundEvent.MATERIAL_EMANCIPATION_EVENT, SoundCategory.BLOCKS,
                        ApertureSoundEvent.FIZZLER_VOLUME, 1.0F);
                mob.discard();
                fizzlable.onFizzled(serverWorld, field);
            }
        }
    }

    /**
     * Client half of the ticker, whose only job is the field hum. Reached through
     * FizzlerBlock#getTicker, so it never runs on a server.
     */
    public static void clientTick(World world, BlockPos pos, BlockState state,
            FizzlerBlockEntity fizzler) {
        // The cached partner is useless here: setConnectedPos never marks the block
        // entity dirty, so the NBT behind it may never be sent at all. The partner
        // is re-resolved from the world instead, at a cost of at most
        // MAX_SEARCH_DISTANCE block lookups per tick per emitter - less work than
        // the renderer already does per frame, to draw the very same field.
        BlockPos partnerPos = FizzlerBlock.getConnectedPartner(world, pos, state);
        if (partnerPos == null || !isPrimary(pos, partnerPos)) {
            // Stops the hum when the pair is broken, and keeps a swapped partner
            // from leaving the old emitter humming by itself.
            FizzlerLoopSoundManager.stop(pos);
            return;
        }
        FizzlerLoopSoundManager.update(world, pos);
    }

    /**
     * Both emitters span the same field, so only the lower one along the connection
     * axis does the work. Mirrors the election in FizzlerBlockEntityRenderer.
     */
    private static boolean isPrimary(BlockPos pos, BlockPos partnerPos) {
        if (pos.getZ() != partnerPos.getZ()) {
            return pos.getZ() < partnerPos.getZ();
        }
        return pos.getX() < partnerPos.getX();
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (connectedToPos != null) {
            nbt.putLong("connectedTo", connectedToPos.asLong());
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        connectedToPos = nbt.contains("connectedTo") ? BlockPos.fromLong(nbt.getLong("connectedTo")) : null;
    }

    @Nullable
    public BlockPos getConnectedPos() {
        return connectedToPos;
    }

    public void setConnectedPos(@Nullable BlockPos pos) {
        this.connectedToPos = pos;
    }

    public boolean isConnected() {
        return connectedToPos != null;
    }
}
