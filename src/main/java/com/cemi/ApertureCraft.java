package com.cemi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.cemi.block.ApertureBlocks;
import com.cemi.block.entity.ApertureBlockEntities;
import com.cemi.entity.ApertureAttributes;
import com.cemi.entity.ApertureEntities;
import com.cemi.fluid.ApertureFluids;
import com.cemi.item.ApertureItemGroups;
import com.cemi.item.ApertureItems;
import com.cemi.networking.AperturePacketHandler;
import com.cemi.particle.ApertureParticleTypes;
import com.cemi.registry.tag.ApertureFluidTags;
import net.fabricmc.api.ModInitializer;

public class ApertureCraft implements ModInitializer {
    public static final String MOD_ID = "aperturecraft";
    public static final String MOD_NAME = "ApertureCraft";
    public static final String MOD_VERSION = "0.0.1";
    public static final String MOD_DESCRIPTION = "Now you're thinking with portals!";
    
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {

        ApertureParticleTypes.registerParticles();
        ApertureFluids.registerFluids();
        ApertureFluidTags.registerFluidTags();
        ApertureItems.registerItems();
        ApertureBlockEntities.registerBlockEntities();
        ApertureBlocks.registerBlocks();
        ApertureItemGroups.registerItemGroups();
        ApertureEntities.registerEntities();
        ApertureAttributes.registerAttributes();
        AperturePacketHandler.registerPacketHandlers();

        LOGGER.info("Now you're thinking with portals!");
    }
}
