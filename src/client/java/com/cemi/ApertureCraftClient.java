package com.cemi;

import com.cemi.fluids.ApertureFluidsClient;
import com.cemi.particle.ApertureParticlesClient;
import com.cemi.render.ApertureRenderers;
import net.fabricmc.api.ClientModInitializer;

public class ApertureCraftClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ApertureParticlesClient.registerParticles();
		ApertureFluidsClient.registerFluids();
		ApertureRenderers.registerRenderers();
	}
}
