package com.github.x3r.mekanism_turrets.common.packet;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class MekanismTurretsPacketHandler {

    private static final String PROTOCOL_VERSION = "1";
    @SubscribeEvent
    public static void registerPayloadHandler(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);
        registrar.playToServer(
                ModifyTurretTargetPayload.TYPE,
                ModifyTurretTargetPayload.STREAM_CODEC,
                ModifyTurretTargetPayload::handleServer
        );
    }
}
