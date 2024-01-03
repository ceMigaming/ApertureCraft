package com.cemi.client;

import com.cemi.client.fluid.ApertureFluidsClient;
import com.cemi.client.render.ApertureColorProviders;
import com.cemi.client.render.ApertureRenderLayers;
import com.cemi.client.render.ApertureRenderers;
import com.cemi.networking.AperturePacketHandler;
import com.cemi.particle.ApertureParticlesClient;
import net.fabricmc.api.ClientModInitializer;

public class ApertureCraftClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ApertureRenderers.registerRenderers();
        ApertureRenderLayers.registerRenderLayers();
        ApertureParticlesClient.registerParticles();
        ApertureFluidsClient.registerFluids();
        ApertureColorProviders.registerColorProviders();
        AperturePacketHandler.registerPacketHandlers();
    }
}
