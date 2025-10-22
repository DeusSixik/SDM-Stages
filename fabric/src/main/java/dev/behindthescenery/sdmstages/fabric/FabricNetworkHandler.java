package dev.behindthescenery.sdmstages.fabric;

import dev.behindthescenery.sdmstages.network.BaseNetworkHandler;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class FabricNetworkHandler implements BaseNetworkHandler {
    @Override
    public void sendToAll(CustomPacketPayload.Type<?> type, MinecraftServer server, Packet<?> packet) {

    }

    @Override
    public void sendTo(CustomPacketPayload.Type<?> type, ServerPlayer player, Packet<?> packet) {

    }

    @Override
    public <T extends CustomPacketPayload> void sendToAll(MinecraftServer server, T packet) {

    }

    @Override
    public <T extends CustomPacketPayload> void sendTo(ServerPlayer player, T packet) {

    }
}
