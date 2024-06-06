package com.github.x3r.mekanism_turrets.common.registry;

import com.github.x3r.mekanism_turrets.common.block.LaserTurretBlock;
import com.github.x3r.mekanism_turrets.common.block_entity.LaserTurretBlockEntity;
import com.github.x3r.mekanism_turrets.common.block_entity.LaserTurretTier;
import com.github.x3r.mekanism_turrets.common.lang.MekanismTurretsLang;
import mekanism.api.Upgrade;
import mekanism.api.math.FloatingLong;
import mekanism.common.block.attribute.*;
import mekanism.common.content.blocktype.BlockTypeTile;
import mekanism.common.registration.impl.BlockRegistryObject;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.EnumSet;
import java.util.function.Supplier;

public class BlockTypeRegistry {

    public static final BlockTypeTile<LaserTurretBlockEntity> BASIC_LASER_TURRET = createLaserTurret(LaserTurretTier.BASIC, () -> BlockEntityTypeRegistry.BASIC_LASER_TURRET, () -> BlockRegistry.ADVANCED_LASER_TURRET);
    public static final BlockTypeTile<LaserTurretBlockEntity> ADVANCED_LASER_TURRET = createLaserTurret(LaserTurretTier.ADVANCED, () -> BlockEntityTypeRegistry.ADVANCED_LASER_TURRET, () -> BlockRegistry.ELITE_LASER_TURRET);
    public static final BlockTypeTile<LaserTurretBlockEntity> ELITE_LASER_TURRET = createLaserTurret(LaserTurretTier.ELITE, () -> BlockEntityTypeRegistry.ELITE_LASER_TURRET, () -> BlockRegistry.ULTIMATE_LASER_TURRET);
    public static final BlockTypeTile<LaserTurretBlockEntity> ULTIMATE_LASER_TURRET = createLaserTurret(LaserTurretTier.ULTIMATE, () -> BlockEntityTypeRegistry.ULTIMATE_LASER_TURRET, null);
    private static <TILE extends LaserTurretBlockEntity> BlockTypeTile<TILE> createLaserTurret(LaserTurretTier tier, Supplier<TileEntityTypeRegistryObject<TILE>> tile, Supplier<BlockRegistryObject<?, ?>> upgradeBlock) {
        return BlockTypeTile.BlockTileBuilder.createBlock(tile, MekanismTurretsLang.DESCRIPTION_LASER_TURRET)
                .withGui(() -> ContainerTypeRegistry.LASER_TURRET)
                .withCustomShape(new VoxelShape[]{LaserTurretBlock.LASER_TURRET_SHAPE})
                .with(new AttributeTier<>(tier), new AttributeUpgradeable(upgradeBlock), Attributes.SECURITY)
                .without(AttributeParticleFX.class, AttributeStateFacing.class, Attributes.AttributeRedstone.class)
                .withEnergyConfig(
                        () -> FloatingLong.create(tier.getEnergyCapacity()))
                .withSupportedUpgrades(EnumSet.of(Upgrade.SPEED, Upgrade.ENERGY))
                .withComputerSupport(tier, "LaserTurret")
                .build();
    }

}
