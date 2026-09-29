package com.cemi.item;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import com.cemi.block.FizzlerBlock;
import com.cemi.client.render.item.PortalGunRenderer;
import com.cemi.entity.ApertureEntities;
import com.cemi.entity.PortalProjectileEntity;
import com.cemi.sound.ApertureSoundEvent;
import com.cemi.world.ChannelData;
import com.cemi.world.PortalData;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class PortalGunItem extends ApertureItem implements GeoItem {

    public static final int COOLDOWN_TICKS = 4;

    private static final RawAnimation ACTIVATE_ANIM = RawAnimation.begin().thenPlay("use.activate");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    public PortalGunItem() {
        super("portal_gun", new FabricItemSettings().maxCount(1));
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new RenderProvider() {
            private PortalGunRenderer renderer;

            @Override
            public BuiltinModelItemRenderer getCustomRenderer() {
                if (this.renderer == null)
                    this.renderer = new PortalGunRenderer();

                return this.renderer;
            }
        });
    }

    @Override
    public Supplier<Object> getRenderProvider() {
        return this.renderProvider;
    }

    @Override
    public void registerControllers(ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Activation", 0, state -> PlayState.STOP)
                .triggerableAnim("activate", ACTIVATE_ANIM));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemCooldownManager cooldownManager = user.getItemCooldownManager();
        if (cooldownManager.isCoolingDown(this)) {
            return super.use(world, user, hand);
        }
        if (!world.isClient) {
            // False, because the right-hand shot is the blue portal.
            spawnPortalProjectile(world, user, new ChannelData("main", "main"), false);
            playFireSound(world, user, ApertureSoundEvent.PORTAL_FIRE_BLUE_EVENT);
        }

        return super.use(world, user, hand);
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return false;
    }

    @Override
    public void onLeftClick(World world, PlayerEntity user, Hand hand) {
        ItemCooldownManager cooldownManager = user.getItemCooldownManager();
        if (cooldownManager.isCoolingDown(this)) {
            return;
        }
        if (!world.isClient) {
            // True, because the left-hand shot is the yellow/orange portal.
            spawnPortalProjectile(world, user, new ChannelData("main", "main"), true);
            playFireSound(world, user, ApertureSoundEvent.PORTAL_FIRE_YELLOW_EVENT);
        }
        super.onLeftClick(world, user, hand);
    }

    private static void playFireSound(World world, PlayerEntity user, SoundEvent event) {
        world.playSound(null, user.getX(), user.getY(), user.getZ(),
                event, SoundCategory.PLAYERS, 1.0F, 1.0F);
    }

    public void onResetPortals(World world, PlayerEntity user, Hand hand) {
        if (!(world instanceof ServerWorld serverWorld)) {
            return;
        }
        resetStackPortals(serverWorld, user.getStackInHand(hand), user);
    }

    /**
     * Removes every portal placed by the portal guns the player currently carries.
     */
    public static boolean resetPortals(ServerWorld world, PlayerEntity user) {
        boolean hadPortals = false;
        for (ItemStack stack : user.getInventory().main) {
            if (stack.getItem() instanceof PortalGunItem) {
                hadPortals |= resetStackPortals(world, stack, user);
            }
        }
        return hadPortals;
    }

    /**
     * @return whether this stack actually had a portal up, so the caller can decide
     *         if a fizzle is worth hearing.
     */
    private static boolean resetStackPortals(ServerWorld world, ItemStack stack) {
        return resetStackPortals(world, stack, null);
    }

    private static boolean resetStackPortals(ServerWorld world, ItemStack stack,
            @Nullable PlayerEntity listener) {
        PortalData masterData = PortalData.getPortalData(stack, true);
        PortalData slaveData = PortalData.getPortalData(stack, false);

        // Once for the pair, not per portal: the two ends are the same object as far
        // as a listener is concerned, and staggering them reads as two failures.
        boolean hadPortals = !masterData.getUuid().isEmpty() || !slaveData.getUuid().isEmpty();
        killPortal(world, masterData);
        killPortal(world, slaveData);
        if (hadPortals && listener != null) {
            // Heard at the player rather than at the portal, which may be a wall away
            // and, when a field fizzles it, may be out of earshot entirely.
            Vec3d pos = listener.getPos();
            world.playSound(null, pos.x, pos.y, pos.z,
                    ApertureSoundEvent.PORTAL_FIZZLE_EVENT, SoundCategory.PLAYERS, 1.0F, 1.0F);
        }

        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.put("portalDataMaster", masterData.reset().writeToNBT(new NbtCompound()));
        nbt.put("portalDataSlave", slaveData.reset().writeToNBT(new NbtCompound()));

        return hadPortals;
    }

    private static void killPortal(ServerWorld world, PortalData data) {
        if (data.getUuid().isEmpty()) {
            return;
        }
        world.getEntitiesByType(ApertureEntities.APERTURE_PORTAL,
                entity -> entity.getUuid().toString().equals(data.getUuid()))
                .forEach(Entity::kill);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return Integer.MAX_VALUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    private final void spawnPortalProjectile(World world, PlayerEntity user, ChannelData channel,
            boolean isMaster) {
        ItemCooldownManager cooldownManager = user.getItemCooldownManager();
        cooldownManager.set(this, 10);
        // Refused before the projectile exists, because PortalProjectileEntity#tick
        // places a portal in the same tick it spawns, which a field tick could lose
        // the race with. The cooldown is still charged so the shot cannot be spammed.
        if (FizzlerBlock.isInsideField(world, user.getBoundingBox())) {
            return;
        }
        PortalProjectileEntity portalProjectileEntity =
                ApertureEntities.PORTAL_PROJECTILE.create(world);
        portalProjectileEntity.setProperties(user, isMaster);
        world.spawnEntity(portalProjectileEntity);
    }

}
