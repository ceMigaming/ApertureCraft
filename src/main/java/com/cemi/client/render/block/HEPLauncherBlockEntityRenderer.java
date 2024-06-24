package com.cemi.client.render.block;

import com.cemi.block.entity.HEPLauncherBlockEntity;
import com.cemi.client.render.model.HEPLauncherModel;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class HEPLauncherBlockEntityRenderer extends GeoBlockRenderer<HEPLauncherBlockEntity> {

    public HEPLauncherBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        super(new HEPLauncherModel());
    }

    @Override
    protected void rotateBlock(Direction facing, MatrixStack poseStack) {
        switch (facing) {
            case SOUTH -> poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
            case WEST -> poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90));
            case NORTH -> poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(0));
            case EAST -> poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(270));
            case UP -> {
                poseStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
                poseStack.translate(0, -0.5, -0.5);
            }
            case DOWN -> {
                poseStack.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(90));
                poseStack.translate(0, -0.5, 0.5);
            }
        }
    }

}
