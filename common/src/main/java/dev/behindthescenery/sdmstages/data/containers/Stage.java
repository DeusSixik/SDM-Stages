package dev.behindthescenery.sdmstages.data.containers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.behindthescenery.sdmstages.data.StageContainer;
import dev.behindthescenery.sdmstages.serializers.CodecSupport;
import dev.behindthescenery.sdmstages.serializers.StreamCodecSupport;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Stage implements CodecSupport<Stage>, StreamCodecSupport<Stage> {

    public static final StreamCodec<FriendlyByteBuf, List<String>> LIST_STRING_STEAM_CODEC = new StreamCodec<>() {
        @Override
        public @NotNull List<String> decode(FriendlyByteBuf buf) {
            final List<String> list = new ArrayList<>();
            final int size = buf.readInt();
            for (int i = 0; i < size; i++) {
                list.add(buf.readUtf());
            }
            return list;
        }

        @Override
        public void encode(FriendlyByteBuf buf, List<String> list) {
            buf.writeInt(list.size());
            for (String string : list) {
                buf.writeUtf(string);
            }
        }
    };

    public static final Codec<Stage> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(Codec.STRING.listOf().fieldOf("stages").forGetter(Stage::getStages)).apply(instance, Stage::new));

    public static final StreamCodec<FriendlyByteBuf, Stage> STREAM_CODEC = StreamCodec.composite(
            LIST_STRING_STEAM_CODEC, Stage::getStages, Stage::new
    );

    protected final List<String> stage_list;
    protected StageContainer stage_data;

    public Stage() {
        this(new ArrayList<>(), null);
    }

    public Stage(List<String> strings) {
        this(strings, null);
    }

    public Stage(StageContainer stage_data) {
        this(new ArrayList<>(), stage_data);
    }

    public Stage(List<String> stages, StageContainer stage_data) {
        this.stage_list = stages;
        this.stage_data = stage_data;
    }

    public Stage setStageData(StageContainer stage_data) {
        this.stage_data = stage_data;
        return this;
    }

    public List<String> getStages() {
        return stage_list.stream().toList();
    }

    public void addStage(String stage) {
        stage_list.add(stage);
    }

    public void addStages(String... stages) {
        this.stage_list.addAll(List.of(stages));
    }

    public boolean contains(String stage) {
        return this.stage_list.contains(stage);
    }

    public boolean remove(String stage) {
        return this.stage_list.remove(stage);
    }

    @Override
    public Stage getValue() {
        return this;
    }

    @Override
    public String toString() {
        return "Stage{" +
                stage_list +
                '}';
    }

    @Override
    public Codec<Stage> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<FriendlyByteBuf, Stage> streamCodec() {
        return STREAM_CODEC;
    }

    public Stage merge(Stage stage) {
        this.stage_list.addAll(stage.stage_list);
        return this;
    }

    public Stage copyFrom(Stage stage) {
        this.stage_list.clear();
        this.stage_list.addAll(stage.stage_list);
        return this;
    }
}
