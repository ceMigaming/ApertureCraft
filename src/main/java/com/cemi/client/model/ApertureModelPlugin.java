package com.cemi.client.model;

import com.cemi.ApertureCraft;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;

public class ApertureModelPlugin implements ModelLoadingPlugin {
    private static final Identifier CONCRETE_MEDIUM_SLOPE = new Identifier(ApertureCraft.MOD_ID,
            "block/concrete_tile_medium");
    private static final Identifier METAL_MEDIUM_SLOPE = new Identifier(ApertureCraft.MOD_ID,
            "block/metal_tile_medium");

    @Override
    public void onInitializeModelLoader(Context pluginContext) {
        pluginContext.modifyModelOnLoad().register((original, context) -> {
            // This is called for every model that is loaded, so make sure we only target
            // ours
            if (context.id().getNamespace().equals(ApertureCraft.MOD_ID)
                    && context.id().getPath().equals("metal_slope")) {
                System.out.println("TEST!!!" + context.id());
            }
            if (context.id() instanceof ModelIdentifier id) {
                if (id != null && id.getNamespace().equals(ApertureCraft.MOD_ID)
                        && id.getPath().equals("concrete_slope")) {
                    return new SlopeModel(CONCRETE_MEDIUM_SLOPE);
                }
                if (id != null && id.getNamespace().equals(ApertureCraft.MOD_ID)
                        && id.getPath().equals("metal_slope")) {
                    return new SlopeModel(METAL_MEDIUM_SLOPE);
                }
            }
            return original;
        });
    }
}