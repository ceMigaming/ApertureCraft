package com.cemi.block;

import com.cemi.block.entity.HEPLauncherBlockEntity;
import com.cemi.entity.ApertureEntities;
import com.cemi.entity.HighEnergyPelletEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class HEPLauncherBlock extends ApertureBlock implements BlockEntityProvider {

    public static final DirectionProperty FACING = Properties.FACING;
    public static final BooleanProperty TRIGGERED = Properties.TRIGGERED;

    public HEPLauncherBlock(String name, Settings settings) {
        super(name, settings, true);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new HEPLauncherBlockEntity(pos, state);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState) this.getDefaultState().with(FACING,
                ctx.getPlayerLookDirection().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, TRIGGERED);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock,
            BlockPos sourcePos, boolean notify) {
        boolean isReceivingRedstonePower = world.isReceivingRedstonePower(pos)
                || world.isReceivingRedstonePower(pos.up());
        boolean isTriggered = (Boolean) state.get(TRIGGERED);
        if (isReceivingRedstonePower && !isTriggered) {
            world.scheduleBlockTick(pos, this, 4);
            world.setBlockState(pos, (BlockState) state.with(TRIGGERED, true), 2);
        } else if (!isReceivingRedstonePower && isTriggered) {
            world.setBlockState(pos, (BlockState) state.with(TRIGGERED, false), 2);
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        shootPellet(state, world, pos);
    }

    private void shootPellet(BlockState state, ServerWorld world, BlockPos pos) {
        if (world.isClient()) {
            return;
        }

        HEPLauncherBlockEntity be = (HEPLauncherBlockEntity) world.getBlockEntity(pos);

        // Remove existing pellet if it exists
        if (be.getActivePelletUuid() != null) {
            var existing = world.getEntity(be.getActivePelletUuid());
            if (existing != null) {
                existing.kill();
            }
            be.clearActivePellet();
        }

        // Play animation
        be.triggerAnim("controller", "shoot");

        // Spawn new pellet
        HighEnergyPelletEntity hep = ApertureEntities.HIGH_ENERGY_PELLET.create(world);

        hep.setPos(
                pos.getX() + 0.5 + state.get(FACING).getOffsetX(),
                pos.getY() + 0.3 + state.get(FACING).getOffsetY(),
                pos.getZ() + 0.5 + state.get(FACING).getOffsetZ());

        hep.setVelocity(
                state.get(FACING).getOffsetX() * 0.1,
                state.get(FACING).getOffsetY() * 0.1,
                state.get(FACING).getOffsetZ() * 0.1);

        world.spawnEntity(hep);

        // Save the new pellet's UUID
        be.setActivePellet(hep.getUuid());
    }
}
