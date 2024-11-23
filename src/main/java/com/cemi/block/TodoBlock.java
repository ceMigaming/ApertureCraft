package com.cemi.block;

import com.cemi.block.entity.TodoBlockEntity;
import com.cemi.client.gui.screen.ingame.TodoBlockScreen;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TodoBlock extends ApertureBlock implements BlockEntityProvider {
    public TodoBlock() {
        super("todo", FabricBlockSettings.copyOf(Blocks.BARRIER));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
            BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof TodoBlockEntity && player.isCreativeLevelTwoOp()) {
            if (world.isClient)
                MinecraftClient.getInstance().setScreen(new TodoBlockScreen((TodoBlockEntity) blockEntity));
            return ActionResult.success(world.isClient);
        } else {
            return ActionResult.PASS;
        }
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos arg0, BlockState arg1) {
        return new TodoBlockEntity(arg0, arg1);
    }

}
