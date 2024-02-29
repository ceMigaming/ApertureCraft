package com.cemi.client.render.entity;

import org.joml.Matrix4f;
import com.cemi.ApertureCraft;
import com.cemi.client.render.entity.model.PortalOverlayModel;
import com.cemi.entity.AperturePortal;
import com.cemi.util.ShaderHelper;
import com.cemi.util.ShaderHelper.PortalShader;
import com.mojang.blaze3d.platform.GlStateManager.DstFactor;
import com.mojang.blaze3d.platform.GlStateManager.SrcFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import qouteall.imm_ptl.core.portal.Portal;
import qouteall.imm_ptl.core.render.PortalEntityRenderer;

public class CustomPortalEntityRenderer extends PortalEntityRenderer {

    MinecraftClient client = MinecraftClient.getInstance();

    private final PortalOverlayModel model;
    public static final EntityModelLayer OVERLAY_MODEL_LAYER =
            new EntityModelLayer(new Identifier(ApertureCraft.MOD_ID, "portal_overlay"), "main");

    Identifier portalClosed =
            new Identifier(ApertureCraft.MOD_ID, "textures/entity/portalclose.png");
    Identifier portalOpen = new Identifier(ApertureCraft.MOD_ID, "textures/entity/portalopen.png");
    Identifier portalOutline =
            new Identifier(ApertureCraft.MOD_ID, "textures/entity/portaloutline.png");
    Identifier portalOutlineFancy =
            new Identifier(ApertureCraft.MOD_ID, "textures/entity/portaloutlinefancy.png");

    public CustomPortalEntityRenderer(Context context) {
        super(context);
        model = new PortalOverlayModel(context.getPart(OVERLAY_MODEL_LAYER));
    }

    @Override
    public Identifier getTexture(Portal entity) {
        return entity.isVisible() ? portalOpen : portalClosed;
    }

    float time = 0;

    float prevTickDelta = 0;

