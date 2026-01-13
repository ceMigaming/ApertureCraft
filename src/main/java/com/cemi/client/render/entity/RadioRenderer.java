package com.cemi.client.render.entity;

import org.joml.Quaternionf;
import com.cemi.client.render.entity.model.RadioModel;
import com.cemi.entity.RadioEntity;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.font.TextRenderer.TextLayerType;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RadioRenderer extends GeoEntityRenderer<RadioEntity> {

    private final TextRenderer textRenderer;


    public RadioRenderer(Context renderManager) {
        super(renderManager, new RadioModel());
        this.textRenderer = renderManager.getTextRenderer();
    }

    @Override
    public void render(RadioEntity entity, float entityYaw, float partialTick,
            MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight) {
        renderText(entity.getLerpedPos(partialTick), entityYaw, poseStack, bufferSource);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    void renderText(Vec3d pos, float entityYaw, MatrixStack matrices,
            VertexConsumerProvider vertexConsumers) {
        matrices.push();
        Quaternionf q =
                new Quaternionf().rotateLocalY((float) (Math.PI - Math.toRadians(entityYaw)));
        matrices.multiply(q);
        matrices.scale(-0.005f, -0.005f, 0.005f);
        matrices.translate(-40, -36.0f, 2.0f);
        textRenderer.draw("85.2 FM", (float) textRenderer.getWidth("85.2 FM") / 2.0f, 0.0f,
                0x27a7d8, false, matrices.peek().getPositionMatrix(), vertexConsumers,
                TextLayerType.NORMAL, 0, 0xF000F0);

        matrices.pop();
    }
}
