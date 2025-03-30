package gg.archipelago.aprandomizer.items;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;

public record ExperienceReward(int experience) implements APReward {

    public static final MapCodec<ExperienceReward> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(
                    Codec.INT.fieldOf("experience").forGetter(ExperienceReward::experience))
            .apply(instance, ExperienceReward::new));

    @Override
    public MapCodec<? extends APReward> codec() {
        return CODEC;
    }

    @Override
    public void give(ServerPlayer player) {
        player.giveExperiencePoints(experience);
    }

}