    @Override
    public void render(Portal entity, float yaw, float tickDelta, MatrixStack matrixStack,
            VertexConsumerProvider bufferSource, int light) {
        super.render(entity, yaw, tickDelta, matrixStack, bufferSource, light);
        // if (!entity.isInFrontOfPortal(CHelper.getCurrentCameraPos())) {
        // return;
        // }
        // RenderSystem.setShader(null);
        // GL20.glUseProgram(ShaderHelper.PORTAL_SHADER);
        matrixStack.push();


        // matrixStack.peek().getNormalMatrix()
        // .rotate(DQuaternion.rotationByDegrees(new Vec3d(1, 0, 0), -90).toMcQuaternion());

        // matrixStack.translate(0.375F, -0.5F, 0.01F);
        AperturePortal portal = (AperturePortal) entity;
        int color = portal.getColor();

        int r = (color & 0xFF0000) >> 16;
        int g = (color & 0xFF00) >> 8;
        int b = color & 0xFF;
        // matrixStack.scale(0.25F, 0.5F, 0.25F);

        // GL20.glUseProgram(ShaderHelper.PORTAL_SHADER);
        // GL20.glUniform1f(GL20.glGetUniformLocation(ShaderHelper.PORTAL_SHADER, "closeAlpha"),
        // 0.5f);
        // GL20.glUniform1f(GL20.glGetUniformLocation(ShaderHelper.PORTAL_SHADER, "theColorR"),
        // r / 255.0f);
        // GL20.glUniform1f(GL20.glGetUniformLocation(ShaderHelper.PORTAL_SHADER, "theColorG"),
        // g / 255.0f);
        // GL20.glUniform1f(GL20.glGetUniformLocation(ShaderHelper.PORTAL_SHADER, "theColorB"),
        // b / 255.0f);
        // GL20.glUniform1f(GL20.glGetUniformLocation(ShaderHelper.PORTAL_SHADER, "time"), 0.5f);
        // GL20.glUniform1f(GL20.glGetUniformLocation(ShaderHelper.PORTAL_SHADER, "pass"), 0.5f);

        // System.out.println(ShaderHelper.getPortalShader());
        Matrix4f matrix = matrixStack.peek().getPositionMatrix();
        matrix.rotate(entity.getOrientationRotation().toMcQuaternion());
        boolean useShader = true;
        int pass = 0;
        if (prevTickDelta < tickDelta) {
            time += (tickDelta - prevTickDelta) * 0.1F;
        }
        prevTickDelta = tickDelta;
        // TODO add switch to disable shader and adjust the overlay so its not hidden behind the portal, also add a second layer of overlay
        if (useShader) {
            PortalShader program = ShaderHelper.getPortalShader();
            RenderSystem.setShader(() -> program);
            TextureManager textureManager = this.dispatcher.textureManager;
            MinecraftClient.getInstance().getTextureManager().bindTexture(portalClosed);
            RenderSystem.enableBlend();

            RenderSystem.blendFuncSeparate(SrcFactor.SRC_ALPHA, DstFactor.ONE_MINUS_SRC_ALPHA,
                    SrcFactor.ZERO, DstFactor.ONE);
            program.theColorR.set(r / 255.0f);
            program.theColorG.set(g / 255.0f);
            program.theColorB.set(b / 255.0f);
            program.closeAlpha.set(0.5f);
            program.time.set(time);
            program.pass.set(pass++);
            program.modelViewMat.set(matrix);
            program.projectionMat.set(RenderSystem.getProjectionMatrix());

            final float sizeProg = MathHelper.clamp((time - 1 + tickDelta) / 5.0f, 0.0f, 1.0f);

            final float scaleX = 1.0F * sizeProg;
            final float scaleY = 2.0F * sizeProg;
            final float scaleZ = 0.95f;
            matrix.translate(0.0F, 0.0F, 0.F);
            matrix.scale(scaleX, scaleY, scaleZ);

        }
        // program.modelViewMat.set(matrixStack.peek().getPositionMatrix());
        // GL20.glBindTexture();



        drawPlane(matrix, 2.45F);


        // RenderSystem.setShader(null);
        // TextureManager textureManager = this.dispatcher.textureManager;
        // int texture = textureManager.getTexture(portalClosed).getGlId();
        // RenderSystem.bindTexture(texture);
        // MinecraftClient.getInstance().getTextureManager().bindTexture(portalClosed);


        // RenderSystem.disableDepthTest();
        // RenderSystem.enableBlend();
        // RenderSystem.depthMask(false);
        // RenderSystem.defaultBlendFunc();
        // RenderSystem.blendFuncSeparate(SrcFactor.SRC_ALPHA, DstFactor.ONE_MINUS_SRC_ALPHA,
        // SrcFactor.ONE, DstFactor.ZERO);
        // RenderSystem.clearColor(1.0F, 1.0F, 1.0F, 1.0F);
        // RenderSystem.disableCull();
        // Matrix4f matrix = matrixStack.peek().getPositionMatrix();
        // matrix.rotate(entity.getOrientationRotation().toMcQuaternion());
        // // bind texture
        // MinecraftClient.getInstance().getTextureManager().bindTexture(portalClosed);
        // final Tessellator tessellator = RenderSystem.renderThreadTesselator();
        // final BufferBuilder bufferbuilder = tessellator.getBuffer();
        // bufferbuilder.begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
        // final float brightness = 1.6f;
        // final float r2 = MathHelper.clamp(r * brightness, 0.0f, 1.0f);
        // final float g2 = MathHelper.clamp(g * brightness, 0.0f, 1.0f);
        // final float b2 = MathHelper.clamp(b * brightness, 0.0f, 1.0f);
        // bufferbuilder.vertex(matrix, -0.5F, -0.5F, 0.0F).color(r2, g2, b2, 1.0f).texture(0.0F,
        // 0.0F)
        // .next();
        // bufferbuilder.vertex(matrix, 0.5F, -0.5F, 0.0F).color(r2, g2, b2, 1.0f).texture(1.0F,
        // 0.0F)
        // .next();
        // bufferbuilder.vertex(matrix, 0.5F, 0.5F, 0.0F).color(r, g, b, 1.0f).texture(1.0F, 1.0F)
        // .next();
        // bufferbuilder.vertex(matrix, -0.5F, 0.5F, 0.0F).color(r, g, b, 1.0f).texture(0.0F, 1.0F)
        // .next();
        // tessellator.draw();
        // RenderSystem.depthMask(true);
        // RenderSystem.enableDepthTest();
        // RenderSystem.enableCull();
        // RenderSystem.clearColor(1.0F, 1.0F, 1.0F, 1.0F);

        // this.model.render(matrixStack,
        // bufferSource.getBuffer(RenderLayer.getEntityTranslucent(this.getTexture(entity))),
        // LightmapTextureManager.pack(15, 15), OverlayTexture.DEFAULT_UV, r / 255.0f,
        // g / 255.0f, b / 255.0f, 1.0f);
        // GL20.glUseProgram(0);
        matrixStack.pop();


        // GL20.glUseProgram(0);
    }

    public void drawPlane(final Matrix4f matrix, final float mult) {
        final Tessellator tessellator = RenderSystem.renderThreadTesselator();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferbuilder.vertex(matrix, -0.5F * mult, -0.5F * mult, 0.0F).texture(0.0F, 0.0F).next();
        bufferbuilder.vertex(matrix, 0.5F * mult, -0.5F * mult, 0.0F).texture(1.0F, 0.0F).next();
        bufferbuilder.vertex(matrix, 0.5F * mult, 0.5F * mult, 0.0F).texture(1.0F, 1.0F).next();
        bufferbuilder.vertex(matrix, -0.5F * mult, 0.5F * mult, 0.0F).texture(0.0F, 1.0F).next();
        tessellator.draw();
    }

}
