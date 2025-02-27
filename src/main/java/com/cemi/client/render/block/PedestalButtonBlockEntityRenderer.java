package com.cemi.client.render.block;

import com.cemi.block.entity.PedestalButtonBlockEntity;
import com.cemi.client.render.model.PedestalButtonModel;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class PedestalButtonBlockEntityRenderer extends GeoBlockRenderer<PedestalButtonBlockEntity> {

    public PedestalButtonBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        super(new PedestalButtonModel());
    }

}
