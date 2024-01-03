package com.cemi.client.render.entity.model;

import com.cemi.ApertureCraft;
import com.cemi.entity.StorageCubeEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class StorageCubeModel extends DefaultedEntityGeoModel<StorageCubeEntity> {

    public StorageCubeModel() {
        super(new Identifier(ApertureCraft.MOD_ID, "storage_cube"));
    }

}
