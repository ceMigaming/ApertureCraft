package com.cemi.render.entity.model;

import com.cemi.ApertureCraft;
import com.cemi.entity.TurretEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class TurretModel extends DefaultedEntityGeoModel<TurretEntity> {

    public TurretModel() {
        super(new Identifier(ApertureCraft.MOD_ID, "turret"));
    }

}
