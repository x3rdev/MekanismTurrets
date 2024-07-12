package com.github.x3r.mekanism_turrets.common.block_entity;

import mekanism.common.tile.component.ITileComponent;
import mekanism.common.upgrade.IUpgradeData;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

import java.util.List;
import java.util.Objects;

public final class LaserTurretUpgradeData implements IUpgradeData {
    private final boolean targetsHostile;
    private final boolean targetsPassive;
    private final boolean targetsPlayers;
    private final boolean targetsTrusted;
    private final CompoundTag components;

    public LaserTurretUpgradeData(boolean targetsHostile, boolean targetsPassive, boolean targetsPlayers, boolean targetsTrusted, List<ITileComponent> components, HolderLookup.Provider provider) {
        this.targetsHostile = targetsHostile;
        this.targetsPassive = targetsPassive;
        this.targetsPlayers = targetsPlayers;
        this.targetsTrusted = targetsTrusted;
        this.components = new CompoundTag();
        for (ITileComponent component : components) {
            component.write(this.components, provider);
        }
    }

    public boolean targetsHostile() {
        return targetsHostile;
    }

    public boolean targetsPassive() {
        return targetsPassive;
    }

    public boolean targetsPlayers() {
        return targetsPlayers;
    }

    public boolean targetsTrusted() {
        return targetsTrusted;
    }

    public CompoundTag components() {
        return components;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (LaserTurretUpgradeData) obj;
        return this.targetsHostile == that.targetsHostile &&
                this.targetsPassive == that.targetsPassive &&
                this.targetsPlayers == that.targetsPlayers &&
                this.targetsTrusted == that.targetsTrusted &&
                Objects.equals(this.components, that.components);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetsHostile, targetsPassive, targetsPlayers, targetsTrusted, components);
    }

    @Override
    public String toString() {
        return "LaserTurretUpgradeData[" +
                "targetsHostile=" + targetsHostile + ", " +
                "targetsPassive=" + targetsPassive + ", " +
                "targetsPlayers=" + targetsPlayers + ", " +
                "targetsTrusted=" + targetsTrusted + ", " +
                "components=" + components + ']';
    }


}
