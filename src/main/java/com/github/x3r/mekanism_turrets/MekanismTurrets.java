package com.github.x3r.mekanism_turrets;

import com.github.x3r.mekanism_turrets.common.block_entity.LaserTurretBlockEntity;
import com.github.x3r.mekanism_turrets.common.block_entity.LaserTurretTier;
import com.github.x3r.mekanism_turrets.common.packet.MekanismTurretsPacketHandler;
import com.github.x3r.mekanism_turrets.common.registry.*;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import software.bernie.geckolib.network.SerializableDataTicket;
import software.bernie.geckolib.util.GeckoLibUtil;

@Mod(MekanismTurrets.MOD_ID)
public class MekanismTurrets {

    public static final String MOD_ID = "mekanism_turrets";
    private static final Logger LOGGER = LogUtils.getLogger();

    public MekanismTurrets() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;

        BlockRegistry.BLOCKS.register(modEventBus);
        BlockEntityTypeRegistry.BLOCK_ENTITY_TYPES.register(modEventBus);
        ContainerTypeRegistry.CONTAINER_TYPES.register(modEventBus);
        EntityRegistry.ENTITY_TYPES.register(modEventBus);
        ItemRegistry.ITEMS.register(modEventBus);
        ItemRegistry.ModItemTab.CREATIVE_MODE_TABS.register(modEventBus);
        SoundRegistry.SOUNDS.register(modEventBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, MekanismTurretsConfig.SPEC);

        modEventBus.addListener(this::loadConfig);
        MekanismTurretsPacketHandler.registerPackets();

        registerDataTickets();
    }

    @SubscribeEvent
    public void loadConfig(ModConfigEvent.Loading event) {
        LaserTurretTier.BASIC.setConfigReference(MekanismTurretsConfig.basicLaserTurretCooldown, MekanismTurretsConfig.basicLaserTurretDamage, MekanismTurretsConfig.basicLaserTurretEnergyCapacity, MekanismTurretsConfig.basicLaserTurretRange);
        LaserTurretTier.ADVANCED.setConfigReference(MekanismTurretsConfig.advancedLaserTurretCooldown, MekanismTurretsConfig.advancedLaserTurretDamage, MekanismTurretsConfig.advancedLaserTurretEnergyCapacity, MekanismTurretsConfig.advancedLaserTurretRange);
        LaserTurretTier.ELITE.setConfigReference(MekanismTurretsConfig.eliteLaserTurretCooldown, MekanismTurretsConfig.eliteLaserTurretDamage, MekanismTurretsConfig.eliteLaserTurretEnergyCapacity, MekanismTurretsConfig.eliteLaserTurretRange);
        LaserTurretTier.ULTIMATE.setConfigReference(MekanismTurretsConfig.ultimateLaserTurretCooldown, MekanismTurretsConfig.ultimateLaserTurretDamage, MekanismTurretsConfig.ultimateLaserTurretEnergyCapacity, MekanismTurretsConfig.ultimateLaserTurretRange);
    }

    private void registerDataTickets() {
        LaserTurretBlockEntity.HAS_TARGET = GeckoLibUtil.addDataTicket(SerializableDataTicket.ofBoolean(new ResourceLocation(MekanismTurrets.MOD_ID, "has_target")));
        LaserTurretBlockEntity.TARGET_POS_X = GeckoLibUtil.addDataTicket(SerializableDataTicket.ofDouble(new ResourceLocation(MekanismTurrets.MOD_ID, "target_pos_x")));
        LaserTurretBlockEntity.TARGET_POS_Y = GeckoLibUtil.addDataTicket(SerializableDataTicket.ofDouble(new ResourceLocation(MekanismTurrets.MOD_ID, "target_pos_y")));
        LaserTurretBlockEntity.TARGET_POS_Z = GeckoLibUtil.addDataTicket(SerializableDataTicket.ofDouble(new ResourceLocation(MekanismTurrets.MOD_ID, "target_pos_z")));
    }
}
