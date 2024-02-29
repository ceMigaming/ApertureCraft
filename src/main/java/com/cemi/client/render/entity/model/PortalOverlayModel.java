package com.cemi.client.render.entity.model;

import com.cemi.entity.AperturePortal;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class PortalOverlayModel extends EntityModel<AperturePortal> {

    private final ModelPart base;
    private final ModelPart cube_r1;

    public PortalOverlayModel(ModelPart root) {
        this.base = root.getChild("base");
        this.cube_r1 = base.getChild("cube_r1");
    }

    private static ModelData getModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        ModelPartData base = modelPartData.addChild("base", ModelPartBuilder.create(),
                ModelTransform.of(0.0F, 16, 0.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData cube_r1 = base.addChild("cube_r1", ModelPartBuilder.create().uv(0, 0)
                .cuboid(-8.0F, -16.0F, 0.0F, 64.0F, 64.0F, 0.0F, Dilation.NONE).mirrored(true),
                ModelTransform.of(0.0F, -16.0F, 0.0F, 0.0F, (float) Math.PI, 0.0F));
        return modelData;
    }

    public static TexturedModelData getTexturedModelData() {
        return TexturedModelData.of(getModelData(), 64, 64);
    }

    @Override
    public void setAngles(AperturePortal entity, float limbAngle, float limbDistance,
            float animationProgress, float headYaw, float headPitch) {

    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay,
            float red, float green, float blue, float alpha) {
        //base.render(matrices, vertices, light, overlay);
        base.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }

}
