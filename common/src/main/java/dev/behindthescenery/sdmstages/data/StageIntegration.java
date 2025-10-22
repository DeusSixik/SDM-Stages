package dev.behindthescenery.sdmstages.data;

import dev.behindthescenery.sdmstages.data.containers.PlayerStageContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class StageIntegration {

    protected static List<Function<PlayerStageContainer, AbstractStageComponent>> StageComponents = new ArrayList<>();

    public static void addComponent(Function<PlayerStageContainer, AbstractStageComponent> component) {
        StageComponents.add(component);
    }

    public static List<AbstractStageComponent> createInstances(PlayerStageContainer stagePlayerData) {
        List<AbstractStageComponent> stageComponents = new ArrayList<>();
        StageComponents.forEach(s -> stageComponents.add(s.apply(stagePlayerData)));
        return stageComponents.stream().toList();
    }

    public static void init() {
//        if(Platform.isModLoaded("kubejs"))
//            addComponent(KubeJsStageIntegration::new);
//        if(Platform.isModLoaded("gamestages"))
//            addComponent(GameStageIntegration::new);
    }
}
