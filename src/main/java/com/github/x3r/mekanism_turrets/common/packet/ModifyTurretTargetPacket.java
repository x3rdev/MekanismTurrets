package com.github.x3r.mekanism_turrets.common.packet;

import com.github.x3r.mekanism_turrets.common.block_entity.LaserTurretBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ModifyTurretTargetPacket {

    private final BlockPos blockEntityPos;
    private final byte index;
    private final boolean value;

    public ModifyTurretTargetPacket(BlockPos blockEntityPos, byte index, boolean value) {
        this.blockEntityPos = blockEntityPos;
        this.index = index;
        this.value = value;
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(blockEntityPos);
        buffer.writeByte(index);
        buffer.writeBoolean(value);
    }

    public static ModifyTurretTargetPacket decode(FriendlyByteBuf buffer) {
        BlockPos pos = buffer.readBlockPos();
        byte index = buffer.readByte();
        boolean value = buffer.readBoolean();
        return new ModifyTurretTargetPacket(pos, index, value);
    }

    public void receivePacket(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            BlockEntity be = player.level().getBlockEntity(blockEntityPos);
            if(be instanceof LaserTurretBlockEntity turret) {
                switch (index) {
                    case 0 -> turret.setTargetsHostile(value);
                    case 1 -> turret.setTargetsPassive(value);
                    case 2 -> turret.setTargetsPlayers(value);
                    case 3 -> turret.setTargetsTrusted(value);
                    default -> throw new IllegalArgumentException("Invalid index: " + index);
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}
