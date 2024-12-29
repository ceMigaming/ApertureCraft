package com.cemi.client.render.model;

import com.cemi.ApertureCraft;
import com.cemi.block.entity.AperturePanelRailCornerBlockEntity;

import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class PanelRailCornerModel extends DefaultedBlockGeoModel<AperturePanelRailCornerBlockEntity> {

    public PanelRailCornerModel() {
        super(Identifier.of(ApertureCraft.MOD_ID, "panel_rail_corner"));
    }

}
