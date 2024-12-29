package com.cemi.client.render.block;

import com.cemi.block.entity.AperturePanelRailCornerBlockEntity;
import com.cemi.client.render.model.PanelRailCornerModel;

import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class PanelRailCornerEntityRenderer extends GeoBlockRenderer<AperturePanelRailCornerBlockEntity> {
    public PanelRailCornerEntityRenderer(BlockEntityRendererFactory.Context context) {
        super(new PanelRailCornerModel());
    }
}