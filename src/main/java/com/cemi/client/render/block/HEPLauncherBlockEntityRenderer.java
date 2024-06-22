package com.cemi.client.render.block;

import com.cemi.block.entity.HEPLauncherBlockEntity;
import com.cemi.client.render.model.HEPLauncherModel;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class HEPLauncherBlockEntityRenderer extends GeoBlockRenderer<HEPLauncherBlockEntity> {

    public HEPLauncherBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        super(new HEPLauncherModel());
    }

}
