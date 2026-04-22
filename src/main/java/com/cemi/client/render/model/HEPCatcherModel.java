package com.cemi.client.render.model;

import com.cemi.ApertureCraft;
import com.cemi.block.entity.HEPCatcherBlockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class HEPCatcherModel extends DefaultedBlockGeoModel<HEPCatcherBlockEntity> {

    public HEPCatcherModel() {
        super(new Identifier(ApertureCraft.MOD_ID, "hep_catcher"));
    }

}
