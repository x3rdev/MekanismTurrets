package com.github.x3r.mekanism_turrets.common.block_entity;

import com.github.x3r.mekanism_turrets.common.capability.MTEnergyStorage;
import com.github.x3r.mekanism_turrets.common.registry.BlockEntityTypeRegistry;
import com.github.x3r.mekanism_turrets.common.registry.BlockRegistry;
import mekanism.common.tile.base.TileEntityMekanism;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.Optional;

public class ElectricFenceBlockEntity extends TileEntityMekanism {

    public final MTEnergyStorage energyStorage = new MTEnergyStorage(1000, 1000, 10000);
    public ElectricFenceBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockRegistry.ELECTRIC_FENCE, pPos, pBlockState);
    }

    public static void serverTick(Level pLevel, BlockPos pPos, BlockState pState, ElectricFenceBlockEntity blockEntity) {
        for (Direction dir : Direction.values()) {
            Optional<ElectricFenceBlockEntity> otherBlockEntity = pLevel.getBlockEntity(pPos.relative(dir), BlockEntityTypeRegistry.ELECTRIC_FENCE.get());
            otherBlockEntity.ifPresent(otherFence -> {
                IEnergyStorage otherEnergyStorage = pLevel.getCapability(Capabilities.EnergyStorage.BLOCK, otherFence.getBlockPos(), dir);
                if(blockEntity.energyStorage.getEnergyStored() > otherEnergyStorage.getEnergyStored() &&
                        otherEnergyStorage.getEnergyStored() < otherEnergyStorage.getMaxEnergyStored()) {
                    int a = blockEntity.energyStorage.extractEnergy(750, true);
                    int b = otherEnergyStorage.receiveEnergy(750, true);
                    blockEntity.energyStorage.extractEnergy(otherEnergyStorage.receiveEnergy(Math.min(a, b), false), false);
                }
            });
        }
        blockEntity.markUpdated();
    }

    protected void markUpdated() {
        this.setChanged();
        this.getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
    }
}
