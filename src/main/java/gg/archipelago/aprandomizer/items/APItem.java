package gg.archipelago.aprandomizer.items;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;

import java.util.List;

public record APItem(List<APTier> tiers) {
    public static final Codec<APItem> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(
                    Codec.mapEither(
                            APTier.CODEC.listOf().fieldOf("tiers"),
                            ExtraCodecs.compactListCodec(APReward.CODEC).fieldOf("rewards"))
                            .xmap(
                                    either -> either.map(
                                            tiers -> tiers,
                                            rewards -> List.of(new APTier(rewards))),
                                    tiers -> tiers.size() == 1 ? Either.right(tiers.get(0).rewards()) : Either.left(tiers))
                            .forGetter(APItem::tiers))
            .apply(instance, APItem::new));

    public static APItem ofTiers(List<APTier> tiers) {
//        .forGetter(APItem::rewards);
        return new APItem(tiers);
    }

    public static APItem ofRewards(List<APReward> rewards) {
        return new APItem(List.of(new APTier(rewards)));
    }

    public static APItem ofReward(APReward reward) {
        return new APItem(List.of(new APTier(List.of(reward))));
    }
}
