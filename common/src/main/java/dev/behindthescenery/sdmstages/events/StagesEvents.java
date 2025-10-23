package dev.behindthescenery.sdmstages.events;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.behindthescenery.sdmstages.data.StageContainer;
import dev.behindthescenery.sdmstages.data.containers.Stage;
import org.jetbrains.annotations.Nullable;

public interface StagesEvents {

    Event<StageAdd> ON_STAGE_ADD = EventFactory.createLoop(new StageAdd[0]);
    Event<StageRemove> ON_STAGE_REMOVE = EventFactory.createLoop(new StageRemove[0]);
    Event<SyncStages> ON_STAGE_SYNC = EventFactory.createLoop(new SyncStages[0]);

    interface StageAdd {
        void add(String stage, Stage stageData, StageContainer stageContainer, @Nullable Object owner);
    }

    interface StageRemove {
        void remove(String stage, Stage stageData, StageContainer stageContainer, @Nullable Object owner);
    }

    interface SyncStages {
        void sync(Stage stageData, StageContainer stageContainer);
    }
}
