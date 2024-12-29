package com.cemi.client.render.item;

import com.cemi.ApertureCraft;
import com.cemi.item.ApertureGeoItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class ApertureGeoItemsRenderer extends GeoItemRenderer<ApertureGeoItem> {
    public ApertureGeoItemsRenderer(String path) {
        super(new DefaultedItemGeoModel<>(Identifier.of(ApertureCraft.MOD_ID, path)));
    }
}
