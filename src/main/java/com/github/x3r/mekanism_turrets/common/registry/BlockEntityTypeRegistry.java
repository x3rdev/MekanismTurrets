package com.github.x3r.mekanism_turrets.common.registry;

import com.github.x3r.mekanism_turrets.MekanismTurrets;
import com.github.x3r.mekanism_turrets.common.block_entity.ElectricFenceBlockEntity;
import mekanism.common.registration.impl.TileEntityTypeDeferredRegister;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;

public class BlockEntityTypeRegistry {

    public static final TileEntityTypeDeferredRegister BLOCK_ENTITY_TYPES = new TileEntityTypeDeferredRegister(MekanismTurrets.MOD_ID);
    public static final TileEntityTypeRegistryObject<ElectricFenceBlockEntity> ELECTRIC_FENCE = BLOCK_ENTITY_TYPES.register(BlockRegistry.ELECTRIC_FENCE, ElectricFenceBlockEntity::new);

}
