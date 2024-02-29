package com.cemi.client.render.models;

import com.cemi.ApertureCraft;
import com.cemi.item.PortalGunItem;

import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class PortalGunModel extends DefaultedItemGeoModel<PortalGunItem> {

    public PortalGunModel() {
        super(new Identifier(ApertureCraft.MOD_ID, "portal_gun"));
    }

}
