package com.cemi.entity;

import java.util.List;
import com.cemi.world.PortalData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
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
    public PortalData portalData = new PortalData();
    public LivingEntity shooter;

    public PortalProjectileEntity(EntityType<? extends PortalProjectileEntity> entityType,
            World world) {
        super(entityType, world);
        this.noClip = true;
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(COLOR, 0xFFFFFF);
        this.dataTracker.startTracking(SHOOTER, -1);
        this.dataTracker.startTracking(DISTANCE, 10000);
        this.dataTracker.startTracking(SPAWN_POS, BlockPos.ORIGIN);
        this.dataTracker.startTracking(VELOCITY, new EulerAngle(0.0F, 0.0F, 0.0F));
        this.dataTracker.startTracking(IS_MASTER, false);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        this.setColor(nbt.getInt("color"));
        this.portalWidth = nbt.getInt("portalWidth");
        this.portalHeight = nbt.getInt("portalHeight");
        this.portalData = this.portalData.readFromNBT(nbt.getCompound("portalInfo"));
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putInt("color", this.getColor());
        nbt.putInt("portalWidth", this.portalWidth);
        nbt.putInt("portalHeight", this.portalHeight);
        nbt.put("portalInfo", this.portalData.writeToNBT(new NbtCompound()));
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
        PortalUtils.PortalAwareRaytraceResult portalResult =
                PortalUtils.portalAwareRayTrace(this, 1);
        this.setPos(newPos.getX(), newPos.getY(), newPos.getZ());
        if (portalResult != null) {
            BlockHitResult result = portalResult.hitResult();
            if (result != null && result.getType() == BlockHitResult.Type.BLOCK
                    && !getWorld().isAir(result.getBlockPos())) {

                if (getWorld().getEntityById(this.dataTracker.get(SHOOTER)) == null) {
                    this.kill();
                    return;
                }
                ItemStack stack =
                        ((PlayerEntity) getWorld().getEntityById(this.dataTracker.get(SHOOTER)))
                                .getMainHandStack();

                portalData = PortalData.getPortalData(stack, dataTracker.get(IS_MASTER));
                PortalData otherPortalData =
                        PortalData.getPortalData(stack, !dataTracker.get(IS_MASTER));

                if (!portalData.getUuid().equals("")) {
                    List<? extends AperturePortal> portals = ((ServerWorld) getWorld())
                            .getEntitiesByType(ApertureEntities.APERTURE_PORTAL, (entity) -> {
                                return entity.getUuid().toString().equals(portalData.getUuid());
                            });

                    if (portals.size() > 0) {
                        AperturePortal portal = portals.get(0);
                        portal.kill();
                    }
                }

                AperturePortal portal = ApertureEntities.APERTURE_PORTAL.create(getWorld());
                Direction lookDirection = Direction.fromRotation((float) this.getYaw());
                if (result.getSide() == Direction.UP || result.getSide() == Direction.DOWN) {
                    portal.setOriginPos(result.getBlockPos().toCenterPos()
                            .add(new Vec3d(result.getSide().getUnitVector().mul(0.501f)))
                            .add(new Vec3d(lookDirection.getUnitVector().mul(0.5f))));
                    System.out.println(result.getSide().getUnitVector());
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

                portalData.setDimension(getWorld().getRegistryKey());
                portalData.setPos(portal.getPos());
                portalData.setColor(this.getColor());

                if (!otherPortalData.getUuid().equals("")) {
                    portalData.setOther(otherPortalData);
                    otherPortalData.setOther(portalData);
                    PortalManipulation.makePortalRound(portal, 30);
                    portal.setIsVisible(true);
                    portal.setTeleportable(true);
                    portal.setDestinationDimension(otherPortalData.getDimension());
                    portal.setDestination(otherPortalData.getPos());
                    List<? extends AperturePortal> otherPortals = ((ServerWorld) getWorld())
                            .getEntitiesByType(ApertureEntities.APERTURE_PORTAL, (entity) -> {
                                return entity.getUuid().toString()
                                        .equals(otherPortalData.getUuid());
                            });
                    if (otherPortals.size() > 0) {

                        AperturePortal otherPortal = otherPortals.get(0);
                        otherPortal.setDestinationDimension(portalData.getDimension());
                        otherPortal.setDestination(portalData.getPos());
                        portal.setOtherSideOrientation(otherPortal.getOrientationRotation());
                        otherPortal.setOtherSideOrientation(portal.getOrientationRotation());
                        otherPortal.setIsVisible(true);
                        otherPortal.setTeleportable(true);
                        PortalManipulation.makePortalRound(otherPortal, 30);
                    }
                }
                portalData.setPortalData(portal.getUuid().toString(), "main",
                        dataTracker.get(IS_MASTER));
                portal.getWorld().spawnEntity(portal);
                portal.setNbt(portalData.writeToNBT(new NbtCompound()));

                if (portalData.isMaster())
                    stack.getOrCreateNbt().put("portalDataMaster",
                            portalData.writeToNBT(new NbtCompound()));
                else
                    stack.getOrCreateNbt().put("portalDataSlave",
                            portalData.writeToNBT(new NbtCompound()));

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
