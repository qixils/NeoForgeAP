package gg.archipelago.aprandomizer.datacomponents;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gg.archipelago.aprandomizer.items.CompassReward;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

// TODO: i think the index here is not especially useful, could be consolidated down with something like indexOf
public record TrackedStructure(CompassReward reward, int index) {

    public static final Codec<TrackedStructure> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    CompassReward.CODEC.fieldOf("value1").forGetter(TrackedStructure::reward),
                    Codec.INT.fieldOf("value2").forGetter(TrackedStructure::index)
            ).apply(instance, TrackedStructure::new)
    );

    public static final StreamCodec<ByteBuf, TrackedStructure> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodec(CompassReward.CODEC), TrackedStructure::reward, // TODO: stream codec
            ByteBufCodecs.INT, TrackedStructure::index,
            TrackedStructure::new
    );
}
