package com.cemi.client.render.entity.model;

import com.cemi.ApertureCraft;
import com.cemi.entity.StorageCubeEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class CompanionCubeModel extends DefaultedEntityGeoModel<StorageCubeEntity> {

    public CompanionCubeModel() {
        super(Identifier.of(ApertureCraft.MOD_ID, "companion_cube"));
    }

}
