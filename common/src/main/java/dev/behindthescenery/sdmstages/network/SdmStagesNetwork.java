package dev.behindthescenery.sdmstages.network;

import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class SdmStagesNetwork {

    private static BaseNetworkHandler Handler;

    public static void init(BaseNetworkHandler handler) {
        SdmStagesNetwork.Handler = handler;

        registerS2C(SendStagesS2C.TYPE, SendStagesS2C.STREAM_CODEC, SendStagesS2C::handle);
    }

    public static <T extends CustomPacketPayload> void registerC2S(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, NetworkManager.NetworkReceiver<T> handler) {
        NetworkManager.registerReceiver(NetworkManager.c2s(), type, codec, handler);
    }

    public static <T extends CustomPacketPayload> void registerS2C(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, NetworkManager.NetworkReceiver<T> handler) {
        if (Platform.getEnvironment() == Env.CLIENT) {
            NetworkManager.registerReceiver(NetworkManager.s2c(), type, codec, handler);
        } else {
            NetworkManager.registerS2CPayloadType(type, codec);
        }
    }

    public static void sendToAll(CustomPacketPayload.Type<?> type, MinecraftServer server, Packet<?> packet) {
        Handler.sendToAll(type, server, packet);
    }

    public static void sendTo(CustomPacketPayload.Type<?> type, ServerPlayer player, Packet<?> packet) {
        Handler.sendTo(type, player, packet);
    }

    public static <T extends CustomPacketPayload> void sendToAll(MinecraftServer server, T packet) {
        Handler.sendToAll(server, packet);
    }

    public static <T extends CustomPacketPayload> void sendTo(ServerPlayer player, T packet) {
        Handler.sendTo(player, packet);
    }

    public static <T extends CustomPacketPayload> void sendToServer(T payload) {
        NetworkManager.sendToServer(payload);
    }
}
