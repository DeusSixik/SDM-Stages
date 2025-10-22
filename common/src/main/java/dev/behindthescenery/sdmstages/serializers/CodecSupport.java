package dev.behindthescenery.sdmstages.serializers;

import com.mojang.serialization.Codec;
import dev.behindthescenery.sdmstages.serializers.utils.DataSerializerUtils;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
public interface CodecSupport<T> extends NbtSupport<CompoundTag> {

    T getValue();

    Codec<T> codec();

    @Override
    default CompoundTag serialize(RegistryAccess registryAccess) {
        return DataSerializerUtils.encode(codec().encodeStart(NbtOps.INSTANCE, getValue()).getOrThrow());
    }

}
