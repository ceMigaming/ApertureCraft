package com.cemi.client.render.entity;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

import com.cemi.ApertureCraft;
import com.cemi.client.render.ApertureRenderLayers;
import com.cemi.client.render.entity.model.PortalOverlayModel;
import com.cemi.entity.AperturePortal;
import com.cemi.util.ShaderHelper;
import com.mojang.blaze3d.platform.GlStateManager.DstFactor;
import com.mojang.blaze3d.platform.GlStateManager.SrcFactor;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import qouteall.imm_ptl.core.portal.Portal;
import qouteall.imm_ptl.core.render.PortalEntityRenderer;

public class CustomPortalEntityRenderer extends PortalEntityRenderer {

    MinecraftClient client = MinecraftClient.getInstance();
    protected boolean useShader;

    private final PortalOverlayModel model;
    public static final EntityModelLayer OVERLAY_MODEL_LAYER = new EntityModelLayer(
            new Identifier(ApertureCraft.MOD_ID, "portal_overlay"), "main");

    Identifier portalClosed = new Identifier(ApertureCraft.MOD_ID, "textures/entity/portalclose.png");
    Identifier portalOpen = new Identifier(ApertureCraft.MOD_ID, "textures/entity/portalopen.png");
    Identifier portalOutline = new Identifier(ApertureCraft.MOD_ID, "textures/entity/portaloutline.png");
    Identifier portalOutlineFancy = new Identifier(ApertureCraft.MOD_ID, "textures/entity/portaloutlinefancy.png");

    public CustomPortalEntityRenderer(Context context) {
        super(context);
        model = new PortalOverlayModel(context.getPart(OVERLAY_MODEL_LAYER));
        useShader = ApertureCraft.getConfig().isEnableShaders();
    }

    @Override
    public Identifier getTexture(Portal entity) {
        return entity.isVisible() ? portalOpen : portalClosed;
    }

    @Override
    public void render(Portal entity, float yaw, float tickDelta, MatrixStack matrixStack,
            VertexConsumerProvider bufferSource, int light) {
        super.render(entity, yaw, tickDelta, matrixStack, bufferSource, light);
    }

}
