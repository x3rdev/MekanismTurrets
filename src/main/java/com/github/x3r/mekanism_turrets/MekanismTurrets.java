package com.github.x3r.mekanism_turrets;

import com.github.x3r.mekanism_turrets.common.block_entity.ElectricFenceBlockEntity;
import com.github.x3r.mekanism_turrets.common.block_entity.LaserTurretBlockEntity;
import com.github.x3r.mekanism_turrets.common.block_entity.LaserTurretTier;
import com.github.x3r.mekanism_turrets.common.capability.MTEnergyStorage;
import com.github.x3r.mekanism_turrets.common.entity.LaserEntity;
import com.github.x3r.mekanism_turrets.common.packet.MekanismTurretsPacketHandler;
import com.github.x3r.mekanism_turrets.common.registry.*;
import com.github.x3r.mekanism_turrets.common.scheduler.Scheduler;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;
import software.bernie.geckolib.constant.dataticket.SerializableDataTicket;
import software.bernie.geckolib.util.GeckoLibUtil;

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
        modEventBus.addListener(this::loadConfig);
        modEventBus.addListener(MekanismTurretsPacketHandler::registerPayloadHandler);

        neoEventBus.addListener(LaserEntity::enterChunk);

        registerDataTickets();
    }

    @SubscribeEvent
    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlock(
                Capabilities.EnergyStorage.BLOCK,
                (level, pos, state, blockEntity, context) -> ((ElectricFenceBlockEntity) blockEntity).energyStorage,
                BlockRegistry.ELECTRIC_FENCE.get());
    }

    @SubscribeEvent
    public void loadConfig(ModConfigEvent.Loading event) {
        LaserTurretTier.BASIC.setConfigReference(MekanismTurretsConfig.basicLaserTurretCooldown, MekanismTurretsConfig.basicLaserTurretDamage, MekanismTurretsConfig.basicLaserTurretEnergyCapacity, MekanismTurretsConfig.basicLaserTurretRange);
        LaserTurretTier.ADVANCED.setConfigReference(MekanismTurretsConfig.advancedLaserTurretCooldown, MekanismTurretsConfig.advancedLaserTurretDamage, MekanismTurretsConfig.advancedLaserTurretEnergyCapacity, MekanismTurretsConfig.advancedLaserTurretRange);
        LaserTurretTier.ELITE.setConfigReference(MekanismTurretsConfig.eliteLaserTurretCooldown, MekanismTurretsConfig.eliteLaserTurretDamage, MekanismTurretsConfig.eliteLaserTurretEnergyCapacity, MekanismTurretsConfig.eliteLaserTurretRange);
        LaserTurretTier.ULTIMATE.setConfigReference(MekanismTurretsConfig.ultimateLaserTurretCooldown, MekanismTurretsConfig.ultimateLaserTurretDamage, MekanismTurretsConfig.ultimateLaserTurretEnergyCapacity, MekanismTurretsConfig.ultimateLaserTurretRange);
    }

    private void registerDataTickets() {
        LaserTurretBlockEntity.HAS_TARGET = GeckoLibUtil.addDataTicket(SerializableDataTicket.ofBoolean(ResourceLocation.fromNamespaceAndPath(MekanismTurrets.MOD_ID, "has_target")));
        LaserTurretBlockEntity.TARGET_POS_X = GeckoLibUtil.addDataTicket(SerializableDataTicket.ofDouble(ResourceLocation.fromNamespaceAndPath(MekanismTurrets.MOD_ID, "target_pos_x")));
        LaserTurretBlockEntity.TARGET_POS_Y = GeckoLibUtil.addDataTicket(SerializableDataTicket.ofDouble(ResourceLocation.fromNamespaceAndPath(MekanismTurrets.MOD_ID, "target_pos_y")));
        LaserTurretBlockEntity.TARGET_POS_Z = GeckoLibUtil.addDataTicket(SerializableDataTicket.ofDouble(ResourceLocation.fromNamespaceAndPath(MekanismTurrets.MOD_ID, "target_pos_z")));
    }
}
