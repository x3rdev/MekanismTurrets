package com.github.x3r.mekanism_turrets.common.registry;

import com.github.x3r.mekanism_turrets.MekanismTurrets;
import com.github.x3r.mekanism_turrets.common.block.BasicLaserTurretBlock;
import com.github.x3r.mekanism_turrets.common.block.ElectricFenceBlock;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockRegistry {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MekanismTurrets.MOD_ID);

    public static final RegistryObject<Block> ELECTRIC_FENCE = BLOCKS.register("electric_fence",
            () -> new ElectricFenceBlock(BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL).noOcclusion()));
    public static final RegistryObject<Block> BASIC_LASER_TURRET = BLOCKS.register("basic_laser_turret",
            () -> new BasicLaserTurretBlock(BlockBehaviour.Properties.of().strength(5.0F, 6.0F).sound(SoundType.METAL).noOcclusion()));
}
