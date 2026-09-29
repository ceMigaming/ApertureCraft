package com.cemi.client.render.block;

import javax.annotation.Nullable;

import org.joml.Matrix4f;

import com.cemi.ApertureCraft;
import com.cemi.block.FizzlerBlock;
import com.cemi.block.entity.FizzlerBlockEntity;
import com.cemi.client.render.ApertureRenderLayers;
import com.cemi.client.render.model.FizzlerModel;
import com.cemi.util.ShaderHelper;

import net.minecraft.block.BlockState;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class FizzlerBlockEntityRenderer extends GeoBlockRenderer<FizzlerBlockEntity> {

    /**
     * Seconds of wall clock rather than accumulated frame time: the field must not
     * freeze mid-animation when the player looks away from it, since nothing is
     * drawn while it is off screen to advance a tick-by-tick counter.
     */
    private static final long START_NANOS = System.nanoTime();

    private static final VertexConsumerProvider.Immediate FIZZLER_CONSUMERS =
            VertexConsumerProvider.immediate(new BufferBuilder(256));

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
            partnerPos = FizzlerBlock.findPartner(entity.getWorld(), pos,
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

        ShaderProgram shader = ShaderHelper.getFizzlerShader();
        if (shader == null)
            return;

        boolean alongZ = connectionAxis.getAxis() == Direction.Axis.Z;

        float len = (float) length + 1;
        float h = (float) FizzlerBlock.FIELD_HEIGHT;
        float hw = 0.05f;
        float cx = 0.5f;
        float cz = 0.5f;

        // White with alpha alone. The shader carries its own blue palette, so
        // tinting the vertex colour here would only colour the noise in.
        float a = 0.25f;

        RenderLayer layer = ApertureRenderLayers.getFizzlerLayer();
        VertexConsumer vc = FIZZLER_CONSUMERS.getBuffer(layer);
        Matrix4f matrix = poseStack.peek().getPositionMatrix();

        // A single quad, culled off, so the field reads from both sides. u runs
        // along the field so the shader's border glow lands on the two emitters.
        if (alongZ) {
            vert(vc, matrix, cx - hw, 0, 0, 0, 0, a);
            vert(vc, matrix, cx + hw, 0, len, 1, 0, a);
            vert(vc, matrix, cx + hw, h, len, 1, 1, a);
            vert(vc, matrix, cx - hw, h, 0, 0, 1, a);
        } else {
            vert(vc, matrix, 0, 0, cz - hw, 0, 0, a);
            vert(vc, matrix, len, 0, cz + hw, 1, 0, a);
            vert(vc, matrix, len, h, cz + hw, 1, 1, a);
            vert(vc, matrix, 0, h, cz - hw, 0, 1, a);
        }

        // Set before the flush. RenderLayer binds the program during draw(), and
        // ShaderProgram#bind uploads every dirty uniform, so values set now still
        // reach the GPU. getUniform returns null for a name the fsh does not use,
        // rather than a no-op uniform, so that case is reported rather than thrown.
        setUniform(shader, "time", (float) ((System.nanoTime() - START_NANOS) / 1_000_000_000.0));
        setUniform(shader, "FieldLength", len);
        setUniform(shader, "FieldHeight", h);

        FIZZLER_CONSUMERS.draw();
    }

    private static void setUniform(ShaderProgram shader, String name, float value) {
        GlUniform uniform = shader.getUniform(name);
        if (uniform == null) {
            ApertureCraft.LOGGER.warn("Fizzler shader is missing uniform {}", name);
            return;
        }
        uniform.set(value);
    }

    private static void vert(VertexConsumer vc, Matrix4f m, float x, float y, float z,
            float u, float v, float a) {
        vc.vertex(m, x, y, z).color(1.0f, 1.0f, 1.0f, a).texture(u, v).next();
    }
}
