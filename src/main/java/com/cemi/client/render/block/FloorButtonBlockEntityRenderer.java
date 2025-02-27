package com.cemi.client.render.block;

import com.cemi.ApertureCraft;
import com.cemi.block.FloorButtonBlock;
import com.cemi.block.entity.ApertureFloorButtonBlockEntity;
import com.cemi.client.render.model.FloorButtonModel;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class FloorButtonBlockEntityRenderer
        extends GeoBlockRenderer<ApertureFloorButtonBlockEntity> {
    private static final Identifier TEXTURE_ON =
            new Identifier(ApertureCraft.MOD_ID, "textures/block/floor_button_on.png");
    private static final Identifier TEXTURE_OFF =
            new Identifier(ApertureCraft.MOD_ID, "textures/block/floor_button_off.png");

    public FloorButtonBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        super(new FloorButtonModel());
    }

    @Override
    public Identifier getTextureLocation(ApertureFloorButtonBlockEntity animatable) {
        BlockState state = animatable.getWorld().getBlockState(animatable.getPos());
        if (state.getBlock() instanceof FloorButtonBlock)
            return state.get(FloorButtonBlock.POWERED) ? TEXTURE_ON : TEXTURE_OFF;
        else
            return TEXTURE_OFF;
    }
}
