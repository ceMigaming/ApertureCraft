package com.cemi.entity;

import java.util.List;

import com.cemi.block.SlopeBlock;
import com.cemi.world.PortalData;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
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

    private static final int ORANGE_COLOR = 0xFF9A00;
    private static final int BLUE_COLOR = 0x27A7D8;

    private static final TrackedData<Integer> COLOR;
    private static final TrackedData<Integer> OTHER_COLOR;
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
        this.dataTracker.startTracking(OTHER_COLOR, 0xFFFFFF);
        this.dataTracker.startTracking(SHOOTER, -1);
        this.dataTracker.startTracking(DISTANCE, 10000);
        this.dataTracker.startTracking(SPAWN_POS, BlockPos.ORIGIN);
        this.dataTracker.startTracking(VELOCITY, new EulerAngle(0.0F, 0.0F, 0.0F));
        this.dataTracker.startTracking(IS_MASTER, false);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        this.setColor(nbt.getInt("color"));
        this.setOtherColor(nbt.getInt("otherColor"));
        this.portalWidth = nbt.getInt("portalWidth");
        this.portalHeight = nbt.getInt("portalHeight");
        this.portalData = this.portalData.readFromNBT(nbt.getCompound("portalInfo"));
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putInt("color", this.getColor());
        nbt.putInt("otherColor", this.getOtherColor());
        nbt.putInt("portalWidth", this.portalWidth);
        nbt.putInt("portalHeight", this.portalHeight);
        nbt.put("portalInfo", this.portalData.writeToNBT(new NbtCompound()));
    }

    public void setColor(final int i) {
        this.getDataTracker().set(PortalProjectileEntity.COLOR, i);
    }

    public void setOtherColor(final int i) {
        this.getDataTracker().set(PortalProjectileEntity.OTHER_COLOR, i);
    }

    public int getColor() {
        return this.getDataTracker().get(PortalProjectileEntity.COLOR);
    }

    public int getOtherColor() {
        return this.getDataTracker().get(PortalProjectileEntity.OTHER_COLOR);
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
            spawnPortal(result);
        }
    }

    private void spawnPortal(BlockHitResult hit) {
        if (!isValidHit(hit))
            return;

        PlayerEntity shooter = getShooter();
        if (shooter == null) {
            this.kill();
            return;
        }

        ItemStack stack = shooter.getMainHandStack();

        portalData = PortalData.getPortalData(stack, dataTracker.get(IS_MASTER));
        PortalData otherPortalData = PortalData.getPortalData(stack, !dataTracker.get(IS_MASTER));

        removeExistingPortal(portalData);

        AperturePortal portal = createPortal();
        if (portal == null)
            return;

        if (!setupPortalPlacement(hit, portal)) {
            this.kill();
            return;
        }

        if (!otherPortalData.getUuid().equals("")) {
            portal.disableAttachedSlopeCollision();
            List<? extends AperturePortal> others = ((ServerWorld) getWorld()).getEntitiesByType(
                    ApertureEntities.APERTURE_PORTAL,
                    e -> e.getUuid().toString().equals(otherPortalData.getUuid()));

            if (!others.isEmpty()) {
                AperturePortal other = others.get(0);
                other.disableAttachedSlopeCollision();
            }
        }

        configurePortal(portal, hit);
        linkPortalsIfPossible(portal, otherPortalData);

        savePortalData(stack, portal, otherPortalData);

        getWorld().spawnEntity(portal);
        this.kill();
    }

    private boolean isValidHit(BlockHitResult hit) {
        return hit != null
                && hit.getType() == BlockHitResult.Type.BLOCK
                && !getWorld().isAir(hit.getBlockPos());
    }

    private PlayerEntity getShooter() {
        Entity entity = getWorld().getEntityById(this.dataTracker.get(SHOOTER));
        return (entity instanceof PlayerEntity player) ? player : null;
    }

    private void removeExistingPortal(PortalData data) {
        if (data.getUuid().equals(""))
            return;

        List<? extends AperturePortal> portals = ((ServerWorld) getWorld()).getEntitiesByType(
                ApertureEntities.APERTURE_PORTAL,
                e -> e.getUuid().toString().equals(data.getUuid()));

        if (!portals.isEmpty()) {
            portals.get(0).kill();
        }
    }

    private AperturePortal createPortal() {
        return ApertureEntities.APERTURE_PORTAL.create(getWorld());
    }

    private boolean setupPortalPlacement(BlockHitResult hit, AperturePortal portal) {
        Direction side = hit.getSide();
        if (isSlope(hit.getBlockPos())) {
            return tryPlaceDiagonal(hit, portal);
        }
        if (side == Direction.UP || side == Direction.DOWN) {
            return placeOnFloorOrCeiling(hit, portal);
        } else {
            return placeOnWall(hit, portal);
        }
    }

    private boolean isSlope(BlockPos pos) {
        return getWorld().getBlockState(pos).getBlock() instanceof SlopeBlock;
    }

    private boolean tryPlaceDiagonal(BlockHitResult hit, AperturePortal portal) {
        BlockPos base = hit.getBlockPos();
        BlockState state = getWorld().getBlockState(base);

        if (!(state.getBlock() instanceof SlopeBlock))
            return false;

        Direction facing = state.get(SlopeBlock.FACING);

        // check perpendicular directions
        for (Direction dir : Direction.Type.HORIZONTAL) {
            BlockPos neighborPos = base.offset(dir).down();
            BlockState neighbor = getWorld().getBlockState(neighborPos);

            if (!(neighbor.getBlock() instanceof SlopeBlock))
                continue;

            Direction neighborFacing = neighbor.get(SlopeBlock.FACING);

            // 🔑 key condition: slopes must form a diagonal plane
            if (areDiagonalPair(facing, neighborFacing, dir)) {
                return placeDiagonalPortal(base, neighborPos, portal, facing, neighborFacing);
            }
        }

        return false;
    }

    private boolean areDiagonalPair(Direction a, Direction b, Direction offsetDir) {
        return a.getAxis() == b.getAxis();
    }

    private boolean placeDiagonalPortal(BlockPos a, BlockPos b, AperturePortal portal,
            Direction facingA, Direction facingB) {

        Vec3d center = Vec3d.ofCenter(a).add(Vec3d.ofCenter(b)).multiply(0.5);

        // diagonal normal (average of slope directions)
        Vec3d normal = new Vec3d(facingA.getOffsetX(), 1, facingA.getOffsetZ())
                .add(new Vec3d(facingB.getOffsetX(), 1, facingB.getOffsetZ()))
                .normalize();

        // right vector = perpendicular horizontal
        Vec3d right = new Vec3d(normal.z, 0, normal.x);

        // up vector = vertical diagonal (optional tweak)
        Vec3d up = new Vec3d(-normal.x, normal.y, -normal.z);

        portal.setOriginPos(center.add(normal.multiply(0.01)));

        portal.setOrientationAndSize(
                right,
                up,
                1,
                2);

        portal.setSlopeBlockPos(a, b);

        return true;
    }

    private boolean placeOnWall(BlockHitResult hit, AperturePortal portal) {
        Direction side = hit.getSide();

        BlockPos base = hit.getBlockPos().offset(side);

        Direction right = side.rotateYCounterclockwise();
        Direction up = Direction.UP;

        if (!canPlacePortal(base, up))
            return false;

        portal.setOriginPos(Vec3d.ofCenter(base).add(0, -0.5, 0).add(Vec3d.of(side.getVector()).multiply(-0.499)));
        portal.setOrientationAndSize(
                new Vec3d(right.getUnitVector()),
                new Vec3d(0, 1, 0),
                1, 2);

        return true;
    }

    private boolean placeOnFloorOrCeiling(BlockHitResult hit, AperturePortal portal) {
        Direction side = hit.getSide();

        BlockPos base = hit.getBlockPos().offset(side);

        Direction right = side.rotateClockwise(Axis.X);
        Direction up = side.rotateClockwise(Axis.Z);

        if (side == Direction.UP) {
            up = up.getOpposite();
        }

        if (!canPlacePortal(base, up))
            return false;

        Vec3d pos = Vec3d.ofCenter(base)
                .add(new Vec3d(side.getUnitVector()).multiply(-0.499));

        portal.setOriginPos(pos);
        portal.setOrientationAndSize(
                new Vec3d(right.getUnitVector()),
                new Vec3d(up.getUnitVector()),
                1, 2);

        return true;
    }

    private boolean canPlacePortal(BlockPos base, Direction up) {
        return isFree(base) && isFree(base.offset(up));
    }

    private boolean isFree(BlockPos pos) {
        return getWorld().getBlockState(pos).isReplaceable();
    }

    private void configurePortal(AperturePortal portal, BlockHitResult hit) {
        portal.setDestinationDimension(World.OVERWORLD);
        portal.setDestination(hit.getBlockPos().toCenterPos());
        portal.setIsVisible(false);
        portal.setTeleportable(false);

        portalData.setDimension(getWorld().getRegistryKey());
        portalData.setPos(portal.getPos());
        portalData.setColor(this.getColor());
        portalData.setOtherColor(this.getOtherColor());
    }

    private void linkPortalsIfPossible(AperturePortal portal, PortalData otherData) {
        if (otherData.getUuid().equals(""))
            return;

        portalData.setOther(otherData);
        otherData.setOther(portalData);

        PortalManipulation.makePortalRound(portal, 30);

        portal.setIsVisible(true);
        portal.setTeleportable(true);
        portal.setDestinationDimension(otherData.getDimension());
        portal.setDestination(otherData.getPos());

        List<? extends AperturePortal> others = ((ServerWorld) getWorld()).getEntitiesByType(
                ApertureEntities.APERTURE_PORTAL,
                e -> e.getUuid().toString().equals(otherData.getUuid()));

        if (!others.isEmpty()) {
            AperturePortal other = others.get(0);

            other.setDestinationDimension(portalData.getDimension());
            other.setDestination(portalData.getPos());

            portal.setOtherSideOrientation(other.getOrientationRotation());
            other.setOtherSideOrientation(portal.getOrientationRotation());

            other.setIsVisible(true);
            other.setTeleportable(true);

            PortalManipulation.makePortalRound(other, 30);
        }
    }

    private void savePortalData(ItemStack stack, AperturePortal portal, PortalData otherData) {
        portalData.setPortalData(portal.getUuid().toString(), "main", dataTracker.get(IS_MASTER));

        portal.setNbt(portalData.writeToNBT(new NbtCompound()));

        if (portalData.isMaster()) {
            stack.getOrCreateNbt().put("portalDataMaster", portalData.writeToNBT(new NbtCompound()));
        } else {
            stack.getOrCreateNbt().put("portalDataSlave", portalData.writeToNBT(new NbtCompound()));
        }
    }

    static {
        COLOR = DataTracker.registerData(PortalProjectileEntity.class,
                TrackedDataHandlerRegistry.INTEGER);
        OTHER_COLOR = DataTracker.registerData(PortalProjectileEntity.class,
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
        this.dataTracker.set(COLOR, isMaster ? ORANGE_COLOR : BLUE_COLOR);
        this.dataTracker.set(OTHER_COLOR, isMaster ? BLUE_COLOR : ORANGE_COLOR);

        this.setPos(user.getX(), user.getEyeY() - getHeight() / 2, user.getZ());
    }

}
