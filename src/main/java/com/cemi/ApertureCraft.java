package com.cemi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.cemi.block.ApertureBlocks;
import com.cemi.block.entity.ApertureBlockEntities;
import com.cemi.fluid.ApertureFluids;
import com.cemi.item.ApertureItemGroups;
import com.cemi.item.ApertureItems;
import com.cemi.particle.ApertureParticleTypes;
import net.fabricmc.api.ModInitializer;

public class ApertureCraft implements ModInitializer {
	public static final String MOD_ID = "aperturecraft";
	public static final String MOD_NAME = "ApertureCraft";
	public static final String MOD_VERSION = "0.0.1";
	public static final String MOD_DESCRIPTION = "Now you're thinking with portals!";
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		ApertureParticleTypes.registerParticles();
		ApertureFluids.registerFluids();
		ApertureItems.registerItems();
		ApertureBlockEntities.registerBlockEntities();
		ApertureBlocks.registerBlocks();
		ApertureItemGroups.registerItemGroups();

		LOGGER.info("Now you're thinking with portals!");
	}
}
