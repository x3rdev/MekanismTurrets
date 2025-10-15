package com.github.x3r.mekanism_turrets.common.block;

import com.github.x3r.mekanism_turrets.common.block_entity.LaserTurretBlockEntity;
import mekanism.common.block.prefab.BlockTile;
import mekanism.common.content.blocktype.BlockTypeTile;
import mekanism.common.resource.BlockResourceInfo;
import mekanism.common.tile.base.WrenchResult;
import mekanism.common.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class LaserTurretBlock extends BlockTile.BlockTileModel<LaserTurretBlockEntity, BlockTypeTile<LaserTurretBlockEntity>> {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    private static VoxelShape SHAPE_NORTH;
    private static VoxelShape SHAPE_EAST;
    private static VoxelShape SHAPE_SOUTH;
    private static VoxelShape SHAPE_WEST;
    private static VoxelShape SHAPE_UP;
    private static VoxelShape SHAPE_DOWN;
    public LaserTurretBlock(BlockTypeTile<LaserTurretBlockEntity> type) {
        super(type, properties -> properties.mapColor(BlockResourceInfo.STEEL.getMapColor()));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(@NotNull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    public BlockState rotate(BlockState pState, Rotation pRot) {
        return pState.setValue(FACING, pRot.rotate(pState.getValue(FACING)));
    }

    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        LaserTurretBlockEntity tile = WorldUtils.getTileEntity(LaserTurretBlockEntity.class, world, pos, true);
        if (tile == null) {
            return InteractionResult.PASS;
        } else if (world.isClientSide) {
            return genericClientActivated(player, hand);
        } else if (tile.tryWrench(state, player, hand, hit) != WrenchResult.PASS) {
            return InteractionResult.SUCCESS;
        }
        return tile.openGui(player);
    }

    @Override
    public boolean hasAnalogOutputSignal(@NotNull BlockState blockState) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(@NotNull BlockState blockState, @NotNull Level world, @NotNull BlockPos pos) {
        LaserTurretBlockEntity tile = WorldUtils.getTileEntity(LaserTurretBlockEntity.class, world, pos, true);
        return tile.hasTarget() ? 15 : 0;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getClickedFace().getOpposite());
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        switch (state.getValue(FACING)) {
            case NORTH -> {
                if(SHAPE_NORTH == null) {
                    SHAPE_NORTH = Stream.of(
                            Block.box(2, 2, 0, 14, 14, 1),
                            Block.box(5, 5, 1, 11, 11, 2),
                            Block.box(12, 12, 1, 13, 13, 2),
                            Block.box(12, 3, 1, 13, 4, 2),
                            Block.box(3, 3, 1, 4, 4, 2),
                            Block.box(3, 12, 1, 4, 13, 2)
                    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
                }
                return SHAPE_NORTH;
            }
            case EAST -> {
                if(SHAPE_EAST == null) {
                    SHAPE_EAST = Stream.of(
                            Block.box(15, 2, 2, 16, 14, 14),
                            Block.box(14, 5, 5, 15, 11, 11),
                            Block.box(14, 12, 12, 15, 13, 13),
                            Block.box(14, 3, 12, 15, 4, 13),
                            Block.box(14, 3, 3, 15, 4, 4),
                            Block.box(14, 12, 3, 15, 13, 4)
                    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
                }
                return SHAPE_EAST;
            }
            case SOUTH -> {
                if(SHAPE_SOUTH == null) {
                    SHAPE_SOUTH = Stream.of(
                            Block.box(2, 2, 15, 14, 14, 16),
                            Block.box(5, 5, 14, 11, 11, 15),
                            Block.box(3, 12, 14, 4, 13, 15),
                            Block.box(3, 3, 14, 4, 4, 15),
                            Block.box(12, 3, 14, 13, 4, 15),
                            Block.box(12, 12, 14, 13, 13, 15)
                    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
                }
                return SHAPE_SOUTH;
            }
            case WEST -> {
                if(SHAPE_WEST == null) {
                    SHAPE_WEST = Stream.of(
                            Block.box(0, 2, 2, 1, 14, 14),
                            Block.box(1, 5, 5, 2, 11, 11),
                            Block.box(1, 12, 3, 2, 13, 4),
                            Block.box(1, 3, 3, 2, 4, 4),
                            Block.box(1, 3, 12, 2, 4, 13),
                            Block.box(1, 12, 12, 2, 13, 13)
                    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
                }
                return SHAPE_WEST;
            }
            case UP -> {
                if(SHAPE_UP == null) {
                    SHAPE_UP = Stream.of(
                            Block.box(2, 15, 2, 14, 16, 14),
                            Block.box(5, 14, 5, 11, 15, 11),
                            Block.box(12, 14, 3, 13, 15, 4),
                            Block.box(3, 14, 3, 4, 15, 4),
                            Block.box(3, 14, 12, 4, 15, 13),
                            Block.box(12, 14, 12, 13, 15, 13)
                    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
                }
                return SHAPE_UP;
            }
            case DOWN -> {
                if(SHAPE_DOWN == null) {
                    SHAPE_DOWN = Stream.of(
                            Block.box(2, 0, 2, 14, 1, 14),
                            Block.box(5, 1, 5, 11, 2, 11),
                            Block.box(12, 1, 3, 13, 2, 4),
                            Block.box(12, 1, 12, 13, 2, 13),
                            Block.box(3, 1, 12, 4, 2, 13),
                            Block.box(3, 1, 3, 4, 2, 4)
                    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
                }
                return SHAPE_DOWN;
            }
        }
        return Shapes.block();
    }

        @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return Shapes.block();
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return Shapes.create(AABB.ofSize(pPos.getCenter(), 2, 2, 2));
    }
}
