package com.github.x3r.mekanism_turrets.client;

import com.github.x3r.mekanism_turrets.MekanismTurrets;
import com.github.x3r.mekanism_turrets.client.gui.LaserTurretScreen;
import com.github.x3r.mekanism_turrets.client.renderer.LaserRenderer;
import com.github.x3r.mekanism_turrets.client.renderer.LaserTurretRenderer;
import com.github.x3r.mekanism_turrets.common.block_entity.LaserTurretBlockEntity;
import com.github.x3r.mekanism_turrets.common.registry.BlockEntityTypeRegistry;
import com.github.x3r.mekanism_turrets.common.registry.ContainerTypeRegistry;
import com.github.x3r.mekanism_turrets.common.registry.EntityRegistry;
import mekanism.client.ClientRegistrationUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegisterEvent;
import software.bernie.geckolib.network.SerializableDataTicket;
import software.bernie.geckolib.util.GeckoLibUtil;

@Mod.EventBusSubscriber(modid = MekanismTurrets.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
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
    public static void registerContainers(RegisterEvent event) {
        event.register(Registries.MENU, helper -> {
            ClientRegistrationUtil.registerScreen(ContainerTypeRegistry.LASER_TURRET, LaserTurretScreen::new);
        });
    }
}
