package com.github.x3r.mekanism_turrets;

import com.github.x3r.mekanism_turrets.common.block_entity.ElectricFenceBlockEntity;
import com.github.x3r.mekanism_turrets.common.capability.MTEnergyStorage;
import com.github.x3r.mekanism_turrets.common.packet.MekanismTurretsPacketHandler;
import com.github.x3r.mekanism_turrets.common.registry.*;
import com.github.x3r.mekanism_turrets.common.scheduler.Scheduler;
import com.mojang.logging.LogUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(MekanismTurrets.MOD_ID)
public class MekanismTurrets {

    public static final String MOD_ID = "mekanism_turrets";
    private static final Logger LOGGER = LogUtils.getLogger();

    public MekanismTurrets(IEventBus modEventBus, Dist dist, ModContainer container) {
        IEventBus neoEventBus = NeoForge.EVENT_BUS;

        BlockRegistry.BLOCKS.register(modEventBus);
        BlockEntityTypeRegistry.BLOCK_ENTITY_TYPES.register(modEventBus);
        ContainerTypeRegistry.CONTAINER_TYPES.register(modEventBus);
        EntityRegistry.ENTITY_TYPES.register(modEventBus);
        ItemRegistry.ITEMS.register(modEventBus);
        ItemRegistry.ModItemTab.CREATIVE_MODE_TABS.register(modEventBus);
        SoundRegistry.SOUNDS.register(modEventBus);

        container.registerConfig(ModConfig.Type.COMMON, MekanismTurretsConfig.SPEC);

        modEventBus.addListener(this::registerCapabilities);
        modEventBus.addListener(MekanismTurretsPacketHandler::registerPayloadHandler);
    }

    @SubscribeEvent
    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlock(
                Capabilities.EnergyStorage.BLOCK,
                (level, pos, state, blockEntity, context) -> ((ElectricFenceBlockEntity) blockEntity).energyStorage,
                BlockRegistry.ELECTRIC_FENCE.getBlock());
    }
}
