package dev.behindthescenery.sdmstages.serializers;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.Tag;

public interface NbtSupport<T extends Tag> {

    T serialize(RegistryAccess registryAccess);

}
