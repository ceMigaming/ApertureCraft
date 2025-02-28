package com.cemi.client.render.model;

import com.cemi.ApertureCraft;
import com.cemi.block.entity.ApertureCubeDropperBlockEntity;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class CubeDropperModel extends DefaultedBlockGeoModel<ApertureCubeDropperBlockEntity> {

    public CubeDropperModel() {
        super(new Identifier(ApertureCraft.MOD_ID, "cube_dropper"));
    }

    @Override
    public RenderLayer getRenderType(ApertureCubeDropperBlockEntity animatable, Identifier texture) {
        return RenderLayer.getEntityTranslucent(texture);
    }
}
