package com.github.x3r.mekanism_turrets.common.block;

import com.github.x3r.mekanism_turrets.MekanismTurrets;
import com.github.x3r.mekanism_turrets.common.block_entity.ElectricFenceBlockEntity;
import com.github.x3r.mekanism_turrets.common.registry.BlockEntityTypeRegistry;
import com.github.x3r.mekanism_turrets.common.registry.DamageTypeRegistry;
import mekanism.common.block.interfaces.IHasTileEntity;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.Nullable;

public class ElectricFenceBlock extends IronBarsBlock implements IHasTileEntity<ElectricFenceBlockEntity> {
    public ElectricFenceBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
        pLevel.getBlockEntity(pPos, BlockEntityTypeRegistry.ELECTRIC_FENCE.get()).ifPresent(blockEntity -> {
            blockEntity.getCapability(ForgeCapabilities.ENERGY).ifPresent(energyStorage -> {
                int total = energyStorage.getEnergyStored();
                if(total > 0 && pEntity instanceof LivingEntity && pEntity.isAlive()) {
                    float ratio = (float) energyStorage.getEnergyStored() / energyStorage.getMaxEnergyStored();
                    boolean b = pEntity.hurt(new DamageTypeRegistry(pLevel.registryAccess()).electricFence(), ratio * 10.0F);
                    if(b) {
                        energyStorage.extractEnergy((int) (energyStorage.getEnergyStored() * 0.1F), false);
                    }
                }
            });
        });
    }

    @Override
    public void stepOn(Level pLevel, BlockPos pPos, BlockState pState, Entity pEntity) {
        entityInside(pState, pLevel, pPos, pEntity);
    }

    @Override
    public TileEntityTypeRegistryObject<? extends ElectricFenceBlockEntity> getTileType() {
        return BlockEntityTypeRegistry.ELECTRIC_FENCE;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pLevel.isClientSide() ? null : BaseEntityBlock.createTickerHelper(pBlockEntityType, BlockEntityTypeRegistry.ELECTRIC_FENCE.get(), ElectricFenceBlockEntity::serverTick);
    }


}
