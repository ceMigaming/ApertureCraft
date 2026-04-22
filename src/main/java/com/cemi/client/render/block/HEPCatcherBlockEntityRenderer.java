package com.cemi.client.render.block;

import com.cemi.block.entity.HEPCatcherBlockEntity;
import com.cemi.client.render.model.HEPCatcherModel;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class HEPCatcherBlockEntityRenderer extends GeoBlockRenderer<HEPCatcherBlockEntity> {

    public HEPCatcherBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        super(new HEPCatcherModel());
    }

    @Override
    protected void rotateBlock(Direction facing, MatrixStack poseStack) {
        switch (facing) {
            case SOUTH -> poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90));
            case WEST -> poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(0));
            case NORTH -> poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(270));
            case EAST -> poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
            case UP -> {
                poseStack.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(90));
                poseStack.translate(-0.5, -0.5, 0);
            }
            case DOWN -> {
                poseStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90));
                poseStack.translate(0.5, -0.5, 0);
            }
        }
    }

}
