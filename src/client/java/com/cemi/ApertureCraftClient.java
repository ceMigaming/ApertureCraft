package com.cemi;

import com.cemi.particles.ApertureParticlesClient;
import net.fabricmc.api.ClientModInitializer;

public class ApertureCraftClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ApertureParticlesClient.registerParticles();
	}
}
