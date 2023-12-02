package com.cemi.render.entity.model;

import com.cemi.ApertureCraft;
import com.cemi.entity.RocketTurretEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class RocketTurretModel extends DefaultedEntityGeoModel<RocketTurretEntity> {

    public RocketTurretModel() {
        super(new Identifier(ApertureCraft.MOD_ID, "rocket_turret"));
    }

}
