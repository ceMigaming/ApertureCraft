package com.cemi.client.render.item;

import com.cemi.client.render.model.PortalGunModel;
import com.cemi.item.PortalGunItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class PortalGunRenderer extends GeoItemRenderer<PortalGunItem> {

    public PortalGunRenderer() {
        super(new PortalGunModel());
    }
}
