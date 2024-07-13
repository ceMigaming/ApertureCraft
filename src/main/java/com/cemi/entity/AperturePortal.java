package com.cemi.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import qouteall.imm_ptl.core.portal.Portal;

public class AperturePortal extends Portal {

    private NbtCompound portalNbt = new NbtCompound();

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

    public void setNbt(NbtCompound nbt) {
        this.portalNbt = nbt;
    }

    public NbtCompound getNbt() {
        return this.portalNbt;
    }


    @Override
    protected void readCustomDataFromNbt(NbtCompound compoundTag) {
        portalNbt = compoundTag.getCompound("portalNbt");
        super.readCustomDataFromNbt(compoundTag);
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound compoundTag) {
        compoundTag.put("portalNbt", portalNbt);
        super.writeCustomDataToNbt(compoundTag);
    }
}
