package gg.archipelago.aprandomizer.items;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Function;

public interface APReward {
    public static final Codec<APReward> CODEC = APRewardTypes.REGISTRY.byNameCodec().dispatch(APReward::codec, Function.identity());

    MapCodec<? extends APReward> codec();

    void give(ServerPlayer player);
}
