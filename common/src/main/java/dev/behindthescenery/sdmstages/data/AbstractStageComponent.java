package dev.behindthescenery.sdmstages.data;

import dev.behindthescenery.sdmstages.data.containers.PlayerStageContainer;
import dev.behindthescenery.sdmstages.data.containers.Stage;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public abstract class AbstractStageComponent {

    protected PlayerStageContainer stage_data;

    public AbstractStageComponent(PlayerStageContainer stage_data) {
        this.stage_data = stage_data;
    }

    public boolean onAdd(UUID player, String stage) {
        return true;
    }

    public boolean onRemove(UUID player, String stage) {
        return true;
    }

    public void onPlayerJoin(ServerPlayer player, Stage stage) {}
}
