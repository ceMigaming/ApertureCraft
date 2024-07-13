package com.cemi.client.render.model;

import com.cemi.ApertureCraft;
import com.cemi.block.entity.ApertureFloorButtonBlockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class FloorButtonModel extends DefaultedBlockGeoModel<ApertureFloorButtonBlockEntity> {

    public FloorButtonModel() {
        super(new Identifier(ApertureCraft.MOD_ID, "floor_button"));
    }

}
