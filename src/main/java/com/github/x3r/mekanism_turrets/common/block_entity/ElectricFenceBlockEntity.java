package com.github.x3r.mekanism_turrets.common.block_entity;

import com.github.x3r.mekanism_turrets.common.capability.MTEnergyStorage;
import com.github.x3r.mekanism_turrets.common.registry.BlockEntityTypeRegistry;
import com.github.x3r.mekanism_turrets.common.registry.BlockRegistry;
import mekanism.common.tile.base.TileEntityMekanism;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ElectricFenceBlockEntity extends TileEntityMekanism {
    private final LazyOptional<MTEnergyStorage> energyStorageLazyOptional = LazyOptional.of(() -> new MTEnergyStorage(1000, 1000, 10000));

    public ElectricFenceBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockRegistry.ELECTRIC_FENCE, pPos, pBlockState);
    }

    public static void serverTick(Level pLevel, BlockPos pPos, BlockState pState, ElectricFenceBlockEntity blockEntity) {
        for (Direction dir : Direction.values()) {
            blockEntity.getCapability(ForgeCapabilities.ENERGY).ifPresent(ownStorage -> {
                Optional<ElectricFenceBlockEntity> otherBlockEntity = pLevel.getBlockEntity(pPos.relative(dir), BlockEntityTypeRegistry.ELECTRIC_FENCE.get());
                otherBlockEntity.ifPresent(electricFenceBlockEntity -> electricFenceBlockEntity.getCapability(ForgeCapabilities.ENERGY, dir.getOpposite()).ifPresent(otherStorage -> {
                    if(ownStorage.getEnergyStored() > otherStorage.getEnergyStored() &&
                            otherStorage.getEnergyStored() < otherStorage.getMaxEnergyStored()) {
                        int a = ownStorage.extractEnergy(750, true);
                        int b = otherStorage.receiveEnergy(750, true);
                        ownStorage.extractEnergy(otherStorage.receiveEnergy(Math.min(a, b), false), false);
                    }
                }));
            });
        }
        blockEntity.markUpdated();
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        energyStorageLazyOptional.ifPresent(energyStorage -> energyStorage.deserializeNBT(tag));
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        energyStorageLazyOptional.ifPresent(energyStorage -> tag.put(MTEnergyStorage.TAG_KEY, energyStorage.serializeNBT()));
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap.equals(ForgeCapabilities.ENERGY)) {
            return energyStorageLazyOptional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        energyStorageLazyOptional.invalidate();
    }

    protected void markUpdated() {
        this.setChanged();
        this.getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
    }
}
