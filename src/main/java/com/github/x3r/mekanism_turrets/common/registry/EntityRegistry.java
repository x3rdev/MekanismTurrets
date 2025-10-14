package com.github.x3r.mekanism_turrets.common.registry;

import com.github.x3r.mekanism_turrets.MekanismTurrets;
import com.github.x3r.mekanism_turrets.common.entity.LaserEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EntityRegistry {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, MekanismTurrets.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<LaserEntity>> LASER = ENTITY_TYPES.register("laser",
            () -> EntityType.Builder.<LaserEntity>of(LaserEntity::new, MobCategory.MISC)
                    .sized(0.75F, 0.75F)
                    .noSave()
                    .clientTrackingRange(8)
                    .updateInterval(3)
                    .build(ResourceLocation.fromNamespaceAndPath(MekanismTurrets.MOD_ID, "laser").toString()));
}
