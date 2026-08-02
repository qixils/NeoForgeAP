package gg.archipelago.aprandomizer.items.compass;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.biome.Biome;

import java.util.Optional;

public interface BiomeTarget extends CompassTarget {
    @Override
    default Optional<BlockPos> findTarget(ServerLevel level, BlockPos start, ServerPlayer player) {
        Pair<BlockPos, Holder<Biome>> target = level.findClosestBiome3d(biome -> isTargetingBiome(biome, player), start, 6400, 32, 64);
        if (target == null) {
            return Optional.empty();
        }
        return Optional.of(target.getFirst());
    }

    boolean isTargetingBiome(Holder<Biome> biome, ServerPlayer player);
}
