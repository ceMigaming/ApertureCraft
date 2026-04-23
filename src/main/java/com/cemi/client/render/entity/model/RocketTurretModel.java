package com.cemi.client.render.entity.model;

import com.cemi.ApertureCraft;
import com.cemi.entity.RocketTurretEntity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class RocketTurretModel extends DefaultedEntityGeoModel<RocketTurretEntity> {

    public RocketTurretModel() {
        super(new Identifier(ApertureCraft.MOD_ID, "rocket_turret"));
    }

    @Override
    public void setCustomAnimations(RocketTurretEntity entity, long instanceId,
            AnimationState<RocketTurretEntity> state) {
        super.setCustomAnimations(entity, instanceId, state);

        CoreGeoBone raczka = this.getAnimationProcessor().getBone("raczka");

        if (raczka == null)
            return;

        // Apply rotations
        raczka.setRotY(entity.yaw);
        raczka.setRotX(entity.pitch);
    }

}
