package com.cemi.client.render.model;

import com.cemi.ApertureCraft;
import com.cemi.block.entity.PedestalButtonBlockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class PedestalButtonModel extends DefaultedBlockGeoModel<PedestalButtonBlockEntity> {

    public PedestalButtonModel() {
        super(new Identifier(ApertureCraft.MOD_ID, "pedestal_button"));
    }

    
}
