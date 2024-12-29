package com.cemi.client.render.model;

import com.cemi.ApertureCraft;
import com.cemi.block.entity.AperturePanelRailCornerBlockEntity;
import com.cemi.block.entity.AperturePanelRailSupportBlockEntity;

import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class PanelRailSupportModel extends DefaultedBlockGeoModel<AperturePanelRailSupportBlockEntity> {

    public PanelRailSupportModel() {
        super(Identifier.of(ApertureCraft.MOD_ID, "panel_rail_support"));
    }

}
