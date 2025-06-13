package gg.archipelago.aprandomizer.items;

import com.mojang.serialization.MapCodec;
import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.managers.GoalManager;
import net.minecraft.server.MinecraftServer;

public record DragonEggShardReward() implements APReward {

    public static final MapCodec<DragonEggShardReward> MAP_CODEC = MapCodec.unit(DragonEggShardReward::new);

    @Override
    public MapCodec<? extends APReward> codec() {
        return MAP_CODEC;
    }

    @Override
    public void give(MinecraftServer server) {
        APRandomizer.goalManager().ifPresent(GoalManager::incrementDragonEggShards);
    }

}
