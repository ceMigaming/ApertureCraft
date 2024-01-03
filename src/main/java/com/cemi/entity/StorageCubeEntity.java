package com.cemi.entity;

import java.util.Collections;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class StorageCubeEntity extends MobEntity implements GeoEntity, Pickable {

    private PlayerEntity holder = null;
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    protected StorageCubeEntity(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public boolean isCollidable() {
        return true;
    }

    @Override
    protected void fall(double heightDifference, boolean onGround, BlockState state,
            BlockPos landedPosition) {
        if (holder != null) {
            return;
        }
        super.fall(heightDifference, onGround, state, landedPosition);
    }

    @Override
    public void equipStack(EquipmentSlot slot, ItemStack stack) {}

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return Collections.emptyList();
    }

    @Override
    public ItemStack getEquippedStack(EquipmentSlot slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public Arm getMainArm() {
        return Arm.RIGHT;
    }

    @Override
    public void registerControllers(final AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (holder == null || !holder.equals(player)) {
            pickUp(player);
        } else {
            drop();
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void pickUp(PlayerEntity player) {
        holder = player;
        setNoGravity(true);
    }

    @Override
    public void drop() {
        holder = null;
        setNoGravity(false);
    }

    @Override
    public void tick() {
        if (holder != null) {
            handlePickedUp(getWorld(), holder, this);
        }
        super.tick();
    }

    @Override
    public boolean isInvulnerable() {
        return true;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        return false;
    }

}
