package com.cemi.entity;

import com.cemi.block.SlopeBlock;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import qouteall.imm_ptl.core.portal.Portal;

public class AperturePortal extends Portal {

    private NbtCompound portalNbt = new NbtCompound();
    private BlockPos slopePosA;
    private BlockPos slopePosB;

    public AperturePortal(EntityType<?> entityType, World world) {
        super(entityType, world);
        if (!world.isClient)
            disableDefaultAnimation();
    }

    public int getColor() {
        if (this.portalNbt.contains("color"))
            return this.portalNbt.getInt("color");
        else
            return 0xFFFFFF;
    }

    public int getOtherColor() {
        if (this.portalNbt.contains("otherColor"))
            return this.portalNbt.getInt("otherColor");
        else
            return 0xFFFFFF;
    }

    public void setNbt(NbtCompound nbt) {
        this.portalNbt = nbt;
    }

    public NbtCompound getNbt() {
        return this.portalNbt;
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound compoundTag) {
        portalNbt = compoundTag.getCompound("portalNbt");
        if (compoundTag.contains("slopePosA"))
            slopePosA = BlockPos.fromLong(compoundTag.getLong("slopePosA"));
        if (compoundTag.contains("slopePosB"))
            slopePosB = BlockPos.fromLong(compoundTag.getLong("slopePosB"));
        super.readCustomDataFromNbt(compoundTag);
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound compoundTag) {
        compoundTag.put("portalNbt", portalNbt);
        if (slopePosA != null)
            compoundTag.putLong("slopePosA", slopePosA.asLong());
        if (slopePosB != null)
            compoundTag.putLong("slopePosB", slopePosB.asLong());
        super.writeCustomDataToNbt(compoundTag);
    }

    public void setSlopeBlockPos(BlockPos posA, BlockPos posB) {
        slopePosA = posA;
        slopePosB = posB;
    }

    public void enableAttachedSlopeCollision() {
        if (slopePosA == null || slopePosB == null)
            return;
        var world = getWorld();
        world.setBlockState(slopePosA, world.getBlockState(slopePosA).with(SlopeBlock.COLLISION_ENABLED, true));
        world.setBlockState(slopePosB, world.getBlockState(slopePosB).with(SlopeBlock.COLLISION_ENABLED, true));
        slopePosA = null;
        slopePosB = null;
    }

    public void disableAttachedSlopeCollision() {
        if (slopePosA == null || slopePosB == null)
            return;
        var world = getWorld();
        world.setBlockState(slopePosA, world.getBlockState(slopePosA).with(SlopeBlock.COLLISION_ENABLED, false));
        world.setBlockState(slopePosB, world.getBlockState(slopePosB).with(SlopeBlock.COLLISION_ENABLED, false));
    }

    @Override
    public void onEntityTeleportedOnServer(Entity entity) {
        var vNormal = entity.getVelocity().normalize();
        entity.addVelocity(vNormal.x * 0.01, vNormal.y * 0.01, vNormal.z * 0.01);
        super.onEntityTeleportedOnServer(entity);
        pushEntityOutOfBlocks(entity);
    }

    private void pushEntityOutOfBlocks(Entity entity) {
        World world = entity.getWorld();
        if (world.isClient() || entity.noClip || world.isSpaceEmpty(entity, entity.getBoundingBox())) {
            return;
        }

        Vec3d pos = entity.getPos();
        Vec3d normal = getNormal();
        Vec3d horizontalNormal = new Vec3d(normal.x, 0.0, normal.z);
        if (horizontalNormal.lengthSquared() > 1.0E-6) {
            horizontalNormal = horizontalNormal.normalize();
        } else {
            horizontalNormal = Vec3d.ZERO;
        }

        Vec3d[] directions = new Vec3d[] {
                new Vec3d(0.0, 1.0, 0.0),
                normal.normalize(),
                normal.add(0.0, 0.75, 0.0).normalize(),
                horizontalNormal.add(0.0, 0.75, 0.0).normalize()
        };

        double[] distances = new double[] {
                0.0625, 0.125, 0.25, 0.375, 0.5, 0.75, 1.0, 1.25, 1.5, 2.0
        };

        for (Vec3d direction : directions) {
            if (direction.lengthSquared() <= 1.0E-6) {
                continue;
            }

            for (double distance : distances) {
                Vec3d offset = direction.multiply(distance);
                if (world.isSpaceEmpty(entity, entity.getBoundingBox().offset(offset))) {
                    entity.refreshPositionAfterTeleport(pos.add(offset));
                    entity.fallDistance = 0.0F;
                    return;
                }
            }
        }
    }
}
