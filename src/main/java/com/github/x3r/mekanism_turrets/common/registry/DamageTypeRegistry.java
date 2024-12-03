package com.github.x3r.mekanism_turrets.common.registry;

import com.github.x3r.mekanism_turrets.MekanismTurrets;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;

public class DamageTypeRegistry {

    private final Registry<DamageType> damageTypes;
    private final DamageSource electricFence;
    private final DamageSource laser;

    public DamageTypeRegistry(RegistryAccess registryAccess) {
        this.damageTypes = registryAccess.registryOrThrow(Registries.DAMAGE_TYPE);
        this.electricFence = source(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(MekanismTurrets.MOD_ID, "electric_fence")));
        this.laser = source(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(MekanismTurrets.MOD_ID, "laser")));
    }
    public DamageTypeRegistry(RegistryAccess registryAccess,Entity pCausingEntity) {
        this.damageTypes = registryAccess.registryOrThrow(Registries.DAMAGE_TYPE);
        this.electricFence = source(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(MekanismTurrets.MOD_ID, "electric_fence")),pCausingEntity);
        this.laser = source(ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(MekanismTurrets.MOD_ID, "laser")),pCausingEntity);
    }

    private DamageSource source(ResourceKey<DamageType> pDamageTypeKey) {
        return new DamageSource(this.damageTypes.getHolderOrThrow(pDamageTypeKey));
    }
    private DamageSource source(ResourceKey<DamageType> pDamageTypeKey, Entity pCausingEntity){
        return new DamageSource(this.damageTypes.getHolderOrThrow(pDamageTypeKey),pCausingEntity);
    }


    public DamageSource electricFence() {
        return this.electricFence;
    }
    public DamageSource laser() {
        return this.laser;
    }
}
