package com.cemi.client.render.block;

import com.cemi.block.entity.AperturePanelRailSupportBlockEntity;
import com.cemi.client.render.model.PanelRailSupportModel;

import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class PanelRailSupportEntityRenderer extends GeoBlockRenderer<AperturePanelRailSupportBlockEntity> {
    public PanelRailSupportEntityRenderer(BlockEntityRendererFactory.Context context) {
        super(new PanelRailSupportModel());
    }
}