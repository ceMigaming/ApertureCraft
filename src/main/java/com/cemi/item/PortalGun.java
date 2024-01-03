package com.cemi.item;

import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.apache.commons.lang3.Validate;
import com.cemi.client.render.item.PortalGunRenderer;
import com.cemi.misc.ApertureUtils;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.command.ControlFlowAware.Helper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
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

public class PortalGun extends ApertureItem implements GeoItem {

    public static final int COOLDOWN_TICKS = 4;

    private static final RawAnimation ACTIVATE_ANIM = RawAnimation.begin().thenPlay("use.activate");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    public PortalGun() {
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
        if (world instanceof ServerWorld serverWorld)
            triggerAnim(user, GeoItem.getOrAssignId(user.getStackInHand(hand), serverWorld),
                    "Activation", "activate");

        return super.use(world, user, hand);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    // // return whether successful
    // public boolean interact(ServerPlayerEntity player,
    //         Hand hand/*
    //                   * , PortalGunRecord.PortalGunSide side
    //                   */) {
    //     ItemStack itemStack = player.getStackInHand(hand);
    //     player.getItemCooldownManager().set(this, COOLDOWN_TICKS);

    //     ApertureUtils.PortalAwareRaytraceResult raytraceResult =
    //             ApertureUtils.portalAwareRayTrace(player, 100);

    //     if (raytraceResult == null) {
    //         return false;
    //     }

    //     BlockHitResult blockHit = raytraceResult.hitResult();
    //     ServerWorld world = ((ServerWorld) raytraceResult.world());
    //     Direction wallFacing = blockHit.getSide();

    //     // if (!checkAction(player, world)) {
    //     //     return false;
    //     // }

    //     Validate.isTrue(blockHit.getType() == HitResult.Type.BLOCK);

    //     player.World().playSound(null, player.getX(), player.getY(), player.getZ(),
    //             side == PortalGunRecord.PortalGunSide.blue ? PortalGunMod.PORTAL1_SHOOT_EVENT
    //                     : PortalGunMod.PORTAL2_SHOOT_EVENT,
    //             SoundSource.PLAYERS, 1.0F, 1.0F);

    //     PortalGunRecord.PortalGunKind kind = PortalGunRecord.PortalGunKind._2x1;

    //     PortalGunRecord.PortalDescriptor descriptor =
    //             new PortalGunRecord.PortalDescriptor(player.getUUID(), kind, side);

    //     ItemInfo itemInfo = ItemInfo.fromTag(itemStack.getOrCreateTag());

    //     if (itemInfo.limitsEnergy()) {
    //         if (itemInfo.remainingEnergy <= 0) {
    //             player.displayClientMessage(Text.translatable("portal_gun.out_of_energy"), true);
    //             return false;
    //         }
    //     }

    //     BiPredicate<World, BlockPos> wallPredicate = itemInfo.allowedBlocks.getWallPredicate();

    //     PortalPlacement placement =
    //             findPortalPlacement(player, kind, raytraceResult, descriptor, wallPredicate);

    //     if (placement == null) {
    //         return false;
    //     }

    //     if (itemInfo.limitsEnergy()) {
    //         itemInfo.remainingEnergy--;
    //         itemStack.setTag(itemInfo.toTag());
    //     }

    //     Direction rightDir = placement.rotation.transformedX;
    //     Direction upDir = placement.rotation.transformedY;

    //     triggerAnim(player,
    //             GeoItem.getOrAssignId(player.getItemInHand(hand), ((ServerWorld) player.World())),
    //             "portalGunController", "shoot_anim");

    //     PortalGunRecord record = PortalGunRecord.get();

    //     PortalGunRecord.PortalDescriptor otherSideDescriptor = descriptor.getTheOtherSide();

    //     PortalGunRecord.PortalInfo thisSideInfo = record.data.get(descriptor);
    //     PortalGunRecord.PortalInfo otherSideInfo = record.data.get(otherSideDescriptor);

    //     Vec3 wallFacingVec = Vec3.atLowerCornerOf(wallFacing.getNormal());
    //     Vec3 newPortalOrigin =
    //             Helper.getBoxSurface(placement.areaBox.toRealNumberBox(), wallFacing.getOpposite())
    //                     .getCenter().add(wallFacingVec.scale(PortalGunMod.portalOffset));

    //     CustomPortal portal = null;
    //     boolean isExistingPortal = false;

    //     if (thisSideInfo != null) {
    //         Entity entity = world.getEntity(thisSideInfo.portalId());
    //         if (entity instanceof CustomPortal customPortal) {
    //             portal = customPortal;
    //             isExistingPortal = true;
    //         }
    //     }

    //     if (portal == null) {
    //         portal = CustomPortal.entityType.create(world);
    //         Validate.notNull(portal);
    //     }

    //     portal.setOriginPos(newPortalOrigin);
    //     portal.setOrientationAndSize(Vec3.atLowerCornerOf(rightDir.getNormal()),
    //             Vec3.atLowerCornerOf(upDir.getNormal()), kind.getWidth() * SIZE_MULTIPLIER,
    //             kind.getHeight() * SIZE_MULTIPLIER);
    //     portal.descriptor = descriptor;
    //     portal.wallBox = placement.wallBox;
    //     portal.airBox = placement.areaBox;
    //     portal.setAllowedBlocks(itemInfo.allowedBlocks);
    //     portal.thisSideUpdateCounter = thisSideInfo == null ? 0 : thisSideInfo.updateCounter();
    //     portal.otherSideUpdateCounter = otherSideInfo == null ? 0 : otherSideInfo.updateCounter();
    //     PortalManipulation.makePortalRound(portal, 20);
    //     portal.disableDefaultAnimation();

    //     if (otherSideInfo == null) {
    //         // it's unpaired, invisible and not teleportable
    //         portal.setDestinationDimension(world.dimension());
    //         portal.setDestination(newPortalOrigin.add(0, 10, 0));
    //         portal.setIsVisible(false);
    //         portal.teleportable = false;
    //     } else {
    //         // it's linked
    //         portal.setDestinationDimension(otherSideInfo.portalDim());
    //         portal.setDestination(otherSideInfo.portalPos());
    //         portal.setOtherSideOrientation(otherSideInfo.portalOrientation());
    //         portal.setIsVisible(true);
    //         portal.teleportable = true;
    //         player.World().playSound(null, player.getX(), player.getY(), player.getZ(),
    //                 PortalGunMod.PORTAL_OPEN_EVENT, SoundSource.PLAYERS, 1.0F, 1.0F);
    //     }

    //     portal.thisSideUpdateCounter += 1;
    //     thisSideInfo = new PortalGunRecord.PortalInfo(portal.getUUID(), world.dimension(),
    //             newPortalOrigin, portal.getOrientationRotation(), portal.thisSideUpdateCounter);
    //     record.data.put(descriptor, thisSideInfo);
    //     record.setDirty();

    //     if (!isExistingPortal) {
    //         McHelper.spawnServerEntity(portal);
    //     } else {
    //         portal.reloadAndSyncToClient();
    //     }

    //     return true;
    // }


}
