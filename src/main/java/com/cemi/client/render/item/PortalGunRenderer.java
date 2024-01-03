package com.cemi.client.render.item;

import com.cemi.client.render.models.PortalGunModel;
import com.cemi.item.PortalGun;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class PortalGunRenderer extends GeoItemRenderer<PortalGun> {

    public PortalGunRenderer() {
        super(new PortalGunModel());
    }
}
