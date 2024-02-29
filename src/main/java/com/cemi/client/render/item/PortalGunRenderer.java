package com.cemi.client.render.item;

import com.cemi.client.render.models.PortalGunModel;
import com.cemi.item.PortalGunItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class PortalGunRenderer extends GeoItemRenderer<PortalGunItem> {

    public PortalGunRenderer() {
        super(new PortalGunModel());
    }
}
