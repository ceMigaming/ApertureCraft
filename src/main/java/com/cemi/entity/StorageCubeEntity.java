package com.cemi.entity;

import java.util.Collections;

import org.jetbrains.annotations.Nullable;

import com.cemi.block.entity.ApertureCubeDropperBlockEntity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class StorageCubeEntity extends MobEntity implements GeoEntity, Pickable, Fizzlable {

    private PlayerEntity holder = null;
    @Nullable
    private BlockPos dropperPos;
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
    public void equipStack(EquipmentSlot slot, ItemStack stack) {
    }

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
    public void registerControllers(final AnimatableManager.ControllerRegistrar controllers) {
    }

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
        fallDistance = 0;
    }

    @Override
    public void drop() {
        holder = null;
        setNoGravity(false);
        fallDistance = 0;
    }

    @Override
    public void tick() {
        if (holder != null) {
            handlePickedUp(getWorld(), holder, this);
        }
        super.tick();
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.contains("dropperPos")) {
            dropperPos = BlockPos.fromLong(nbt.getLong("dropperPos"));
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        if (dropperPos != null) {
            nbt.putLong("dropperPos", dropperPos.asLong());
        }
    }

    /**
     * Master position of the dropper that released this cube. A companion cube
     * fizzled by a field uses it to rematerialise where it came from.
     */
    @Nullable
    public BlockPos getDropperPos() {
        return dropperPos;
    }

    public void setDropperPos(@Nullable BlockPos pos) {
        this.dropperPos = pos;
    }

    /**
     * A companion cube rematerialises at its dropper the moment it is fizzled, so
     * it comes back in the same tick rather than after a delay. Other cubes stay
     * gone, matching the games.
     */
    @Override
    public void onFizzled(ServerWorld world, Box field) {
        if (getType() != ApertureEntities.COMPANION_CUBE || dropperPos == null) {
            return;
        }
        if (!(world.getBlockEntity(dropperPos) instanceof ApertureCubeDropperBlockEntity dropper)) {
            return;
        }
        // Skipped when the dropper's mouth sits inside the field, which would
        // otherwise rematerialise the cube and re-fizzle it every tick forever.
        if (!field.intersects(dropperSpawnBounds(dropper.getPos()))) {
            dropper.spawnEntity();
        }
    }

    /** Where ApertureCubeDropperBlockEntity#spawnEntity places the cube it makes. */
    private static Box dropperSpawnBounds(BlockPos masterPos) {
        return new Box(masterPos.getX() + 0.5, masterPos.getY() + 1.0, masterPos.getZ() + 0.5,
                masterPos.getX() + 1.5, masterPos.getY() + 2.0, masterPos.getZ() + 1.5);
    }

    @Override
    public boolean isInvulnerable() {
        return true;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        return source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY) ? super.damage(source, amount) : false;
    }

}
