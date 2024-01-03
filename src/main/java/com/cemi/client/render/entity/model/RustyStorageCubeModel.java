package com.cemi.client.render.entity.model;

import com.cemi.ApertureCraft;
import com.cemi.entity.StorageCubeEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class RustyStorageCubeModel extends DefaultedEntityGeoModel<StorageCubeEntity> {

    public RustyStorageCubeModel() {
        super(new Identifier(ApertureCraft.MOD_ID, "rusty_storage_cube"));
    }

}
