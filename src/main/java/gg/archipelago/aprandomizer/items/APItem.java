package gg.archipelago.aprandomizer.items;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;

import java.util.List;

public record APItem(List<List<APReward>> rewards) {
    public static final Codec<APItem> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(
                    ExtraCodecs.compactListCodec(ExtraCodecs.compactListCodec(APReward.CODEC)).fieldOf("rewards").forGetter(APItem::rewards))
            .apply(instance, APItem::new));

    public static APItem ofTiers(List<List<APReward>> tiers) {
        return new APItem(tiers);
    }

    public static APItem ofRewards(List<APReward> rewards) {
        return new APItem(List.of(rewards));
    }

    public static APItem ofReward(APReward reward) {
        return new APItem(List.of(List.of(reward)));
    }
}
