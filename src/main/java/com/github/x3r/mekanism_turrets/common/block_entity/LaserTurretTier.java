package com.github.x3r.mekanism_turrets.common.block_entity;

import com.github.x3r.mekanism_turrets.MekanismTurretsConfig;
import mekanism.api.tier.BaseTier;
import mekanism.api.tier.ITier;
import mekanism.common.config.value.CachedFloatValue;
import mekanism.common.config.value.CachedIntValue;

public enum LaserTurretTier implements ITier {
    BASIC(BaseTier.BASIC,
            MekanismTurretsConfig.basicLaserTurretCooldown.get(),
            MekanismTurretsConfig.basicLaserTurretDamage.get(),
            MekanismTurretsConfig.basicLaserTurretEnergyCapacity.get()),
    ADVANCED(BaseTier.ADVANCED,
            MekanismTurretsConfig.advancedLaserTurretCooldown.get(),
            MekanismTurretsConfig.advancedLaserTurretDamage.get(),
            MekanismTurretsConfig.advancedLaserTurretEnergyCapacity.get()),
    ELITE(BaseTier.ELITE,
            MekanismTurretsConfig.eliteLaserTurretCooldown.get(),
            MekanismTurretsConfig.eliteLaserTurretDamage.get(),
            MekanismTurretsConfig.eliteLaserTurretEnergyCapacity.get()),
    ULTIMATE(BaseTier.ULTIMATE,
            MekanismTurretsConfig.ultimateLaserTurretCooldown.get(),
            MekanismTurretsConfig.ultimateLaserTurretDamage.get(),
            MekanismTurretsConfig.ultimateLaserTurretEnergyCapacity.get());

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
