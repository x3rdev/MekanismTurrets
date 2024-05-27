package com.github.x3r.mekanism_turrets.common.registry;

import com.github.x3r.mekanism_turrets.MekanismTurrets;
import com.github.x3r.mekanism_turrets.common.block_entity.LaserTurretBlockEntity;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.registration.impl.ContainerTypeDeferredRegister;
import mekanism.common.registration.impl.ContainerTypeRegistryObject;
import mekanism.common.tile.TileEntityFluidTank;

public class ContainerTypeRegistry {

    public static final ContainerTypeDeferredRegister CONTAINER_TYPES = new ContainerTypeDeferredRegister(MekanismTurrets.MOD_ID);

    public static final ContainerTypeRegistryObject<MekanismTileContainer<LaserTurretBlockEntity>> LASER_TURRET = CONTAINER_TYPES.custom("laser_turret", LaserTurretBlockEntity.class).build();

}
