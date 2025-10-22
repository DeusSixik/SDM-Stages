package dev.behindthescenery.sdmstages.data.containers;

import dev.behindthescenery.sdmstages.data.AbstractStageComponent;
import dev.behindthescenery.sdmstages.data.StageContainer;
import dev.behindthescenery.sdmstages.data.StageContainerType;
import dev.behindthescenery.sdmstages.data.StageIntegration;
import dev.behindthescenery.sdmstages.serializers.SimpleData;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PlayerStageContainer extends SimpleData<UUID, Stage> implements StageContainer {

    protected static final String ID_KEY = "player_id";
    protected static final String DATA_KEY = "data";

    protected List<AbstractStageComponent> components = new ArrayList<>();

    public PlayerStageContainer() {
        super(HashMap::new);
        initializeComponents();
    }

    public void addStage(Player player, String stage) {
        addStage(player.getGameProfile().getId(), stage);
    }

    public void addStage(UUID player, String stage) {
        Stage data = getOrCreate(player, (s) -> new Stage(this));

        boolean isBreak = false;

        for (AbstractStageComponent component : components) {
            if(!component.onAdd(player, stage)) {
                isBreak = true;
            }
        }

        if(isBreak) return;

        data.addStage(stage);
    }

    public void addStages(Player player, String... stage) {
        addStages(player.getGameProfile().getId(), stage);
    }

    public void addStages(UUID player, String... stage) {
        Stage data = getOrCreate(player, (s) -> new Stage(this));

        boolean isBreak = false;

        for (AbstractStageComponent component : components) {
            for (String s : stage) {
                if(!component.onAdd(player, s)) {
                    isBreak = true;
                    break;
                }
            }
        }

        if(isBreak)
            return;

        data.addStages(stage);
    }

    public void removeStage(Player player, String stage) {
        removeStage(player.getGameProfile().getId(), stage);
    }

    public void removeStage(UUID player, String stage) {
        Stage data = getOrCreate(player, (s) -> new Stage(this));

        boolean isBreak = false;

        for (AbstractStageComponent component : components) {
            if(!component.onRemove(player, stage)) {
                isBreak = true;
            }
        }

        if(isBreak) return;

        data.remove(stage);
    }

    public boolean hasStage(Player player, String stage) {
        return hasStage(player.getGameProfile().getId(), stage);
    }

    public boolean hasStage(UUID player, String stage) {
        Stage data = getOrCreate(player, (s) -> new Stage(this));
        return data.contains(stage);
    }

    public Collection<String> getStages(Player player) {
        return getStages(player.getGameProfile().getId());
    }

    public Collection<String> getStages(UUID player) {
        Stage data = getOrCreate(player, (s) -> new Stage(this));
        return new ArrayList<>(data.stage_list);
    }

    public void onPlayerJoin(ServerPlayer player) {
        Stage data = getOrCreate(player.getGameProfile().getId(), (s) -> new Stage(this));

        for (AbstractStageComponent component : components) {
            component.onPlayerJoin(player, data);
        }

    }

    @Override
    public Stage createElement(@Nullable MinecraftServer server, Tag valueNbt) {
        if(!(valueNbt instanceof CompoundTag nbt)) {
            return new Stage(this);
        }

        return Stage.CODEC.decode(NbtOps.INSTANCE, nbt).getOrThrow().getFirst().setStageData(this);
    }

    @Override
    public UUID createElementKey(@Nullable MinecraftServer server, String fileName) {
        return UUID.fromString(fileName);
    }

    @Override
    public UUID createElementKey(@Nullable MinecraftServer server, CompoundTag entryNbt) {
        return entryNbt.getUUID(ID_KEY);
    }

    @Override
    public String createElementKey(@Nullable MinecraftServer server, UUID uuid, Stage stages) {
        return uuid.toString();
    }

    @Override
    public CompoundTag createNbt(@Nullable RegistryAccess access, UUID uuid, Stage stages) {
        @Nullable final Tag encodeData = stages.codec().encodeStart(NbtOps.INSTANCE, stages).getOrThrow();

        CompoundTag nbt = new CompoundTag();
        nbt.putUUID(ID_KEY, uuid);
        nbt.put(DATA_KEY, Objects.requireNonNullElseGet(encodeData, CompoundTag::new));

        return nbt;
    }

    @Override
    public String getName() {
        return "PlayerStages";
    }


    @Override
    public String getDataKey() {
        return DATA_KEY;
    }

    @Override
    public int getFlags() {
        return SINGLE_SAVE;
    }

    protected void initializeComponents() {
        components = StageIntegration.createInstances(this);
    }

    @Override
    public void addStage(@Nullable Object element, Stage stage) {
        UUID currentUUID;

        if(element instanceof UUID uuid) {
            currentUUID = uuid;
        } else if(element instanceof Player player) {
            currentUUID = player.getGameProfile().getId();
        } else
            throw new IllegalArgumentException();

        putValue(currentUUID, stage);
    }

    @Override
    public Stage getStage(@Nullable Object element) {
        UUID currentUUID;

        if(element instanceof UUID uuid) {
            currentUUID = uuid;
        } else if(element instanceof Player player) {
            currentUUID = player.getGameProfile().getId();
        } else
            throw new IllegalArgumentException();

        return getOrCreate(currentUUID, (s) -> new Stage(this));
    }

    @Override
    public StageContainerType getContainerType() {
        return StageContainerType.PLAYER;
    }
}
