package dev.behindthescenery.sdmstages.network;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public interface BaseNetworkHandler {

    void sendToAll(CustomPacketPayload.Type<?> type, MinecraftServer server, Packet<?> packet);

    void sendTo(CustomPacketPayload.Type<?> type, ServerPlayer player, Packet<?> packet);

    <T extends CustomPacketPayload> void sendToAll(MinecraftServer server, T packet);

    <T extends CustomPacketPayload> void sendTo(ServerPlayer player, T packet);
}
