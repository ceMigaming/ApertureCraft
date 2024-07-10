package com.cemi.mixin;

import java.util.Collection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;

@Mixin(PlayerLookup.class)
public class PlayerLookupMixin {

    @SuppressWarnings("resource") // false positive
    @Inject(method = "tracking", at = @At("RETURN"), cancellable = true)
    private static void tracking(ServerWorld world, ChunkPos pos,
            CallbackInfoReturnable<Collection<ServerPlayerEntity>> cir) {

        cir.setReturnValue(
                world.getChunkManager().threadedAnvilChunkStorage.getPlayersWatchingChunk(pos));
    }
}
