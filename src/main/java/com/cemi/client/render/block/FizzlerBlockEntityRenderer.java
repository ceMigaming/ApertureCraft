package com.cemi.client.render.block;

import javax.annotation.Nullable;

import org.joml.Matrix4f;

import com.cemi.block.FizzlerBlock;
import com.cemi.block.entity.FizzlerBlockEntity;
import com.cemi.client.render.model.FizzlerModel;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class FizzlerBlockEntityRenderer extends GeoBlockRenderer<FizzlerBlockEntity> {

    public FizzlerBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        super(new FizzlerModel());
    }

    @Override
    protected void rotateBlock(Direction facing, MatrixStack poseStack) {
        switch (facing) {
            case SOUTH -> poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90));
            case WEST -> poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(0));
            case NORTH -> poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(270));
            case EAST -> poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
            default -> {
            }
        }
    }

    @Override
    public void defaultRender(MatrixStack poseStack, FizzlerBlockEntity animatable, VertexConsumerProvider bufferSource,
            @Nullable RenderLayer renderType, @Nullable VertexConsumer buffer, float yaw, float partialTick,
            int packedLight) {
        renderPlane(animatable, poseStack);
        super.defaultRender(poseStack, animatable, bufferSource, renderType, buffer, yaw, partialTick, packedLight);
    }

    private void renderPlane(FizzlerBlockEntity entity, MatrixStack poseStack) {
        BlockPos pos = entity.getPos();
        BlockState state = entity.getCachedState();
        if (!state.contains(FizzlerBlock.CONNECTED)
                || !state.get(FizzlerBlock.CONNECTED))
            return;

        BlockPos partnerPos = entity.getConnectedPos();
        if (partnerPos == null) {
            partnerPos = findPartner(entity.getWorld(), pos,
                    state.get(FizzlerBlock.FACING));
            if (partnerPos == null)
                return;
            entity.setConnectedPos(partnerPos);
        }

        Direction facing = state.get(FizzlerBlock.FACING);
        Direction connectionAxis = facing.getOpposite();

        boolean isPrimary;
        if (connectionAxis.getAxis() == Direction.Axis.Z) {
            isPrimary = pos.getZ() < partnerPos.getZ();
        } else {
            isPrimary = pos.getX() < partnerPos.getX();
        }
        if (!isPrimary)
            return;

        double length;
        if (connectionAxis.getAxis() == Direction.Axis.Z) {
            length = Math.abs(partnerPos.getZ() - pos.getZ());
        } else {
            length = Math.abs(partnerPos.getX() - pos.getX());
        }
        if (length < 1.0)
            return;

        poseStack.push();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        Matrix4f matrix = poseStack.peek().getPositionMatrix();

        float cx = 0.5f;
        float cz = 0.5f;
        float hw = 0.05f;
        float h = 2.0f;
        float len = (float) length + 1;

        float r = 0.2f, g = 0.4f, b = 1.0f, a = 0.25f;

        BufferBuilder buf = Tessellator.getInstance().getBuffer();
        buf.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        if (connectionAxis.getAxis() == Direction.Axis.Z) {
            v(buf, matrix, cx - hw, 0, len, r, g, b, a);
            v(buf, matrix, cx - hw, h, len, r, g, b, a);
            v(buf, matrix, cx + hw, h, len, r, g, b, a);
            v(buf, matrix, cx + hw, 0, len, r, g, b, a);

            v(buf, matrix, cx - hw, 0, 0, r, g, b, a);
            v(buf, matrix, cx + hw, 0, 0, r, g, b, a);
            v(buf, matrix, cx + hw, h, 0, r, g, b, a);
            v(buf, matrix, cx - hw, h, 0, r, g, b, a);

            v(buf, matrix, cx + hw, 0, 0, r, g, b, a);
            v(buf, matrix, cx + hw, h, 0, r, g, b, a);
            v(buf, matrix, cx + hw, h, len, r, g, b, a);
            v(buf, matrix, cx + hw, 0, len, r, g, b, a);

            v(buf, matrix, cx - hw, 0, 0, r, g, b, a);
            v(buf, matrix, cx - hw, 0, len, r, g, b, a);
            v(buf, matrix, cx - hw, h, len, r, g, b, a);
            v(buf, matrix, cx - hw, h, 0, r, g, b, a);

            v(buf, matrix, cx - hw, h, 0, r, g, b, a);
            v(buf, matrix, cx - hw, h, len, r, g, b, a);
            v(buf, matrix, cx + hw, h, len, r, g, b, a);
            v(buf, matrix, cx + hw, h, 0, r, g, b, a);

            v(buf, matrix, cx - hw, 0, 0, r, g, b, a);
            v(buf, matrix, cx + hw, 0, 0, r, g, b, a);
            v(buf, matrix, cx + hw, 0, len, r, g, b, a);
            v(buf, matrix, cx - hw, 0, len, r, g, b, a);
        } else {
            v(buf, matrix, len, 0, cz - hw, r, g, b, a);
            v(buf, matrix, len, h, cz - hw, r, g, b, a);
            v(buf, matrix, len, h, cz + hw, r, g, b, a);
            v(buf, matrix, len, 0, cz + hw, r, g, b, a);

            v(buf, matrix, 0, 0, cz - hw, r, g, b, a);
            v(buf, matrix, 0, 0, cz + hw, r, g, b, a);
            v(buf, matrix, 0, h, cz + hw, r, g, b, a);
            v(buf, matrix, 0, h, cz - hw, r, g, b, a);

            v(buf, matrix, 0, 0, cz + hw, r, g, b, a);
            v(buf, matrix, 0, h, cz + hw, r, g, b, a);
            v(buf, matrix, len, h, cz + hw, r, g, b, a);
            v(buf, matrix, len, 0, cz + hw, r, g, b, a);

            v(buf, matrix, 0, 0, cz - hw, r, g, b, a);
            v(buf, matrix, 0, h, cz - hw, r, g, b, a);
            v(buf, matrix, len, h, cz - hw, r, g, b, a);
            v(buf, matrix, len, 0, cz - hw, r, g, b, a);

            v(buf, matrix, 0, h, cz - hw, r, g, b, a);
            v(buf, matrix, len, h, cz - hw, r, g, b, a);
            v(buf, matrix, len, h, cz + hw, r, g, b, a);
            v(buf, matrix, 0, h, cz + hw, r, g, b, a);

            v(buf, matrix, 0, 0, cz - hw, r, g, b, a);
            v(buf, matrix, 0, 0, cz + hw, r, g, b, a);
            v(buf, matrix, len, 0, cz + hw, r, g, b, a);
            v(buf, matrix, len, 0, cz - hw, r, g, b, a);
        }

        BufferRenderer.drawWithGlobalProgram(buf.end());

        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();

        poseStack.pop();
    }

    private static BlockPos findPartner(net.minecraft.world.World world, BlockPos pos,
            Direction facing) {
        Direction opposite = facing.getOpposite();
        for (int i = 1; i <= 16; i++) {
            BlockPos checkPos = pos.offset(facing, i);
            BlockState checkState = world.getBlockState(checkPos);
            if (checkState.getBlock() instanceof FizzlerBlock) {
                if (checkState.get(FizzlerBlock.FACING) == opposite) {
                    boolean clear = true;
                    for (int j = 1; j < i; j++) {
                        BlockPos between = pos.offset(facing, j);
                        BlockState bs = world.getBlockState(between);
                        if (!bs.isAir() && !bs.isReplaceable()) {
                            clear = false;
                            break;
                        }
                    }
                    if (clear)
                        return checkPos;
                }
                return null;
            }
            if (!checkState.isAir() && !checkState.isReplaceable())
                return null;
        }
        return null;
    }

    private static void v(BufferBuilder buf, Matrix4f m, float x, float y, float z,
            float r, float g, float b, float a) {
        buf.vertex(m, x, y, z).color(r, g, b, a).next();
    }
}
