package com.github.x3r.mekanism_turrets.common.registry;

import com.github.x3r.mekanism_turrets.MekanismTurrets;
import com.github.x3r.mekanism_turrets.common.block_entity.BasicLaserTurretBlockEntity;
import com.github.x3r.mekanism_turrets.common.block_entity.ElectricFenceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockEntityRegistry {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MekanismTurrets.MOD_ID);

    public static final RegistryObject<BlockEntityType<ElectricFenceBlockEntity>> ELECTRIC_FENCE = BLOCK_ENTITIES.register("electric_fence",
            () -> BlockEntityType.Builder.of(ElectricFenceBlockEntity::new, BlockRegistry.ELECTRIC_FENCE.get()).build(null));
    public static final RegistryObject<BlockEntityType<BasicLaserTurretBlockEntity>> BASIC_LASER_TURRET = BLOCK_ENTITIES.register("basic_laser_turret",
            () -> BlockEntityType.Builder.of(BasicLaserTurretBlockEntity::new, BlockRegistry.BASIC_LASER_TURRET.get()).build(null));
}
