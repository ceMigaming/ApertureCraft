package com.cemi.item;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import com.cemi.client.render.item.PortalGunRenderer;
import com.cemi.component.ApertureComponents;
import com.cemi.component.PortalComponent;
import com.cemi.entity.ApertureEntities;
import com.cemi.entity.PortalProjectileEntity;
import com.cemi.world.ChannelData;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class PortalGunItem extends ApertureItem implements GeoItem {

    public static final int COOLDOWN_TICKS = 4;

    private static final RawAnimation ACTIVATE_ANIM = RawAnimation.begin().thenPlay("use.activate");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public PortalGunItem() {
        super("portal_gun", new Item.Settings().maxCount(1));
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private PortalGunRenderer renderer;

            @Override
            public @Nullable BuiltinModelItemRenderer getGeoItemRenderer() {
                if (this.renderer == null)
                    this.renderer = new PortalGunRenderer();

                return this.renderer;
            }
        });
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
            spawnPortalProjectile(world, user, new ChannelData("main", "main"), false);
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
            spawnPortalProjectile(world, user, new ChannelData("main", "main"), true);
        }
        super.onLeftClick(world, user, hand);
    }

    public void onResetPortals(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        PortalComponent masterData = user.getStackInHand(hand).get(ApertureComponents.PORTAL_COMPONENT);
        PortalComponent slaveData = masterData.other().get();
        ((ServerWorld) world).getEntitiesByType(ApertureEntities.APERTURE_PORTAL, (entity) -> {
            return entity.getUuid().toString().equals(masterData.uuid());
        }).forEach((entity) -> {
            entity.kill();
            // FIXME use new component system
            // stack.componen getOrCreateNbt().put("portalDataMaster",
            // masterData.reset().writeToNBT(new NbtCompound()));
        });
        ((ServerWorld) world).getEntitiesByType(ApertureEntities.APERTURE_PORTAL, (entity) -> {
            return entity.getUuid().toString().equals(slaveData.uuid());
        }).forEach((entity) -> {
            entity.kill();
            // FIXME use new component system
            // stack.getOrCreateNbt().put("portalDataSlave",
            // slaveData.reset().writeToNBT(new NbtCompound()));
        });

    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
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
        PortalProjectileEntity portalProjectileEntity = ApertureEntities.PORTAL_PROJECTILE.create(world);
        portalProjectileEntity.setProperties(user, isMaster);
        world.spawnEntity(portalProjectileEntity);
    }

}
