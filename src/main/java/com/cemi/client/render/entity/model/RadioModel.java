package com.cemi.client.render.entity.model;

import com.cemi.ApertureCraft;
import com.cemi.entity.RadioEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class RadioModel extends DefaultedEntityGeoModel<RadioEntity> {

    public RadioModel() {
        super(new Identifier(ApertureCraft.MOD_ID, "radio"));
    }

}
