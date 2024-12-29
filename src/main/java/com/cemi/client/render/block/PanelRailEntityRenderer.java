package com.cemi.client.render.block;

import com.cemi.block.entity.AperturePanelRailStraightBlockEntity;
import com.cemi.client.render.model.PanelRailStraightModel;

import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class PanelRailEntityRenderer extends GeoBlockRenderer<AperturePanelRailStraightBlockEntity> {
    public PanelRailEntityRenderer(BlockEntityRendererFactory.Context context) {
        super(new PanelRailStraightModel());
    }
}