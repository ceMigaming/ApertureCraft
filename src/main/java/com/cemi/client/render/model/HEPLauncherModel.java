package com.cemi.client.render.model;

import com.cemi.ApertureCraft;
import com.cemi.block.entity.HEPLauncherBlockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class HEPLauncherModel extends DefaultedBlockGeoModel<HEPLauncherBlockEntity> {

    public HEPLauncherModel() {
        super(Identifier.of(ApertureCraft.MOD_ID, "hep_launcher"));
    }

}
