package com.cemi.client;

import com.cemi.client.fluid.ApertureFluidsClient;
import com.cemi.client.networking.AperturePacketHandler;
import com.cemi.client.render.ApertureColorProviders;
import com.cemi.client.render.ApertureRenderLayers;
import com.cemi.client.render.ApertureRenderers;
import com.cemi.client.render.PortalOverlayRenderer;
import com.cemi.particle.ApertureParticlesClient;
import com.cemi.util.ShaderHelper;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

public class ApertureCraftClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CoreShaderRegistrationCallback.EVENT.register(context -> {
            context.register(
                    new Identifier("aperturecraft", "portal"),
                    VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
                    ShaderHelper::setPortalShader);
        });

        ApertureRenderers.registerRenderers();
        ApertureRenderLayers.registerRenderLayers();
        ApertureParticlesClient.registerParticles();
        ApertureFluidsClient.registerFluids();
        ApertureColorProviders.registerColorProviders();
        AperturePacketHandler.registerPacketHandlers();
        ApertureCraftInput.registerInput();
    }
}
