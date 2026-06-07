package gg.archipelago.aprandomizer.items;

import com.mojang.serialization.MapCodec;
import gg.archipelago.aprandomizer.common.Utils.UnlockableHearts;
import net.minecraft.server.level.ServerPlayer;

public record HeartReward() implements APReward {
    public static final MapCodec<HeartReward> MAP_CODEC = MapCodec.unit(new HeartReward());

    @Override
    public MapCodec<? extends APReward> codec() {
        return MAP_CODEC;
    }

    @Override
    public void give(ServerPlayer player) {
        UnlockableHearts.grantHeart(player);
    }
}
