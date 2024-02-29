package com.cemi.item;

import java.util.function.Consumer;
import java.util.function.Supplier;
import com.cemi.client.render.item.PortalGunRenderer;
import com.cemi.entity.ApertureEntities;
import com.cemi.entity.PortalProjectileEntity;
import com.cemi.world.ChannelData;
import com.cemi.world.PortalData;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
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
        PortalData masterData = PortalData.getPortalData(user.getStackInHand(hand), true);
        PortalData slaveData = PortalData.getPortalData(user.getStackInHand(hand), false);
        ((ServerWorld) world).getEntitiesByType(ApertureEntities.APERTURE_PORTAL, (entity) -> {
            return entity.getUuid().toString().equals(masterData.getUuid());
        }).forEach((entity) -> {
            entity.kill();
            stack.getOrCreateNbt().put("portalDataMaster",
                    masterData.reset().writeToNBT(new NbtCompound()));
        });
        ((ServerWorld) world).getEntitiesByType(ApertureEntities.APERTURE_PORTAL, (entity) -> {
            return entity.getUuid().toString().equals(slaveData.getUuid());
        }).forEach((entity) -> {
            entity.kill();
            stack.getOrCreateNbt().put("portalDataSlave",
                    slaveData.reset().writeToNBT(new NbtCompound()));
        });

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
        PortalProjectileEntity portalProjectileEntity =
                ApertureEntities.PORTAL_PROJECTILE.create(world);
        portalProjectileEntity.setProperties(user, isMaster);
        world.spawnEntity(portalProjectileEntity);
    }

}
