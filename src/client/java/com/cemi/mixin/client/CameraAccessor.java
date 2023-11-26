package com.cemi.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;

@Mixin(Camera.class)
public interface CameraAccessor {

    @Accessor
    public BlockView getArea();

    @Accessor
    public BlockPos.Mutable getBlockPos();

    @Accessor
    public Vec3d getPos();

}
