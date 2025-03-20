package gg.archipelago.aprandomizer.items;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public record APTier(List<APReward> rewards) {
    public static final Codec<APTier> CODEC = Codec.withAlternative(
            RecordCodecBuilder.create(instance -> instance
                    .group(
                            APReward.CODEC.listOf().fieldOf("rewards").forGetter(APTier::rewards))
                    .apply(instance, APTier::new)),
            APReward.CODEC, APTier::new);

    public APTier(APReward reward) {
        this(List.of(reward));
    }
}
