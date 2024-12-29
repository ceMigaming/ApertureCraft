package com.cemi.client.render.entity.model;

import com.cemi.ApertureCraft;
import com.cemi.entity.FloatingPanelEntity;

import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class FloatingPanelModel extends DefaultedEntityGeoModel<FloatingPanelEntity> {

    public FloatingPanelModel() {
        super(Identifier.of(ApertureCraft.MOD_ID, "panel_floating"));
    }

}
