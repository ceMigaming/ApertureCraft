package com.cemi.client.render.model;

import com.cemi.ApertureCraft;
import com.cemi.block.entity.FizzlerBlockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class FizzlerModel extends DefaultedBlockGeoModel<FizzlerBlockEntity> {

    public FizzlerModel() {
        super(new Identifier(ApertureCraft.MOD_ID, "fizzler"));
    }
}
