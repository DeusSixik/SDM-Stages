package dev.behindthescenery.sdmstages.network;

import dev.architectury.networking.NetworkManager;
import dev.behindthescenery.sdmstages.SdmStages;
import dev.behindthescenery.sdmstages.StageApi;
import dev.behindthescenery.sdmstages.data.containers.Stage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SendStagesS2C(Stage stage) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SendStagesS2C> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.tryBuild(SdmStages.MODID, "send_stages"));

    public static final StreamCodec<FriendlyByteBuf, SendStagesS2C> STREAM_CODEC =
            StreamCodec.composite(Stage.STREAM_CODEC, SendStagesS2C::stage, SendStagesS2C::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SendStagesS2C message, NetworkManager.PacketContext context) {
        context.queue(() -> StageApi.getClientStage().copyFrom(message.stage()));
    }

}
