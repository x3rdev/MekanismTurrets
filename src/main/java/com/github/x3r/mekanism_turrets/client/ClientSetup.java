package com.github.x3r.mekanism_turrets.client;

import com.github.x3r.mekanism_turrets.MekanismTurrets;
import com.github.x3r.mekanism_turrets.client.gui.LaserTurretScreen;
import com.github.x3r.mekanism_turrets.client.renderer.LaserRenderer;
import com.github.x3r.mekanism_turrets.client.renderer.LaserTurretRenderer;
import com.github.x3r.mekanism_turrets.common.registry.BlockEntityTypeRegistry;
import com.github.x3r.mekanism_turrets.common.registry.ContainerTypeRegistry;
import com.github.x3r.mekanism_turrets.common.registry.EntityRegistry;
import mekanism.client.ClientRegistrationUtil;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = MekanismTurrets.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientSetup {

    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(BlockEntityTypeRegistry.BASIC_LASER_TURRET.get(), pContext -> new LaserTurretRenderer());
        event.registerBlockEntityRenderer(BlockEntityTypeRegistry.ADVANCED_LASER_TURRET.get(), pContext -> new LaserTurretRenderer());
        event.registerBlockEntityRenderer(BlockEntityTypeRegistry.ELITE_LASER_TURRET.get(), pContext -> new LaserTurretRenderer());
        event.registerBlockEntityRenderer(BlockEntityTypeRegistry.ULTIMATE_LASER_TURRET.get(), pContext -> new LaserTurretRenderer());
        event.registerEntityRenderer(EntityRegistry.LASER.get(), LaserRenderer::new);
    }

    @SubscribeEvent
    public static void registerContainers(RegisterMenuScreensEvent event) {
        ClientRegistrationUtil.registerScreen(event, ContainerTypeRegistry.LASER_TURRET, LaserTurretScreen::new);
    }
}
