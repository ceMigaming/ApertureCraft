package com.cemi.client.render.entity.model;

import com.cemi.ApertureCraft;
import com.cemi.entity.StorageCubeEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class OldStorageCubeModel extends DefaultedEntityGeoModel<StorageCubeEntity> {

    public OldStorageCubeModel() {
        super(new Identifier(ApertureCraft.MOD_ID, "old_storage_cube"));
    }

}
