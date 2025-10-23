package dev.behindthescenery.sdmstages;

import com.mojang.logging.LogUtils;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.behindthescenery.sdmstages.config.SdmStagesConfig;
import dev.behindthescenery.sdmstages.network.BaseNetworkHandler;
import dev.behindthescenery.sdmstages.network.SdmStagesNetwork;
import net.minecraft.server.level.ServerLevel;
import org.slf4j.Logger;

public final class SdmStages {
    public static final String MODID = "sdmstages";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final SdmStagesConfig Config = new SdmStagesConfig();

    public static void init(BaseNetworkHandler handler) {
        SdmStagesNetwork.init(handler);

        CommandRegistrationEvent.EVENT.register(SdmStagesCommand::registerCommands);
        LifecycleEvent.SERVER_BEFORE_START.register(StageApi::reloadServerStage);
        LifecycleEvent.SERVER_LEVEL_SAVE.register(s -> {
            if(s.dimension() == ServerLevel.OVERWORLD) {
                StageApi.saveServerStage(s.getServer());
            }
        });

        PlayerEvent.PLAYER_JOIN.register(StageApi::syncWithPlayer);
    }
}
