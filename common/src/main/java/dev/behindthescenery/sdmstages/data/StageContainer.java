package dev.behindthescenery.sdmstages.data;

import dev.behindthescenery.sdmstages.data.containers.Stage;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

public interface StageContainer {

    void addStage(@Nullable Object element, Stage stage);

    Stage getStage(@Nullable Object element);

    default StageContainerType getContainerType() {
        return StageContainerType.CUSTOM;
    }

    boolean load(@Nullable MinecraftServer server);

    boolean save(@Nullable MinecraftServer server);
}
