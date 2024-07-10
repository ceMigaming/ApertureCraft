package com.cemi.client.render.block;

import com.cemi.block.entity.ApertureDoorBlockEntity;
import com.cemi.client.render.model.DoorModel;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class DoorBlockEntityRenderer extends GeoBlockRenderer<ApertureDoorBlockEntity> {

    public DoorBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        super(new DoorModel());
    }

}
