package com.github.x3r.mekanism_turrets.common.block_entity;

import mekanism.api.tier.BaseTier;
import mekanism.api.tier.ITier;
import mekanism.common.config.value.CachedFloatValue;
import mekanism.common.config.value.CachedIntValue;

public enum LaserTurretTier implements ITier {
    BASIC(BaseTier.BASIC, 4*20, 1),
    ADVANCED(BaseTier.ADVANCED, 3*20, 2),
    ELITE(BaseTier.ELITE, 2*20, 3),
    ULTIMATE(BaseTier.ULTIMATE, 20, 4);

    private final BaseTier baseTier;
    private final int cooldown;
    private final float damage;

    private CachedIntValue cooldownReference;
    private CachedFloatValue damageReference;

    LaserTurretTier(BaseTier baseTier, int cooldown, float damage) {
        this.baseTier = baseTier;
        this.cooldown = cooldown;
        this.damage = damage;
    }

    @Override
    public BaseTier getBaseTier() {
        return baseTier;
    }

    public int getCooldown() {
        return cooldownReference == null ? getBaseCooldown() : cooldownReference.getOrDefault();
    }
    public int getBaseCooldown() {
        return cooldown;
    }

    public float getDamage() {
        return damageReference == null ? getBaseDamage() : damageReference.getOrDefault();
    }
    public float getBaseDamage() {
        return damage;
    }

    public void setConfigReference(CachedIntValue cooldownReference, CachedFloatValue damageReference) {
        this.cooldownReference = cooldownReference;
        this.damageReference = damageReference;
    }

}
