package com.github.x3r.mekanism_turrets.common.registry;

import com.github.x3r.mekanism_turrets.MekanismTurrets;
import com.github.x3r.mekanism_turrets.common.item.GeckoBlockItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockItemRegistry {

    public static final DeferredRegister<Item> BLOCK_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MekanismTurrets.MOD_ID);

     public static final RegistryObject<Item> ELECTRIC_FENCE_BLOCK = BLOCK_ITEMS.register("electric_fence",
             () -> new BlockItem(BlockRegistry.ELECTRIC_FENCE.get(), new Item.Properties()));
     public static final RegistryObject<Item> BASIC_LASER_TURRET_BLOCK = BLOCK_ITEMS.register("basic_laser_turret",
             () -> new GeckoBlockItem(BlockRegistry.BASIC_LASER_TURRET.get(), new Item.Properties()));
}
