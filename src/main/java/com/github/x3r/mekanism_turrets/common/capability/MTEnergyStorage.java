package com.github.x3r.mekanism_turrets.common.capability;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.UnknownNullability;

public class MTEnergyStorage implements IEnergyStorage, INBTSerializable<CompoundTag> {

    public static final String TAG_KEY = "EnergyStorage";
    private int maxReceive;
    private int maxExtract;
    private int maxEnergy;
    private int energy = 0;

    public MTEnergyStorage(int maxReceive, int maxExtract, int maxEnergy) {
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
        this.maxEnergy = maxEnergy;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int amount = Math.min(Math.min(this.maxReceive, maxReceive), getMaxEnergyStored() - getEnergyStored());
        if(!simulate) {
            energy += amount;
        }
        return amount;
    }

    public int forceReceiveEnergy(int maxReceive, boolean simulate) {
        int amount = Math.min(maxReceive, getMaxEnergyStored() - getEnergyStored());
        if(!simulate) {
            energy += amount;
        }
        return amount;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int amount = Math.min(Math.min(this.maxExtract, maxExtract), getEnergyStored());
        if(!simulate) {
            energy -= amount;
        }
        return amount;
    }

    public int forceExtractEnergy(int maxExtract, boolean simulate) {
        int amount = Math.min(maxExtract, getEnergyStored());
        if(!simulate) {
            energy -= amount;
        }
        return amount;
    }

    @Override
    public int getEnergyStored() {
        return energy;
    }

    public void setEnergyStored(int energy) {
        this.energy = Math.min(this.getMaxEnergyStored(), energy);
    }

    @Override
    public int getMaxEnergyStored() {
        return maxEnergy;
    }

    @Override
    public boolean canExtract() {
        return maxExtract > 0;
    }

    @Override
    public boolean canReceive() {
        return maxReceive > 0;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("MaxReceive", maxReceive);
        tag.putInt("MaxExtract", maxExtract);
        tag.putInt("MaxEnergy", maxEnergy);
        tag.putInt("Energy", energy);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        CompoundTag tag = nbt.getCompound(MTEnergyStorage.TAG_KEY);
        this.maxReceive = tag.getInt("MaxReceive");
        this.maxExtract = tag.getInt("MaxExtract");
        this.maxEnergy = tag.getInt("MaxEnergy");
        this.energy = tag.getInt("Energy");
    }
}
