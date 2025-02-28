package com.cemi.block;

import com.cemi.ApertureCraft;
import com.cemi.block.entity.ApertureDoorBlockEntity;
import com.cemi.entity.ApertureEntities;
import com.cemi.entity.GhostBlockEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class ApertureDoorBlock extends ApertureBlock implements BlockEntityProvider {

    public static final DirectionProperty FACING = Properties.FACING;
    public static final BooleanProperty TRIGGERED = Properties.TRIGGERED;
    public static final IntProperty SIDE = IntProperty.of("side", 0, 3);

    public ApertureDoorBlock(Settings settings) {
        super("door", settings, true);
        setDefaultState(getDefaultState().with(FACING, Direction.NORTH).with(TRIGGERED, false)
                .with(SIDE, 0));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState) this.getDefaultState()
                .with(FACING, ctx.getPlayerLookDirection().getOpposite()).with(TRIGGERED, false);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer,
            ItemStack itemStack) {
        if (world.isClient()) {
            return;
        }
        Direction facing = Direction.getEntityFacingOrder(placer)[0];
        int dx = 0;
        int dy = 0;
        int dz = 0;
        int dw = 0;
        switch (facing) {
            case NORTH:
                dx++;
                dy++;
                break;
            case SOUTH:
                dx--;
                dy++;
                break;
            case EAST:
                dy++;
                dz++;
                break;
            case WEST:
                dy++;
                dz--;
                break;
            case UP:
                dx++;
                dw++;
                break;
            default:
            case DOWN:
                dx++;
                dw--;
                break;
        }
        BlockPos upperRightBlockPos = new BlockPos(pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz + dw);
        BlockPos lowerLeftBlockPos = new BlockPos(pos.getX(), pos.getY() + dy, pos.getZ() + dw);
        BlockPos lowerRightBlockPos = new BlockPos(pos.getX() + dx, pos.getY(), pos.getZ() + dz);

        if (!world.isAir(upperRightBlockPos) || !world.isAir(lowerLeftBlockPos)
                || !world.isAir(lowerRightBlockPos)) {
            placer.sendMessage(Text.translatable(ApertureCraft.MOD_ID + ".door.no_space")
                    .setStyle(Style.EMPTY.withColor(Formatting.RED)));
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
            if (!world.isAir(upperRightBlockPos)) {
                GhostBlockEntity upperRightGhostBlockEntity = ApertureEntities.GHOSTBLOCK.create(world);
                upperRightGhostBlockEntity.setPos(upperRightBlockPos.toCenterPos().getX(),
                        upperRightBlockPos.toCenterPos().getY(),
                        upperRightBlockPos.toCenterPos().getZ());
                world.spawnEntity(upperRightGhostBlockEntity);
            }
            if (!world.isAir(lowerLeftBlockPos)) {
                GhostBlockEntity lowerLeftGhostBlockEntity = ApertureEntities.GHOSTBLOCK.create(world);
                lowerLeftGhostBlockEntity.setPos(lowerLeftBlockPos.toCenterPos().getX(),
                        lowerLeftBlockPos.toCenterPos().getY(),
                        lowerLeftBlockPos.toCenterPos().getZ());
                world.spawnEntity(lowerLeftGhostBlockEntity);
            }
            if (!world.isAir(lowerRightBlockPos)) {

                GhostBlockEntity lowerRightGhostBlockEntity = ApertureEntities.GHOSTBLOCK.create(world);
                lowerRightGhostBlockEntity.setPos(lowerRightBlockPos.toCenterPos().getX(),
                        lowerRightBlockPos.toCenterPos().getY(),
                        lowerRightBlockPos.toCenterPos().getZ());
                world.spawnEntity(lowerRightGhostBlockEntity);
            }
            return;
        }
        world.setBlockState(pos, state.with(SIDE, 0));
        world.setBlockState(upperRightBlockPos, state.with(SIDE, 1));
        world.setBlockState(lowerLeftBlockPos, state.with(SIDE, 2));
        world.setBlockState(lowerRightBlockPos, state.with(SIDE, 3));

        BlockEntity upperLeftBlockEntity = world.getBlockEntity(pos);
        BlockEntity upperRightBlockEntity = world.getBlockEntity(upperRightBlockPos);
        BlockEntity lowerLeftBlockEntity = world.getBlockEntity(lowerLeftBlockPos);
        BlockEntity lowerRightBlockEntity = world.getBlockEntity(lowerRightBlockPos);

        if (world.getBlockState(pos).get(SIDE) == 0) {
            if (upperLeftBlockEntity instanceof ApertureDoorBlockEntity
                    && upperRightBlockEntity instanceof ApertureDoorBlockEntity
                    && lowerLeftBlockEntity instanceof ApertureDoorBlockEntity
                    && lowerRightBlockEntity instanceof ApertureDoorBlockEntity) {
                ((ApertureDoorBlockEntity) upperLeftBlockEntity).setBlockPoses(pos,
                        upperRightBlockPos, lowerLeftBlockPos, lowerRightBlockPos);
                ((ApertureDoorBlockEntity) upperRightBlockEntity).setBlockPoses(pos,
                        upperRightBlockPos, lowerLeftBlockPos, lowerRightBlockPos);
                ((ApertureDoorBlockEntity) lowerLeftBlockEntity).setBlockPoses(pos,
                        upperRightBlockPos, lowerLeftBlockPos, lowerRightBlockPos);
                ((ApertureDoorBlockEntity) lowerRightBlockEntity).setBlockPoses(pos,
                        upperRightBlockPos, lowerLeftBlockPos, lowerRightBlockPos);
            }
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, TRIGGERED, SIDE);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ApertureDoorBlockEntity(pos, state);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos,
            ShapeContext context) {
        Direction facing = state.get(FACING);
        boolean shouldFlip = state.get(SIDE) == 1 || state.get(SIDE) == 3;
        if (state.get(TRIGGERED) == false) {
            return switch (facing) {
                case WEST -> createCuboidShape(4, 0, 0, 12, 16, 16);
                case EAST -> createCuboidShape(4, 0, 0, 12, 16, 16);
                case NORTH -> createCuboidShape(0, 0, 4, 16, 16, 12);
                case SOUTH -> createCuboidShape(0, 0, 4, 16, 16, 12);
                case DOWN -> createCuboidShape(0, 11, 0, 16, 16, 16);
                case UP -> createCuboidShape(0, 0, 0, 16, 5, 16);
                default -> createCuboidShape(0, 0, 0, 16, 16, 16);
            };
        } else {
            if (!shouldFlip)
                return switch (facing) {
                    case EAST -> createCuboidShape(4, 0, 11, 12, 16, 16);
                    case WEST -> createCuboidShape(4, 0, 0, 12, 16, 5);
                    case SOUTH -> createCuboidShape(0, 0, 4, 5, 16, 12);
                    case NORTH -> createCuboidShape(11, 0, 4, 16, 16, 12);
                    case DOWN -> createCuboidShape(0, 11, 0, 16, 16, 16);
                    case UP -> createCuboidShape(0, 0, 0, 16, 5, 16);
                    default -> createCuboidShape(0, 0, 0, 16, 16, 16);
                };
            else
                return switch (facing) {
                    case WEST -> createCuboidShape(4, 0, 11, 12, 16, 16);
                    case EAST -> createCuboidShape(4, 0, 0, 12, 16, 5);
                    case NORTH -> createCuboidShape(0, 0, 4, 5, 16, 12);
                    case SOUTH -> createCuboidShape(11, 0, 4, 16, 16, 12);
                    case DOWN -> createCuboidShape(0, 11, 0, 16, 16, 16);
                    case UP -> createCuboidShape(0, 0, 0, 16, 5, 16);
                    default -> createCuboidShape(0, 0, 0, 16, 16, 16);
                };
        }
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock,
            BlockPos sourcePos, boolean notify) {
        ApertureDoorBlockEntity doorEntity = (ApertureDoorBlockEntity) world.getBlockEntity(pos);
        if (doorEntity == null) {
            return;
        }
        boolean isReceivingRedstonePower = world.isReceivingRedstonePower(pos)
                || world.isReceivingRedstonePower(pos.up());
        boolean isTriggered = (Boolean) state.get(TRIGGERED);
        if (isReceivingRedstonePower && !isTriggered) {
            BlockPos upperLeftBlockPos = ((ApertureDoorBlockEntity) doorEntity).getUpperLeftBlockPos();
            BlockPos upperRightBlockPos = ((ApertureDoorBlockEntity) doorEntity).getUpperRightBlockPos();
            BlockPos lowerLeftBlockPos = ((ApertureDoorBlockEntity) doorEntity).getLowerLeftBlockPos();
            BlockPos lowerRightBlockPos = ((ApertureDoorBlockEntity) doorEntity).getLowerRightBlockPos();

            world.setBlockState(upperLeftBlockPos, world.getBlockState(upperLeftBlockPos).with(TRIGGERED, true), 2);
            world.setBlockState(upperRightBlockPos, world.getBlockState(upperRightBlockPos).with(TRIGGERED, true), 2);
            world.setBlockState(lowerLeftBlockPos, world.getBlockState(lowerLeftBlockPos).with(TRIGGERED, true), 2);
            world.setBlockState(lowerRightBlockPos, world.getBlockState(lowerRightBlockPos).with(TRIGGERED, true), 2);

            ApertureDoorBlockEntity mainDoorEntity = (ApertureDoorBlockEntity) world.getBlockEntity(upperLeftBlockPos);
            if (mainDoorEntity != null) {
                mainDoorEntity.triggerAnim("controller", "open");
            }
        } else if (!isReceivingRedstonePower && isTriggered) {
            BlockPos upperLeftBlockPos = ((ApertureDoorBlockEntity) doorEntity).getUpperLeftBlockPos();
            BlockPos upperRightBlockPos = ((ApertureDoorBlockEntity) doorEntity).getUpperRightBlockPos();
            BlockPos lowerLeftBlockPos = ((ApertureDoorBlockEntity) doorEntity).getLowerLeftBlockPos();
            BlockPos lowerRightBlockPos = ((ApertureDoorBlockEntity) doorEntity).getLowerRightBlockPos();

            world.setBlockState(upperLeftBlockPos, world.getBlockState(upperLeftBlockPos).with(TRIGGERED, false), 2);
            world.setBlockState(upperRightBlockPos, world.getBlockState(upperRightBlockPos).with(TRIGGERED, false), 2);
            world.setBlockState(lowerLeftBlockPos, world.getBlockState(lowerLeftBlockPos).with(TRIGGERED, false), 2);
            world.setBlockState(lowerRightBlockPos, world.getBlockState(lowerRightBlockPos).with(TRIGGERED, false), 2);

            ApertureDoorBlockEntity mainDoorEntity = (ApertureDoorBlockEntity) world.getBlockEntity(upperLeftBlockPos);
            if (mainDoorEntity != null) {
                mainDoorEntity.triggerAnim("controller", "close");
            }
        }
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient()) {
            BlockEntity currentBlockEntity = world.getBlockEntity(pos);
            if (currentBlockEntity instanceof ApertureDoorBlockEntity) {
                BlockPos upperLeftBlockPos = ((ApertureDoorBlockEntity) currentBlockEntity).getUpperLeftBlockPos();
                BlockPos upperRightBlockPos = ((ApertureDoorBlockEntity) currentBlockEntity).getUpperRightBlockPos();
                BlockPos lowerLeftBlockPos = ((ApertureDoorBlockEntity) currentBlockEntity).getLowerLeftBlockPos();
                BlockPos lowerRightBlockPos = ((ApertureDoorBlockEntity) currentBlockEntity).getLowerRightBlockPos();

                world.removeBlock(upperLeftBlockPos, false);
                world.removeBlock(upperRightBlockPos, false);
                world.removeBlock(lowerLeftBlockPos, false);
                world.removeBlock(lowerRightBlockPos, false);
            }
        }
        return super.onBreak(world, pos, state, player);
    }

}
