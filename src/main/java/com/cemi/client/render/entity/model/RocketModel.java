package com.cemi.client.render.entity.model;

import com.cemi.ApertureCraft;
import com.cemi.entity.RocketEntity;

import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class RocketModel extends DefaultedEntityGeoModel<RocketEntity> {

    public RocketModel() {
        super(new Identifier(ApertureCraft.MOD_ID, "rocket"));
    }
}
