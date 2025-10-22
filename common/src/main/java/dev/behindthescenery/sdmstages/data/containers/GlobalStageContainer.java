package dev.behindthescenery.sdmstages.data.containers;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.behindthescenery.sdmstages.SdmStages;
import dev.behindthescenery.sdmstages.data.StageContainer;
import dev.behindthescenery.sdmstages.data.StageContainerType;
import dev.behindthescenery.sdmstages.serializers.CodecSupport;
import dev.behindthescenery.sdmstages.serializers.SimpleDataStruct;
import dev.behindthescenery.sdmstages.serializers.StreamCodecSupport;
import dev.behindthescenery.sdmstages.serializers.exceptions.SerializeEncodeException;
import dev.behindthescenery.sdmstages.serializers.utils.DataSerializerUtils;
import net.minecraft.nbt.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class GlobalStageContainer implements StageContainer, SimpleDataStruct, CodecSupport<GlobalStageContainer>, StreamCodecSupport<GlobalStageContainer> {

    public static final Codec<GlobalStageContainer> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(Stage.CODEC.fieldOf("stage").forGetter(s -> s.getStage(null)))
                    .apply(instance, GlobalStageContainer::new));
    public static final StreamCodec<FriendlyByteBuf, GlobalStageContainer> STREAM_CODEC = StreamCodec.composite(
            Stage.STREAM_CODEC, s -> s.getStage(null), GlobalStageContainer::new);

    protected Stage stage;

    public GlobalStageContainer() {
        this(new Stage());
    }

    public GlobalStageContainer(Stage stage) {
        this.stage = stage;
        this.stage.setStageData(this);
    }

    @Override
    public void addStage(@Nullable Object element, Stage stage) {
        // NotUsed
    }

    @Override
    public Stage getStage(@Nullable Object element) {
        return stage;
    }

    @Override
    public StageContainerType getContainerType() {
        return StageContainerType.GLOBAL;
    }

    @Override
    public String getName() {
        return "global_stage_container";
    }

    @Override
    public int getFlags() {
        return SimpleDataStruct.FULL_SAVE;
    }

    @Override
    public boolean load(@Nullable MinecraftServer server) {
        final Path path = getPath(server);
        final int flags = getFlags();

        if((flags & SimpleDataStruct.FULL_SAVE) == 0)
            return loadFullElementsSafe(server, path);

        return false;
    }

    protected boolean loadFullElementsSafe(@Nullable final MinecraftServer server, final Path path) {
        try {
            return loadFullElements(server, path);
        } catch (Exception e) {
            SdmStages.LOGGER.error("Error full load from {}: {}", path, e.getMessage(), e);
            return false;
        }
    }

    protected boolean loadFullElements(@Nullable final MinecraftServer server, final Path path) throws IOException {
        final Path fullPath = path.resolve(getName() + ".data");
        if (!Files.exists(fullPath)) {
            SdmStages.LOGGER.debug("Full-file not found: {} — nothing load", fullPath);
            return true;
        }

        final CompoundTag root = NbtIo.read(fullPath);
        final Tag decoded = DataSerializerUtils.decode(root);

        Pair<Stage, Tag> data = Stage.CODEC.decode(NbtOps.INSTANCE, decoded).getOrThrow();
        if(data == null || data.getFirst() == null) return false;
        stage = data.getFirst();
        return true;
    }

    @Override
    public boolean save(@Nullable MinecraftServer server) {
        final Path path = getPath(server);
        final int flags = getFlags();

        if((flags & SimpleDataStruct.FULL_SAVE) == 0)
            return saveFullElementsSafe(server, path);
        return false;
    }

    protected boolean saveFullElementsSafe(@Nullable final MinecraftServer server, final Path path) {
        try {
            return saveFullElements(server, path);
        } catch (Exception e) {
            SdmStages.LOGGER.error("Error full save: {}", e.getMessage(), e);
            return false;
        }
    }

    protected boolean saveFullElements(@Nullable final MinecraftServer server, final Path path) throws IOException, SerializeEncodeException {
        final Path currentPath = path.resolve(getName() + ".data");
        NbtIo.write(DataSerializerUtils.encode(Stage.CODEC.encodeStart(NbtOps.INSTANCE, stage).getOrThrow()), currentPath);
        return true;
    }

    @Override
    public GlobalStageContainer getValue() {
        return this;
    }

    @Override
    public Codec<GlobalStageContainer> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<FriendlyByteBuf, GlobalStageContainer> streamCodec() {
        return STREAM_CODEC;
    }
}
