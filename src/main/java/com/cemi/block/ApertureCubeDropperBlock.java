package com.cemi.block;

import com.cemi.block.entity.ApertureCubeDropperBlockEntity;
import com.cemi.entity.ApertureEntities;
import com.cemi.entity.GhostBlockEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class ApertureCubeDropperBlock extends ApertureBlock implements BlockEntityProvider {

    public static final BooleanProperty IS_SLAVE = BooleanProperty.of("slave");
    public static final BooleanProperty CAN_GO_THROUGH = BooleanProperty.of("can_go_through");
    public static final BooleanProperty TRIGGERED = Properties.TRIGGERED;

    private static final VoxelShape SHAPE = VoxelShapes.cuboid(0f, 0f, 0f, 16f / 16f, 1f, 16f / 16f);

    private final EntityType<?> spawnableEntity;

    public ApertureCubeDropperBlock(String name, Settings settings, EntityType<?> spawnableEntity) {
        super(name, settings);
        this.spawnableEntity = spawnableEntity;
        setDefaultState(getDefaultState().with(IS_SLAVE, false).with(CAN_GO_THROUGH, true));
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ApertureCubeDropperBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return state.get(IS_SLAVE) ? BlockRenderType.INVISIBLE : BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return state.get(CAN_GO_THROUGH) ? VoxelShapes.empty() : SHAPE;
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (world.isClient || state.get(IS_SLAVE)) {
            return;
        }
        int masterX = pos.getX();
        int masterY = pos.getY();
        int masterZ = pos.getZ();

        if (world.getBlockState(pos.up()).getBlock() != Blocks.AIR
                && world.getBlockState(pos.down(2)).getBlock() == Blocks.AIR) {
            world.breakBlock(pos, false);
            world.setBlockState(pos.down(2), state, 3);
            return;
        }

        if (!canPlaceAt(world, pos)) {
            world.breakBlock(pos, false);
            return;
        }

        for (int x = -1; x < 2; x++) {
            for (int y = 0; y < 3; y++) {
                for (int z = -1; z < 2; z++) {
                    BlockPos currentPos = new BlockPos(masterX + x, masterY + y, masterZ + z);
                    if (!currentPos.equals(pos)) {
                        BlockState masterState = world.getBlockState(pos);
                        world.setBlockState(currentPos,
                                masterState.with(IS_SLAVE, true).with(CAN_GO_THROUGH, x == 0 && z == 0 ? true : false));
                    }
                    ApertureCubeDropperBlockEntity blockEntity = (ApertureCubeDropperBlockEntity) world
                            .getBlockEntity(currentPos);
                    blockEntity.setMasterPos(pos);
                }
            }
        }
        super.onBlockAdded(state, world, pos, oldState, notify);
    }

    boolean canPlaceAt(World world, BlockPos pos) {
        boolean canPlace = true;
        for (int x = -1; x < 2; x++) {
            for (int y = 0; y < 3; y++) {
                for (int z = -1; z < 2; z++) {
                    BlockPos currentPos = new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                    BlockState currentState = world.getBlockState(currentPos);
                    if (currentState.getBlock() != this && currentState.getBlock() != Blocks.AIR) {
                        canPlace = false;
                        GhostBlockEntity ghostBlockEntity = ApertureEntities.GHOSTBLOCK.create(world);
                        Vec3d ghostBlockPos = pos.toCenterPos();
                        ghostBlockEntity.setPos(ghostBlockPos.getX() + x, ghostBlockPos.getY() + y,
                                ghostBlockPos.getZ() + z);
                        world.spawnEntity(ghostBlockEntity);
                    }
                }
            }
        }
        return canPlace;
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (world.isClient()) {
            return super.onBreak(world, pos, state, player);
        }

        ApertureCubeDropperBlockEntity blockEntity = (ApertureCubeDropperBlockEntity) world.getBlockEntity(pos);
        BlockPos masterPos = blockEntity.getMasterPos();
        int masterX = masterPos.getX();
        int masterY = masterPos.getY();
        int masterZ = masterPos.getZ();
        for (int x = -1; x < 2; x++) {
            for (int y = 0; y < 3; y++) {
                for (int z = -1; z < 2; z++) {
                    BlockPos currentPos = new BlockPos(masterX + x, masterY + y, masterZ + z);
                    BlockState currentState = world.getBlockState(currentPos);
                    if (currentState.getBlock() == this) {
                        world.breakBlock(currentPos, false);
                    }
                }
            }
        }
        return super.onBreak(world, pos, state, player);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos,
            boolean notify) {
        if (world.isClient) {
            return;
        }
        if (world.isReceivingRedstonePower(pos)) {
            // TODO add animations and entity binding - kill old entity on new entity spawn
            ApertureCubeDropperBlockEntity blockEntity = (ApertureCubeDropperBlockEntity) world.getBlockEntity(pos);
            blockEntity.
            BlockPos masterPos = ((ApertureCubeDropperBlockEntity) world.getBlockEntity(pos)).getMasterPos();
            world.scheduleBlockTick(masterPos, this, 4);
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        Entity spawnableEntity = this.spawnableEntity.create(world);
        spawnableEntity.refreshPositionAndAngles(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 0, 0);
        world.spawnEntity(spawnableEntity);
    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> builder) {
        builder.add(IS_SLAVE);
        builder.add(CAN_GO_THROUGH);
        super.appendProperties(builder);
    }
}
