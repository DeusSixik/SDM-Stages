package dev.behindthescenery.sdmstages;

import dev.behindthescenery.sdmstages.data.containers.Stage;
import dev.behindthescenery.sdmstages.data.StageContainer;
import dev.behindthescenery.sdmstages.network.SdmStagesNetwork;
import dev.behindthescenery.sdmstages.network.SendStagesS2C;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Consumer;

public class StageApi {

    private static final Stage ClientStage = new Stage();
    private static StageContainer ServerStage = null;

    public static Stage getClientStage() {
        return ClientStage;
    }

    public static StageContainer getServerStage() {
        return ServerStage;
    }

    public static void reloadServerStage(MinecraftServer server) {
        StageContainer stageContainer  = getServerStage();
        if(stageContainer == null) {
            stageContainer = ServerStage = SdmStages.Config.ContainerConstructor.apply(server);
        }

        stageContainer.load(server);

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            syncWithPlayer(player);
        }
    }

    public static boolean saveServerStage(MinecraftServer server) {
        final StageContainer stageContainer = getServerStage();
        if(stageContainer == null) return false;
        stageContainer.save(server);
        return true;
    }

    public static void syncWithPlayer(ServerPlayer player) {
        SdmStagesNetwork.sendTo(player, new SendStagesS2C(getServerStage().getStage(player)));
    }

    public static boolean modifyStageAndSync(ServerPlayer player, Consumer<Stage> stageConsumer) {
        final StageContainer stageContainer = getServerStage();
        if(stageContainer == null) return false;

        try {
            stageConsumer.accept(stageContainer.getStage(player));
            syncWithPlayer(player);
            return true;
        } catch (Exception e) {
            SdmStages.LOGGER.error(e.getMessage(), e);
            return false;
        }
    }
}
