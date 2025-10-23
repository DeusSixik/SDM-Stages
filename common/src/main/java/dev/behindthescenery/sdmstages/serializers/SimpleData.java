package dev.behindthescenery.sdmstages.serializers;

import dev.behindthescenery.sdmstages.SdmStages;
import dev.behindthescenery.sdmstages.serializers.exceptions.SerializeEncodeException;
import dev.behindthescenery.sdmstages.serializers.utils.DataSerializerUtils;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public abstract class SimpleData<Key, Value extends NbtSupport<? extends Tag>> implements SimpleDataStruct {

    private final Map<Key, Value> map_data;

    public SimpleData(final Supplier<Map<Key, Value>> constr) {
        this.map_data = constr.get();
    }

    public Value putValue(final Key key, final Value value) {
        return map_data.put(key, value);
    }

    public Value getOrCreate(final Key key, final Function<Key, Value> valueSupplier) {
       return map_data.computeIfAbsent(key, valueSupplier);
    }

    public Value getValue(final Key key) {
        return map_data.get(key);
    }

    public boolean contains(final Key key) {
        return map_data.containsKey(key);
    }

    public boolean contains(final Value key) {
        return map_data.containsValue(key);
    }

    public int getSize() {
        return map_data.size();
    }

    public abstract Value createElement(@Nullable final MinecraftServer server, final Tag valueNbt, Key key);

    public abstract Key createElementKey(@Nullable final MinecraftServer server, final String fileName);

    public abstract Key createElementKey(@Nullable final MinecraftServer server, final CompoundTag entryNbt);

    public abstract String createElementKey(@Nullable final MinecraftServer server, final Key key, final Value value);

    public String getDataKey() {
        return "data";
    }

    /**
     * Invoke whe element save
     */
    public abstract CompoundTag createNbt(@Nullable RegistryAccess access, Key key, Value value);

    public final String createElementKeyImpl(@Nullable final MinecraftServer server, final Key key, final Value value) {
        return getName() + "_" + createElementKey(server, key, value);
    }

    @Override
    public final boolean load(@Nullable MinecraftServer server) {
        final Path path = getPath(server);
        final int flags = getFlags();

        map_data.clear();

        if((flags & SimpleDataStruct.FULL_SAVE) == 0)
            return loadFullElementsSafe(server, path, map_data);

        if((flags & SimpleDataStruct.SINGLE_SAVE) == 0) {
            return loadSingleElementsSafe(server, path, map_data);
        }

        return false;
    }

    @Override
    public final boolean save(@Nullable final MinecraftServer server) {
        final Path path = getPath(server);
        final int flags = getFlags();

        if((flags & SimpleDataStruct.FULL_SAVE) == 0)
            return saveFullElementsSafe(server, path, map_data);

        if((flags & SimpleDataStruct.SINGLE_SAVE) == 0) {
            for (Map.Entry<Key, Value> entry : map_data.entrySet()) {
                if(!saveSingleElementSafe(server, path, entry.getKey(), entry.getValue())) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    protected boolean saveFullElementsSafe(@Nullable final MinecraftServer server, final Path path, final Map<Key, Value> map_data) {
        try {
            return saveFullElements(server, path, map_data);
        } catch (Exception e) {
            SdmStages.LOGGER.error("Error full save: {}", e.getMessage(), e);
            return false;
        }
    }

    protected boolean saveFullElements(@Nullable final MinecraftServer server, final Path path, final Map<Key, Value> map_data) throws IOException, SerializeEncodeException {
        ListTag valuesData = new ListTag();
        for (Map.Entry<Key, Value> entry : map_data.entrySet()) {
            valuesData.add(createNbt(server != null ? server.registryAccess() : null, entry.getKey(), entry.getValue()));
        }
        final Path currentPath = path.resolve(getName() + ".data");
        NbtIo.write(DataSerializerUtils.encode(valuesData), currentPath);
        return true;
    }

    protected boolean saveSingleElementSafe(@Nullable final MinecraftServer server, final Path path, final Key key, final Value value) {
        try {
            return saveSingleElement(server, path, key, value);
        } catch (Exception e) {
            SdmStages.LOGGER.error("Error single save for {}: {}", key, e.getMessage(), e);
            return false;
        }
    }

    protected boolean saveSingleElement(@Nullable final MinecraftServer server, final Path path, final Key key, final Value value) throws IOException {
        final Path currentPath = path.resolve(createElementKeyImpl(server, key, value) + ".data");
        NbtIo.write(DataSerializerUtils.encode(createNbt(server != null ? server.registryAccess() : null, key, value)), currentPath);
        return true;
    }

    protected boolean loadFullElementsSafe(@Nullable final MinecraftServer server, final Path path, final Map<Key, Value> map_data) {
        try {
            return loadFullElements(server, path, map_data);
        } catch (Exception e) {
            SdmStages.LOGGER.error("Error full load from {}: {}", path, e.getMessage(), e);
            return false;
        }
    }

    protected boolean loadFullElements(@Nullable final MinecraftServer server, final Path path, final Map<Key, Value> map_data) throws IOException {
        final Path fullPath = path.resolve(getName() + ".data");
        if (!Files.exists(fullPath)) {
            SdmStages.LOGGER.debug("Full-file not found: {} — nothing load", fullPath);
            return true;
        }

        final CompoundTag root = NbtIo.read(fullPath);
        final Tag decoded = DataSerializerUtils.decode(root);
        if (!(decoded instanceof ListTag listTag)) {
            SdmStages.LOGGER.warn("Full-file {} not contains ListTag", fullPath);
            return false;
        }

        boolean success = true;
        for (Tag t : listTag) {
            if (!(t instanceof CompoundTag entryNbt)) {
                SdmStages.LOGGER.warn("Un correct entry in full ListTag");
                success = false;
                continue;
            }

            final Key key = createElementKey(server, entryNbt);
            if (key == null) {
                SdmStages.LOGGER.warn("Can't get key from entry");
                success = false;
                continue;
            }

            final Tag valueNbt = entryNbt.get(getDataKey());
            if (!(valueNbt instanceof CompoundTag)) {
                SdmStages.LOGGER.warn("Not fount 'data' in entry for key {}", key);
                success = false;
                continue;
            }

            final Value value = createElement(server, valueNbt, key);
            if (value != null) {
                map_data.put(key, value);
            } else {
                SdmStages.LOGGER.warn("Can't create value for key {}", key);
                success = false;
            }
        }
        return success;
    }

    protected boolean loadSingleElementsSafe(@Nullable final MinecraftServer server, final Path path, final Map<Key, Value> map_data) {
        try {
            return loadSingleElements(server, path, map_data);
        } catch (Exception e) {
            SdmStages.LOGGER.error("Error single load from {}: {}", path, e.getMessage(), e);
            return false;
        }
    }

    protected boolean loadSingleElements(@Nullable final MinecraftServer server, final Path path, final Map<Key, Value> map_data) throws IOException {
        final String prefix = getName() + "_";
        final String extension = ".data";
        AtomicBoolean success = new AtomicBoolean(true);

        try (Stream<Path> fileStream = Files.walk(path, 1)
                .filter(Files::isRegularFile)
                .filter(p -> {
                    final String fileName = p.getFileName().toString();
                    return fileName.startsWith(prefix) && fileName.endsWith(extension);  // Только релевантные файлы
                })) {

            fileStream.forEach(file -> {
                final String fileName = file.getFileName().toString();
                final String keyStr = fileName.substring(prefix.length(), fileName.length() - extension.length());  // Часть после "_"

                try {
                    final CompoundTag root = NbtIo.read(file);
                    final Tag decoded = DataSerializerUtils.decode(root);
                    if (!(decoded instanceof CompoundTag entryNbt)) {
                        SdmStages.LOGGER.warn("Single-file {} not contains Compound entry", file);
                        success.set(false);
                        return;
                    }

                    Key key = createElementKey(server, entryNbt);
                    if (key == null) {
                        key = createElementKey(server, keyStr);
                    }
                    if (key == null) {
                        SdmStages.LOGGER.warn("Can't get key from {} or filename", file);
                        success.set(false);
                        return;
                    }

                    final Tag valueNbt = entryNbt.get(getDataKey());
                    if (!(valueNbt instanceof CompoundTag)) {
                        SdmStages.LOGGER.warn("Not found 'data' in single-file {}", file);
                        success.set(false);
                        return;
                    }

                    final Value value = createElement(server, valueNbt, key);
                    if (value != null) {
                        map_data.put(key, value);
                    } else {
                        SdmStages.LOGGER.warn("Can't create value for key {} from {}", key, file);
                        success.set(false);
                    }
                } catch (IOException | SerializeEncodeException e) {
                    SdmStages.LOGGER.error("Error read single-file {}: {}", file, e.getMessage(), e);
                    success.set(false);
                }
            });
        }

        return success.get();
    }

    @Override
    public String toString() {
        return "SimpleData{" +
                map_data +
                '}';
    }
}
