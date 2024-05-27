package com.github.x3r.mekanism_turrets.common.registry;

import com.github.x3r.mekanism_turrets.MekanismTurrets;
import com.github.x3r.mekanism_turrets.common.block.ElectricFenceBlock;
import com.github.x3r.mekanism_turrets.common.block.LaserTurretBlock;
import com.github.x3r.mekanism_turrets.common.block_entity.LaserTurretBlockEntity;
import com.github.x3r.mekanism_turrets.common.item.LaserTurretBlockItem;
import mekanism.api.tier.ITier;
import mekanism.common.block.attribute.AttributeTier;
import mekanism.common.content.blocktype.BlockType;
import mekanism.common.content.blocktype.BlockTypeTile;
import mekanism.common.registration.impl.BlockDeferredRegister;
import mekanism.common.registration.impl.BlockRegistryObject;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Function;
import java.util.function.Supplier;

public class BlockRegistry {

    public static final BlockDeferredRegister BLOCKS = new BlockDeferredRegister(MekanismTurrets.MOD_ID);

    public static final BlockRegistryObject<ElectricFenceBlock, BlockItem> ELECTRIC_FENCE = BLOCKS.register("electric_fence",
            () -> new ElectricFenceBlock(BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL).noOcclusion()));

    public static final BlockRegistryObject<LaserTurretBlock, LaserTurretBlockItem> BASIC_LASER_TURRET = registerLaserTurret(BlockTypeRegistry.BASIC_LASER_TURRET);

    private static BlockRegistryObject<LaserTurretBlock, LaserTurretBlockItem> registerLaserTurret(BlockTypeTile<LaserTurretBlockEntity> type) {
        return registerTieredBlock(type, "_laser_turret", () -> new LaserTurretBlock(type), LaserTurretBlockItem::new);
    }

    private static <BLOCK extends Block, ITEM extends BlockItem> BlockRegistryObject<BLOCK, ITEM> registerTieredBlock(BlockType type, String suffix,
                                                                                                                      Supplier<? extends BLOCK> blockSupplier, Function<BLOCK, ITEM> itemCreator) {
        return registerTieredBlock(type.get(AttributeTier.class).tier(), suffix, blockSupplier, itemCreator);
    }

    private static <BLOCK extends Block, ITEM extends BlockItem> BlockRegistryObject<BLOCK, ITEM> registerTieredBlock(ITier tier, String suffix,
                                                                                                                      Supplier<? extends BLOCK> blockSupplier, Function<BLOCK, ITEM> itemCreator) {
        return BLOCKS.register(tier.getBaseTier().getLowerName() + suffix, blockSupplier, itemCreator);
    }
}
