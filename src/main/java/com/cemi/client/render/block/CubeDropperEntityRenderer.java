package com.cemi.client.render.block;

import javax.annotation.Nullable;

import com.cemi.block.ApertureBlocks;
import com.cemi.block.ApertureCubeDropperBlock;
import com.cemi.block.entity.ApertureCubeDropperBlockEntity;
import com.cemi.client.render.model.CubeDropperModel;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class CubeDropperEntityRenderer extends GeoBlockRenderer<ApertureCubeDropperBlockEntity> {

    public CubeDropperEntityRenderer(BlockEntityRendererFactory.Context context) {
        super(new CubeDropperModel());
    }

    @Override
    public void defaultRender(MatrixStack poseStack, ApertureCubeDropperBlockEntity animatable,
            VertexConsumerProvider bufferSource, @Nullable RenderLayer renderType, @Nullable VertexConsumer buffer,
            float yaw, float partialTick, int packedLight) {
                BlockState state = animatable.getWorld().getBlockState(animatable.getPos());
                if (state.getBlock() == ApertureBlocks.CUBE_DROPPER && state.get(ApertureCubeDropperBlock.IS_SLAVE)) {
                    return;
                }
                poseStack.push();
                poseStack.translate(0, 0.5, 0);
        super.defaultRender(poseStack, animatable, bufferSource, renderType, buffer, yaw, partialTick, packedLight);
        poseStack.pop();
    }
}
