package com.github.x3r.mekanism_turrets.common.registry;

import com.github.x3r.mekanism_turrets.MekanismTurrets;
import com.github.x3r.mekanism_turrets.common.block.ElectricFenceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockEntityRegistry {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MekanismTurrets.MOD_ID);

    public static final RegistryObject<BlockEntityType<ElectricFenceBlockEntity>> ELECTRIC_FENCE = BLOCK_ENTITIES.register("electric_fence",
            () -> BlockEntityType.Builder.of(ElectricFenceBlockEntity::new, BlockRegistry.ELECTRIC_FENCE.get()).build(null));

}
