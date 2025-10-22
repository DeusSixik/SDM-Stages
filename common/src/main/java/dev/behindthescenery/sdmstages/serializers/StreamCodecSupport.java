package dev.behindthescenery.sdmstages.serializers;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;


public interface StreamCodecSupport<T> {

    StreamCodec<FriendlyByteBuf, T> streamCodec();
}
