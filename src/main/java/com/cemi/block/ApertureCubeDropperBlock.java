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
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
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

    private static final VoxelShape SHAPE = VoxelShapes.cuboid(0f, 0f, 0f, 16f / 16f, 1f, 16f / 16f);

    private final EntityType<?> spawnableEntity;

    public ApertureCubeDropperBlock(String name, Settings settings, EntityType<?> spawnableEntity) {
        super(name, settings, true);
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
        ApertureCubeDropperBlockEntity masterBE = (ApertureCubeDropperBlockEntity) world.getBlockEntity(pos);
        masterBE.setSpawnableEntity(this.spawnableEntity);
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

        ApertureCubeDropperBlockEntity masterBlockEntity = (ApertureCubeDropperBlockEntity) world.getBlockEntity(masterPos);
        masterBlockEntity.killEntity();

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
            BlockPos masterPos = ((ApertureCubeDropperBlockEntity) world.getBlockEntity(pos)).getMasterPos();
            world.scheduleBlockTick(masterPos, this, 0);
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        ApertureCubeDropperBlockEntity blockEntity = (ApertureCubeDropperBlockEntity) world.getBlockEntity(pos);
        BlockPos masterPos = ((ApertureCubeDropperBlockEntity) world.getBlockEntity(pos)).getMasterPos();
        ApertureCubeDropperBlockEntity masterBlockEntity = (ApertureCubeDropperBlockEntity) world
                .getBlockEntity(masterPos);

        if (!blockEntity.getTriggered()) {
            blockEntity.triggerAnim("controller", "open");
            masterBlockEntity.spawnEntity();
            blockEntity.setTriggered(true);
            world.scheduleBlockTick(masterPos, this, 4);
        } else {
            blockEntity.triggerAnim("controller", "close");
            blockEntity.setTriggered(false);
        }
    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> builder) {
        builder.add(IS_SLAVE);
        builder.add(CAN_GO_THROUGH);
        super.appendProperties(builder);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
            BlockHitResult hit) {
        if(world.isClient) {
            return ActionResult.PASS;
        }
        if (player.isCreative()) {
            ItemStack itemStack = player.getStackInHand(hand);
            if (itemStack.getItem() instanceof SpawnEggItem) {
                ApertureCubeDropperBlockEntity blockEntity = (ApertureCubeDropperBlockEntity) world.getBlockEntity(pos);
                BlockPos masterPos = blockEntity.getMasterPos();
                ApertureCubeDropperBlockEntity masterBlockEntity = (ApertureCubeDropperBlockEntity) world
                        .getBlockEntity(masterPos);
                masterBlockEntity.killEntity();
                masterBlockEntity.setSpawnableEntity(((SpawnEggItem) itemStack.getItem()).getEntityType(null));
                return ActionResult.success(true);
            }
        }
        return ActionResult.PASS;
    }
}
