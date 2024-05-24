package com.github.x3r.mekanism_turrets.common.block;

import com.github.x3r.mekanism_turrets.common.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.Nullable;

public class ElectricFenceBlock extends IronBarsBlock implements EntityBlock {
    public ElectricFenceBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
        pLevel.getBlockEntity(pPos, BlockEntityRegistry.ELECTRIC_FENCE.get()).ifPresent(blockEntity -> {
            blockEntity.getCapability(ForgeCapabilities.ENERGY).ifPresent(energyStorage -> {
                energyStorage.receiveEnergy(100, false);
            });
        });
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ElectricFenceBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pLevel.isClientSide() ? null : BaseEntityBlock.createTickerHelper(pBlockEntityType, BlockEntityRegistry.ELECTRIC_FENCE.get(), ElectricFenceBlockEntity::serverTick);
    }


}
