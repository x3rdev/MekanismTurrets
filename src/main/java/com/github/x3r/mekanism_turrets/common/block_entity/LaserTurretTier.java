package com.github.x3r.mekanism_turrets.common.block_entity;

import mekanism.api.tier.BaseTier;
import mekanism.api.tier.ITier;
import mekanism.common.config.value.CachedFloatValue;
import mekanism.common.config.value.CachedIntValue;

public enum LaserTurretTier implements ITier {
    BASIC(BaseTier.BASIC, 80, 1, 10000),
    ADVANCED(BaseTier.ADVANCED, 65, 2, 40000),
    ELITE(BaseTier.ELITE, 50, 3, 90000),
    ULTIMATE(BaseTier.ULTIMATE, 35, 4, 160000);

    private final BaseTier baseTier;
    private final int cooldown;
    private final float damage;
    private final int energyCapacity;
    private CachedIntValue cooldownReference;
    private CachedFloatValue damageReference;
    private CachedIntValue energyCapacityReference;

    LaserTurretTier(BaseTier baseTier, int cooldown, float damage, int energyCapacity) {
        this.baseTier = baseTier;
        this.cooldown = cooldown;
        this.damage = damage;
        this.energyCapacity = energyCapacity;
    }

    @Override
    public BaseTier getBaseTier() {
        return baseTier;
    }

    public int getCooldown() {
        return cooldownReference == null ? getBaseCooldown() : cooldownReference.getOrDefault();
    }
    private int getBaseCooldown() {
        return cooldown;
    }

    public float getDamage() {
        return damageReference == null ? getBaseDamage() : damageReference.getOrDefault();
    }
    private float getBaseDamage() {
        return damage;
    }

    public int getEnergyCapacity() {
        return energyCapacityReference == null ? getBaseEnergyCapacity() : energyCapacityReference.getOrDefault();
    }

    private int getBaseEnergyCapacity() {
        return energyCapacity;
    }

    public void setConfigReference(CachedIntValue cooldownReference, CachedFloatValue damageReference) {
        this.cooldownReference = cooldownReference;
        this.damageReference = damageReference;
    }

}
