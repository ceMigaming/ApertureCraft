package com.cemi.client.render.item;

import org.joml.Quaternionf;
import com.cemi.client.render.model.PortalGunModel;
import com.cemi.item.PortalGunItem;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class PortalGunRenderer extends GeoItemRenderer<PortalGunItem> {

    public PortalGunRenderer() {
        super(new PortalGunModel());
    }
}
