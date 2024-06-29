package com.cemi.client.render.model;

import com.cemi.ApertureCraft;
import com.cemi.block.entity.ApertureDoorBlockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class DoorModel extends DefaultedBlockGeoModel<ApertureDoorBlockEntity> {

    public DoorModel() {
        super(new Identifier(ApertureCraft.MOD_ID, "door"));
    }
    
}
