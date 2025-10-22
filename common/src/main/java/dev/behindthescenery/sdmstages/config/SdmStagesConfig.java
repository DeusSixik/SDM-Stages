package dev.behindthescenery.sdmstages.config;

import dev.behindthescenery.sdmstages.data.StageContainer;
import dev.behindthescenery.sdmstages.data.containers.PlayerStageContainer;
import net.minecraft.server.MinecraftServer;

import java.util.function.Function;

public class SdmStagesConfig {

    public Function<MinecraftServer, StageContainer> ContainerConstructor;

    public SdmStagesConfig() {
        this((s) -> new PlayerStageContainer());
    }

    public SdmStagesConfig(Function<MinecraftServer, StageContainer> ContainerConstructor) {
        this.ContainerConstructor = ContainerConstructor;
    }
}
