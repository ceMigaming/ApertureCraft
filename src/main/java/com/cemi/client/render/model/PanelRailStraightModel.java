package com.cemi.client.render.model;

import com.cemi.ApertureCraft;
import com.cemi.block.entity.AperturePanelRailStraightBlockEntity;

import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class PanelRailStraightModel extends DefaultedBlockGeoModel<AperturePanelRailStraightBlockEntity> {

    public PanelRailStraightModel() {
        super(Identifier.of(ApertureCraft.MOD_ID, "panel_rail_straight"));
    }

}
