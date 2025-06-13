package gg.archipelago.aprandomizer.items;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Function;

public interface APReward {
    Codec<APReward> CODEC = APRewardTypes.REGISTRY.byNameCodec().dispatch(APReward::codec, Function.identity());

    MapCodec<? extends APReward> codec();

    default void give(ServerPlayer player) {

    }

    default void give(MinecraftServer server) {

    }
}
