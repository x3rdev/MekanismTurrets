package com.github.x3r.mekanism_turrets.common.packet;

import com.github.x3r.mekanism_turrets.MekanismTurrets;
import com.github.x3r.mekanism_turrets.common.block_entity.LaserTurretBlockEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;
import net.minecraft.network.protocol.game.ServerPacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.configuration.ICustomConfigurationTask;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.function.Consumer;

public record ModifyTurretTargetPayload(BlockPos blockEntityPos, byte index, boolean value) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ModifyTurretTargetPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(MekanismTurrets.MOD_ID, "modify_turret_target"));
    public static final StreamCodec<ByteBuf, ModifyTurretTargetPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            ModifyTurretTargetPayload::blockEntityPos,
            ByteBufCodecs.BYTE,
            ModifyTurretTargetPayload::index,
            ByteBufCodecs.BOOL,
            ModifyTurretTargetPayload::value,
            ModifyTurretTargetPayload::new
    );
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleServer(final ModifyTurretTargetPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            BlockEntity be = player.level().getBlockEntity(payload.blockEntityPos);
            if(be instanceof LaserTurretBlockEntity turret) {
                switch (payload.index) {
                    case 0 -> turret.setTargetsHostile(payload.value);
                    case 1 -> turret.setTargetsPassive(payload.value);
                    case 2 -> turret.setTargetsPlayers(payload.value);
                    case 3 -> turret.setTargetsTrusted(payload.value);
                    default -> throw new IllegalArgumentException("Invalid index: " + payload.index);
                }
                turret.markUpdated();
                turret.tryInvalidateTarget();
            }
        });
        context.finishCurrentTask(new ConfigurationTask.Type(""));
    }
}
