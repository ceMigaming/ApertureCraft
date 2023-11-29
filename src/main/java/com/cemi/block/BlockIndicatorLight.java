package com.cemi.block;

import java.util.Map;
import com.cemi.ApertureCraft;
import com.cemi.block.entity.IndicatorLightEntity;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;

public class BlockIndicatorLight extends ApertureBlock implements BlockEntityProvider {

    public static final EnumProperty<WireConnection> WIRE_CONNECTION_NORTH;
    public static final EnumProperty<WireConnection> WIRE_CONNECTION_EAST;
    public static final EnumProperty<WireConnection> WIRE_CONNECTION_SOUTH;
    public static final EnumProperty<WireConnection> WIRE_CONNECTION_WEST;
    public static final EnumProperty<WireConnection> WIRE_CONNECTION_UP;
    public static final EnumProperty<WireConnection> WIRE_CONNECTION_DOWN;
    public static final BooleanProperty POWERED;
    public static final Map<Direction, EnumProperty<WireConnection>> DIRECTION_TO_WIRE_CONNECTION_PROPERTY;
    private static final VoxelShape DOT_SHAPE;
    private static final Map<Direction, VoxelShape> DIRECTION_TO_SIDE_SHAPE;
    private static final Map<Direction, VoxelShape> DIRECTION_TO_UP_SHAPE;
    private static final Map<BlockState, VoxelShape> SHAPES;
    private static final Vec3d[] COLORS;
    private final BlockState dotState;

    static {
        WIRE_CONNECTION_NORTH = Properties.NORTH_WIRE_CONNECTION;
        WIRE_CONNECTION_EAST = Properties.EAST_WIRE_CONNECTION;
        WIRE_CONNECTION_SOUTH = Properties.SOUTH_WIRE_CONNECTION;
        WIRE_CONNECTION_WEST = Properties.WEST_WIRE_CONNECTION;
        WIRE_CONNECTION_UP = EnumProperty.of("up", WireConnection.class);
        WIRE_CONNECTION_DOWN = EnumProperty.of("down", WireConnection.class);
        POWERED = BooleanProperty.of("powered");
        DIRECTION_TO_WIRE_CONNECTION_PROPERTY = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH,
                WIRE_CONNECTION_NORTH, Direction.EAST, WIRE_CONNECTION_EAST, Direction.SOUTH,
                WIRE_CONNECTION_SOUTH, Direction.WEST, WIRE_CONNECTION_WEST, Direction.UP,
                WIRE_CONNECTION_UP, Direction.DOWN, WIRE_CONNECTION_DOWN));
        DOT_SHAPE = Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 1.0, 13.0);
        DIRECTION_TO_SIDE_SHAPE = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH,
                Block.createCuboidShape(3.0, 0.0, 0.0, 13.0, 1.0, 13.0), Direction.SOUTH,
                Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 1.0, 16.0), Direction.EAST,
                Block.createCuboidShape(3.0, 0.0, 3.0, 16.0, 1.0, 13.0), Direction.WEST,
                Block.createCuboidShape(0.0, 0.0, 3.0, 13.0, 1.0, 13.0), Direction.UP,
                Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 16.0, 13.0), Direction.DOWN,
                Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 1.0, 13.0)));
        DIRECTION_TO_UP_SHAPE =
                Maps.newEnumMap(ImmutableMap.of(Direction.NORTH,
                        VoxelShapes.union((VoxelShape) DIRECTION_TO_SIDE_SHAPE.get(Direction.NORTH),
                                Block.createCuboidShape(3.0, 0.0, 0.0, 13.0, 16.0, 1.0)),
                        Direction.SOUTH,
                        VoxelShapes.union((VoxelShape) DIRECTION_TO_SIDE_SHAPE.get(Direction.SOUTH),
                                Block.createCuboidShape(3.0, 0.0, 15.0, 13.0, 16.0, 16.0)),
                        Direction.EAST,
                        VoxelShapes.union((VoxelShape) DIRECTION_TO_SIDE_SHAPE.get(Direction.EAST),
                                Block.createCuboidShape(15.0, 0.0, 3.0, 16.0, 16.0, 13.0)),
                        Direction.WEST,
                        VoxelShapes.union((VoxelShape) DIRECTION_TO_SIDE_SHAPE.get(Direction.WEST),
                                Block.createCuboidShape(0.0, 0.0, 3.0, 1.0, 16.0, 13.0))));
        SHAPES = Maps.newHashMap();
        COLORS = new Vec3d[] {new Vec3d(0.09f, 0.69f, 0.82f), new Vec3d(1.f, 1.f, 0.13f)};
    }

    public BlockIndicatorLight(String name, Settings settings) {
        super(name, settings);
        this.setDefaultState(
                this.stateManager.getDefaultState().with(WIRE_CONNECTION_NORTH, WireConnection.NONE)
                        .with(WIRE_CONNECTION_EAST, WireConnection.NONE)
                        .with(WIRE_CONNECTION_SOUTH, WireConnection.NONE)
                        .with(WIRE_CONNECTION_WEST, WireConnection.NONE).with(POWERED, false));
        this.dotState = this.getDefaultState().with(WIRE_CONNECTION_NORTH, WireConnection.SIDE)
                .with(WIRE_CONNECTION_EAST, WireConnection.SIDE)
                .with(WIRE_CONNECTION_SOUTH, WireConnection.SIDE)
                .with(WIRE_CONNECTION_WEST, WireConnection.SIDE);
    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> builder) {
        builder.add(WIRE_CONNECTION_NORTH, WIRE_CONNECTION_EAST, WIRE_CONNECTION_SOUTH,
                WIRE_CONNECTION_WEST, WIRE_CONNECTION_UP, WIRE_CONNECTION_DOWN, POWERED);
    }

    // TODO not working
    // @Override
    // public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos,
    // ShapeContext context) {
    // return (VoxelShape) SHAPES.get(state.with(POWERED, false));
    // }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState,
            boolean notify) {
        if (!world.isClient) {
            boolean otherIndicators = false;
            Direction[] directions = Direction.values();
            for (Direction direction : directions) {
                BlockPos neighboringPos = pos.offset(direction);
                if (!(world.getBlockState(neighboringPos)
                        .getBlock() instanceof BlockIndicatorLight)) {
                    continue;
                }
                BlockEntity blockEntity = world.getBlockEntity(neighboringPos);
                if (blockEntity instanceof IndicatorLightEntity) {
                    IndicatorLightEntity indicatorLightEntity = (IndicatorLightEntity) blockEntity;
                    // indicatorLightEntity.addConnectedBlock(pos);
                    indicatorLightEntity.getConnectedBlocksRecursive(pos);
                    otherIndicators = true;
                    System.out.println("Found another indicator light!");
                }
            }
            if (!otherIndicators) {
                System.out.println("First indicator light!");
                IndicatorLightEntity indicatorLightEntity =
                        (IndicatorLightEntity) world.getBlockEntity(pos);
                indicatorLightEntity.setMaster(true);
                System.out.println(indicatorLightEntity.findMasterBlock().toString());
            }
        }
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new IndicatorLightEntity(pos, state);
    }
}
