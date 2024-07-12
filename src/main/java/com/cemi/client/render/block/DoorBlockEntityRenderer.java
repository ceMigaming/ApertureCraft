package com.cemi.client.render.block;

import com.cemi.block.ApertureBlocks;
import com.cemi.block.ApertureDoorBlock;
import com.cemi.block.entity.ApertureDoorBlockEntity;
import com.cemi.client.render.model.DoorModel;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class DoorBlockEntityRenderer extends GeoBlockRenderer<ApertureDoorBlockEntity> {

    public DoorBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        super(new DoorModel());
    }

    @Override
    public void render(ApertureDoorBlockEntity animatable, float partialTick, MatrixStack poseStack,
            VertexConsumerProvider bufferSource, int packedLight, int packedOverlay) {
        BlockState state = animatable.getWorld().getBlockState(animatable.getPos());
        if (state.getBlock() == ApertureBlocks.DOOR && state.get(ApertureDoorBlock.SIDE) != 0)
            return;
        super.render(animatable, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
    }
}
