package com.cemi.client.render.entity.model;

import com.cemi.ApertureCraft;
import com.cemi.entity.GladosMainframeEntity;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class GladosMainframeModel extends DefaultedEntityGeoModel<GladosMainframeEntity> {

    public GladosMainframeModel() {
        super(new Identifier(ApertureCraft.MOD_ID, "glados_mainframe"));
    }

    @Override
    public void setCustomAnimations(GladosMainframeEntity entity, long instanceId,
            AnimationState<GladosMainframeEntity> state) {
        super.setCustomAnimations(entity, instanceId, state);

        CoreGeoBone mf = this.getAnimationProcessor().getBone("mf");
        CoreGeoBone head = this.getAnimationProcessor().getBone("head");

        if (mf == null || head == null)
            return;

        float partialTick = state.getPartialTick();
        float yaw = MathHelper.lerp(partialTick, entity.prevAimYaw, entity.aimYaw);
        float pitch = MathHelper.lerp(partialTick, entity.prevAimPitch, entity.aimPitch);

        // GeckoLib bakes the geo.json rotation into the bone's initial snapshot and
        // setRotY replaces it outright rather than adding to it, so the baked rest
        // rotation (180 degrees on Y, stored as -PI) has to be re-added here.
        mf.setRotY(mf.getInitialSnapshot().getRotY() + yaw);
        head.setRotX(pitch);
    }
}
