package dev.behindthescenery.sdmstages.serializers;

import dev.architectury.platform.Platform;
import dev.behindthescenery.sdmstages.SdmStages;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;

public interface SimpleDataStruct {

    int FULL_SAVE = 1;
    int SINGLE_SAVE = 1 << 1;

    String getName();

    int getFlags();

    boolean save(@Nullable final MinecraftServer server);

    boolean load(@Nullable final MinecraftServer server);

    default Path getPath(@Nullable final MinecraftServer server) {
        Path path;
        if (server == null) {
            Path data = Platform.getGameFolder().resolve("data");
            try {
                Files.createDirectories(data);
            } catch (Exception e) {
                SdmStages.LOGGER.error(e.getMessage());
            }
            path = data;
        } else {
            path = server.getWorldPath(LevelResource.LEVEL_DATA_FILE).getParent();
        }
        return path;
    }
}
