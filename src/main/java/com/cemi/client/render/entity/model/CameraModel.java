package com.cemi.client.render.entity.model;

import com.cemi.ApertureCraft;
import com.cemi.entity.CameraEntity;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class CameraModel extends DefaultedEntityGeoModel<CameraEntity> {

    public CameraModel() {
        super(new Identifier(ApertureCraft.MOD_ID, "camera"));
    }

    @Override
    public void setCustomAnimations(CameraEntity entity, long instanceId, AnimationState<CameraEntity> state) {
        super.setCustomAnimations(entity, instanceId, state);

        CoreGeoBone rk = this.getAnimationProcessor().getBone("rk");

        if (rk == null)
            return;

        float partialTick = state.getPartialTick();
        float yaw = MathHelper.lerp(partialTick, entity.prevAimYaw, entity.aimYaw);
        float pitch = MathHelper.lerp(partialTick, entity.prevAimPitch, entity.aimPitch);

        // GeckoLib bakes the geo.json rotation into the bone's initial snapshot and
        // setRotY replaces it outright rather than adding to it, so the baked rest
        // rotation (180 degrees on Y, stored as -PI) has to be re-added here.
        rk.setRotY(rk.getInitialSnapshot().getRotY() + yaw);
        rk.setRotX(pitch);
    }
}
