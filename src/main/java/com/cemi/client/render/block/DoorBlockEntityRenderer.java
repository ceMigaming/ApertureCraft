package com.cemi.client.render.block;

import javax.annotation.Nullable;
import com.cemi.block.ApertureBlocks;
import com.cemi.block.ApertureDoorBlock;
import com.cemi.block.entity.ApertureDoorBlockEntity;
import com.cemi.client.render.model.DoorModel;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class DoorBlockEntityRenderer extends GeoBlockRenderer<ApertureDoorBlockEntity> {

    public DoorBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        super(new DoorModel());
    }

    @Override
    public void defaultRender(MatrixStack poseStack, ApertureDoorBlockEntity animatable,
            VertexConsumerProvider bufferSource, @Nullable RenderLayer renderType,
            @Nullable VertexConsumer buffer, float yaw, float partialTick, int packedLight) {
        BlockState state = animatable.getWorld().getBlockState(animatable.getPos());
        if (state.getBlock() == ApertureBlocks.DOOR && state.get(ApertureDoorBlock.SIDE) != 0)
            return;

        super.defaultRender(poseStack, animatable, bufferSource, renderType, buffer, yaw,
                partialTick, packedLight);
    }
}
