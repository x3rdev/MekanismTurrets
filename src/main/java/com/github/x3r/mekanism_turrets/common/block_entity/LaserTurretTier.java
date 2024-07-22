package com.github.x3r.mekanism_turrets.common.block_entity;

import com.github.x3r.mekanism_turrets.MekanismTurretsConfig;
import mekanism.api.tier.BaseTier;
import mekanism.api.tier.ITier;
import mekanism.common.config.value.CachedFloatValue;
import mekanism.common.config.value.CachedIntValue;

public enum LaserTurretTier implements ITier {
    BASIC(BaseTier.BASIC, 80, 1, 10000, 15),
    ADVANCED(BaseTier.ADVANCED, 65, 2, 40000, 20),
    ELITE(BaseTier.ELITE, 50, 3, 90000, 25),
    ULTIMATE(BaseTier.ULTIMATE, 35, 4, 160000, 30);

    private final BaseTier baseTier;
    private final int cooldown;
    private final double damage;
    private final int energyCapacity;
    private final double range;
    private Integer cooldownReference;
    private Double damageReference;
    private Integer energyCapacityReference;
    private Double rangeReference;

    LaserTurretTier(BaseTier baseTier, int cooldown, double damage, int energyCapacity, double range) {
        this.baseTier = baseTier;
        this.cooldown = cooldown;
        this.damage = damage;
        this.energyCapacity = energyCapacity;
        this.range = range;
    }

    @Override
    public BaseTier getBaseTier() {
        return baseTier;
    }

    public int getCooldown() {
        return cooldownReference == null ? getBaseCooldown() : cooldownReference;
    }
    private int getBaseCooldown() {
        return cooldown;
    }

    public double getDamage() {
        return damageReference == null ? getBaseDamage() : damageReference;
    }
    private double getBaseDamage() {
        return damage;
    }

    public int getEnergyCapacity() {
        return energyCapacityReference == null ? getBaseEnergyCapacity() : energyCapacityReference;
    }

    private int getBaseEnergyCapacity() {
        return energyCapacity;
    }

    public double getRange() {
        return rangeReference == null ? getBaseRange() : rangeReference;
    }

    private double getBaseRange() {
        return range;
    }

    public void setConfigReference(int cooldownReference, double damageReference, int energyCapacityReference, double rangeReference) {
        this.cooldownReference = cooldownReference;
        this.damageReference = damageReference;
        this.energyCapacityReference = energyCapacityReference;
        this.rangeReference = rangeReference;
    }

}
