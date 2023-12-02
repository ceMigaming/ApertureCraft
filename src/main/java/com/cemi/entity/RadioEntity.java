package com.cemi.entity;

import java.util.Collections;
import com.cemi.networking.ApertureNetworkingConstants;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class RadioEntity extends MobEntity implements GeoEntity, Pickable {

    private PlayerEntity holder = null;
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    private boolean isPlaying = false;

    protected RadioEntity(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
        // TODO Auto-generated constructor stub
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
    public void registerControllers(ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    @Override
    public boolean isInvulnerable() {
        return true;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        return false;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    @Override
    public boolean saveNbt(NbtCompound nbt) {
        nbt.putBoolean("isPlaying", isPlaying);
        return super.saveNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        isPlaying = nbt.getBoolean("isPlaying");
        super.readNbt(nbt);
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (player.isSneaking()) {
            if (holder == null || !holder.equals(player)) {
                pickUp(player);
            } else {
                drop();
            }
            return ActionResult.SUCCESS;
        }
        if (getWorld().isClient())
            return ActionResult.SUCCESS;
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(this.getId());
        if (isPlaying) {
            isPlaying = false;
        } else {
            isPlaying = true;
            getWorld().getPlayers().forEach(p -> {
                ServerPlayNetworking.send((ServerPlayerEntity) p,
                        ApertureNetworkingConstants.RADIO_PLAY_ID, buf);
            });
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
}
