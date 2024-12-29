package com.cemi.entity;

import java.util.List;
import java.util.Optional;

import com.cemi.component.ApertureComponents;
import com.cemi.component.PortalComponent;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.data.DataTracker.Builder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import qouteall.imm_ptl.core.portal.PortalManipulation;
import qouteall.imm_ptl.core.portal.PortalUtils;

public class PortalProjectileEntity extends ProjectileEntity {

    private static final TrackedData<Integer> COLOR;
    private static final TrackedData<Integer> SHOOTER;
    private static final TrackedData<Integer> DISTANCE;
    private static final TrackedData<BlockPos> SPAWN_POS;
    private static final TrackedData<EulerAngle> VELOCITY;
    private static final TrackedData<Boolean> IS_MASTER;
    public int age = 0;
    public int portalWidth = 1;
    public int portalHeight = 2;
    public PortalComponent portalData = new PortalComponent();
    public LivingEntity shooter;

    public PortalProjectileEntity(EntityType<? extends PortalProjectileEntity> entityType,
            World world) {
        super(entityType, world);
        this.noClip = true;
    }

    @Override
    protected void initDataTracker(Builder builder) {
        builder.add(COLOR, 0xFFFFFF);
        builder.add(SHOOTER, -1);
        builder.add(DISTANCE, 10000);
        builder.add(SPAWN_POS, BlockPos.ORIGIN);
        builder.add(VELOCITY, new EulerAngle(0.0F, 0.0F, 0.0F));
        builder.add(IS_MASTER, false);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        this.setColor(nbt.getInt("color"));
        this.portalWidth = nbt.getInt("portalWidth");
        this.portalHeight = nbt.getInt("portalHeight");
        this.portalData = PortalComponent.fromNBT(nbt.getCompound("portalInfo"));
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putInt("color", this.getColor());
        nbt.putInt("portalWidth", this.portalWidth);
        nbt.putInt("portalHeight", this.portalHeight);
        nbt.put("portalInfo", PortalComponent.toNBT(this.portalData));
    }

    public void setColor(final int i) {
        this.getDataTracker().set(PortalProjectileEntity.COLOR, i);
    }

    public int getColor() {
        return this.getDataTracker().get(PortalProjectileEntity.COLOR);
    }

