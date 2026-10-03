package com.cemi.client;

import static com.cemi.block.ApertureBlock.DIRECTION_TO_PORTAL_SURFACE;

import com.cemi.block.ApertureBlocks;
import com.cemi.block.enums.PortalSurface;
import com.cemi.client.fluid.ApertureFluidsClient;
import com.cemi.client.model.ApertureModelPlugin;
import com.cemi.client.networking.AperturePacketHandler;
import com.cemi.client.render.ApertureColorProviders;
import com.cemi.client.render.ApertureRenderLayers;
import com.cemi.client.render.ApertureRenderers;
import com.cemi.item.ApertureItems;
import com.cemi.particle.ApertureParticlesClient;
import com.cemi.util.ShaderHelper;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class ApertureCraftClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CoreShaderRegistrationCallback.EVENT.register(context -> {
            context.register(
                    new Identifier("aperturecraft", "portal"),
                    VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
                    ShaderHelper::setPortalShader);
            // POSITION_COLOR_TEXTURE supplies exactly the Position/Color/UV0 the
            // fizzler shaders declare, and nothing more.
            context.register(
                    new Identifier("aperturecraft", "fizzler"),
                    VertexFormats.POSITION_COLOR_TEXTURE,
                    ShaderHelper::setFizzlerShader);
        });

        UseBlockCallback.EVENT.register((player, world, hand, hit) -> {
            BlockPos pos = hit.getBlockPos();
            BlockState state = world.getBlockState(pos);

            if (!(state.isOf(ApertureBlocks.SMALL_CONCRETE_TILE) || state.isOf(ApertureBlocks.SMALL_METAL_TILE))) {
                return ActionResult.PASS;
            }
            if (world.isClient()) {
                return ActionResult.PASS;
            }

            if (player.getStackInHand(hand).getItem() == ApertureItems.WRENCH) {
                Direction side = hit.getSide();
                if (DIRECTION_TO_PORTAL_SURFACE.containsKey(side)) {
                    if (player.isSneaking()) {
                        EnumProperty<PortalSurface> property = DIRECTION_TO_PORTAL_SURFACE.get(side);
                        if (state.get(property) == PortalSurface.NONE) {
                            world.setBlockState(pos, state.with(property, PortalSurface.CONCRETE));
                        } else {
                            world.setBlockState(pos, state.with(property, PortalSurface.NONE));
                        }
                    } else {
                        EnumProperty<PortalSurface> property = DIRECTION_TO_PORTAL_SURFACE.get(side);
                        PortalSurface currentSurface = state.get(property);
                        PortalSurface nextSurface = currentSurface.next();
                        world.setBlockState(pos, state.with(property, nextSurface));
                    }
                    return ActionResult.SUCCESS;
                }
            }

            return ActionResult.PASS;
        });

        ApertureRenderers.registerRenderers();
        ApertureRenderLayers.registerRenderLayers();
        ApertureParticlesClient.registerParticles();
        ApertureFluidsClient.registerFluids();
        ApertureColorProviders.registerColorProviders();
        AperturePacketHandler.registerPacketHandlers();
        ApertureCraftInput.registerInput();
        ModelLoadingPlugin.register(new ApertureModelPlugin());
    }
}
