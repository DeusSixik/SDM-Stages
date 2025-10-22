package dev.behindthescenery.sdmstages.serializers.utils;

import dev.behindthescenery.sdmstages.serializers.NbtSupport;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class DataSerializerUtils {

    public static void putBlockPos(CompoundTag f, String key, BlockPos pos) {
        CompoundTag nbt = new CompoundTag();
        nbt.putLong("p", pos.asLong());
        f.put(key, nbt);
    }
    public static BlockPos getBlockPos(CompoundTag f, String key) {
        CompoundTag nbt = f.getCompound(key);
        return BlockPos.of(nbt.getLong("x"));
    }

    public static void putResourceLocation(CompoundTag nbt, String key, ResourceLocation location) {
        nbt.putString(key, location.toString());
    }

    public static ResourceLocation getResourceLocation(CompoundTag nbt, String key) {
        return ResourceLocation.tryParse(nbt.getString(key));
    }

    public static void putItemStack(CompoundTag nbt, String key, ItemStack itemStack, HolderLookup.Provider provider) {
        Tag itemTag = itemStack.save(provider, new CompoundTag());
        nbt.put(key, itemTag);
    }

    public static ItemStack getItemStack(CompoundTag nbt, String key, HolderLookup.Provider provider) {
        return ItemStack.parse(provider, nbt.getCompound(key)).orElse(ItemStack.EMPTY);
    }

    public static void putItem(CompoundTag nbt, String key, Item item) {
        putResourceLocation(nbt, key, BuiltInRegistries.ITEM.getKey(item));
    }

    public static Item getItem(CompoundTag nbt, String key) {
        return BuiltInRegistries.ITEM.get(getResourceLocation(nbt, key));
    }

    public static ListTag collectAll(RegistryAccess access, Collection<? extends NbtSupport<?>> nbtCollection) {
        ListTag listTag = new ListTag();

        for (NbtSupport<?> codecSupport : nbtCollection) {
            listTag.add(codecSupport.serialize(access));
        }

        return listTag;
    }

    public static CompoundTag encode(Tag tag) {
        CompoundTag nbt = new CompoundTag();
        nbt.put("data_codec", tag);
        return nbt;
    }

    @Nullable
    public static Tag decode(CompoundTag nbt) {
        if(nbt == null) return null;

        if(!nbt.contains("data_codec"))
            return nbt;

        return nbt.get("data_codec");
    }


}