    public void tick() {
        super.tick();
        if (getWorld().isClient()) {
            return;
        }
        if (this.age++ > this.dataTracker.get(DISTANCE)) {
            this.kill();
        }
        EulerAngle angle = this.dataTracker.get(VELOCITY);
        this.setRotation(angle.getYaw(), angle.getPitch());
        Vec3d velocity = Vec3d.fromPolar(angle.getPitch(), angle.getYaw()).normalize();
        Vec3d newPos = new Vec3d(getX(), getY(), getZ()).add(velocity);
        PortalUtils.PortalAwareRaytraceResult portalResult = PortalUtils.portalAwareRayTrace(this, 1);
        this.setPos(newPos.getX(), newPos.getY(), newPos.getZ());
        if (portalResult != null) {
            BlockHitResult result = portalResult.hitResult();
            if (result != null && result.getType() == BlockHitResult.Type.BLOCK
                    && !getWorld().isAir(result.getBlockPos())) {

                if (getWorld().getEntityById(this.dataTracker.get(SHOOTER)) == null) {
                    this.kill();
                    return;
                }
                ItemStack stack = ((PlayerEntity) getWorld().getEntityById(this.dataTracker.get(SHOOTER)))
                        .getMainHandStack();

                portalData = stack.get(ApertureComponents.PORTAL_COMPONENT);
                PortalComponent otherPortalData = portalData.other().orElse(new PortalComponent());

                if (!portalData.uuid().equals("")) {
                    List<? extends AperturePortal> portals = ((ServerWorld) getWorld())
                            .getEntitiesByType(ApertureEntities.APERTURE_PORTAL, (entity) -> {
                                return entity.getUuid().toString().equals(portalData.uuid());
                            });

                    if (portals.size() > 0) {
                        AperturePortal portal = portals.get(0);
                        portal.kill();
                    }
                }

                AperturePortal portal = ApertureEntities.APERTURE_PORTAL.create(getWorld());
                // TODO check if fromHorizontal is ok
                Direction lookDirection = Direction.fromHorizontal((int) this.getYaw());
                if (result.getSide() == Direction.UP || result.getSide() == Direction.DOWN) {
                    Vec3d pos = result.getBlockPos().toCenterPos()
                            .add(new Vec3d(result.getSide().getUnitVector().mul(0.501f)))
                            .add(new Vec3d(lookDirection.getUnitVector().mul(0.5f)));
                    if (!getWorld().isAir(BlockPos.ofFloored(pos))) {
                        if (getWorld().isAir(BlockPos.ofFloored(pos.add(0, 1, 0)))
                                && getWorld().isAir(result.getBlockPos().up())) {
                            pos = pos.add(0, 1, 0);
                        } else {
                            portal.kill();
                            this.kill();
                            return;
                        }
                    }
                    portal.setOriginPos(pos);
                    int invert = result.getSide() == Direction.UP ? -1 : 1;
                    portal.setOrientationAndSize(
                            new Vec3d(result.getSide().rotateClockwise(Axis.X).getUnitVector()),
                            new Vec3d(result.getSide().rotateClockwise(Axis.Z).getUnitVector()
                                    .mul(invert)),
                            1 * 0.9F, 2 * 0.9F);
                } else {
                    portal.setOriginPos(result.getBlockPos().toCenterPos()
                            .add(new Vec3d(result.getSide().getUnitVector().mul(0.501f)))
                            .add(0, -0.5, 0));
                    portal.setOrientationAndSize(
                            new Vec3d(result.getSide().rotateYCounterclockwise().getUnitVector()),
                            new Vec3d(0, 1, 0), 1 * 0.9F, 2 * 0.9F);
                }
                portal.setDestinationDimension(World.OVERWORLD);
                portal.setDestination(result.getBlockPos().toCenterPos());
                portal.setIsVisible(false);
                portal.setTeleportable(false);

                PortalComponent newPortalData = new PortalComponent(portalData.uuid(), portalData.channel(),
                        portalData.isMaster(), this.getColor(), portal.getPos(), portalData.other(),
                        getWorld().getRegistryKey());

                if (!otherPortalData.uuid().equals("")) {
                    newPortalData = new PortalComponent(portalData.uuid(), portalData.channel(),
                            portalData.isMaster(), this.getColor(), portal.getPos(), Optional.of(newPortalData),
                            getWorld().getRegistryKey());
                    PortalManipulation.makePortalRound(portal, 30);
                    portal.setIsVisible(true);
                    portal.setTeleportable(true);
                    portal.setDestinationDimension(otherPortalData.dimension());
                    portal.setDestination(otherPortalData.pos());
                    List<? extends AperturePortal> otherPortals = ((ServerWorld) getWorld())
                            .getEntitiesByType(ApertureEntities.APERTURE_PORTAL, (entity) -> {
                                return entity.getUuid().toString()
                                        .equals(otherPortalData.uuid());
                            });
                    if (otherPortals.size() > 0) {

                        AperturePortal otherPortal = otherPortals.get(0);
                        otherPortal.setDestinationDimension(portalData.dimension());
                        otherPortal.setDestination(portalData.pos());
                        portal.setOtherSideOrientation(otherPortal.getOrientationRotation());
                        otherPortal.setOtherSideOrientation(portal.getOrientationRotation());
                        otherPortal.setIsVisible(true);
                        otherPortal.setTeleportable(true);
                        PortalManipulation.makePortalRound(otherPortal, 30);
                    }
                }
                portal.getWorld().spawnEntity(portal);
                portal.setNbt(PortalComponent.toNBT(newPortalData));

                stack.set(ApertureComponents.PORTAL_COMPONENT, newPortalData);
                this.kill();
            }
        }
    }

    static {
        COLOR = DataTracker.registerData(PortalProjectileEntity.class,
                TrackedDataHandlerRegistry.INTEGER);
        SHOOTER = DataTracker.registerData(PortalProjectileEntity.class,
                TrackedDataHandlerRegistry.INTEGER);
        DISTANCE = DataTracker.registerData(PortalProjectileEntity.class,
                TrackedDataHandlerRegistry.INTEGER);
        SPAWN_POS = DataTracker.registerData(PortalProjectileEntity.class,
                TrackedDataHandlerRegistry.BLOCK_POS);
        VELOCITY = DataTracker.registerData(PortalProjectileEntity.class,
                TrackedDataHandlerRegistry.ROTATION);
        IS_MASTER = DataTracker.registerData(PortalProjectileEntity.class,
                TrackedDataHandlerRegistry.BOOLEAN);
    }

    public void setProperties(PlayerEntity user, boolean isMaster) {
        this.setRotation(user.getYaw(), user.getPitch());
        this.dataTracker.set(SHOOTER, user.getId());
        this.dataTracker.set(SPAWN_POS, user.getBlockPos());
        this.dataTracker.set(VELOCITY, new EulerAngle(user.getPitch(), user.getYaw(), 0.0F));
        this.dataTracker.set(IS_MASTER, isMaster);
        this.dataTracker.set(COLOR, isMaster ? 0xFF9A00 : 0x27A7D8);
        this.setPos(user.getX(), user.getEyeY() - getHeight() / 2, user.getZ());
    }

}
