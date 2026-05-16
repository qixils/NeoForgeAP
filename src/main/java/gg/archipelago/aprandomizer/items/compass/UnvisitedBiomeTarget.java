package gg.archipelago.aprandomizer.items.compass;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import gg.archipelago.aprandomizer.attachments.APAttachmentTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.biome.Biome;

import java.util.Optional;
import java.util.Set;

public class UnvisitedBiomeTarget implements CompassTarget {

    public static final MapCodec<UnvisitedBiomeTarget> CODEC = MapCodec.unit(UnvisitedBiomeTarget::new);

    @Override
    public Optional<BlockPos> findTarget(ServerLevel level, BlockPos start, ServerPlayer player) {
        Set<ResourceKey<Biome>> visited = player.getData(APAttachmentTypes.AP_PLAYER).getVisitedBiomes();
        Pair<BlockPos, Holder<Biome>> target = level.findClosestBiome3d(biome -> !visited.contains(biome.getKey()), start, 6400, 32, 64);
        if (target == null) {
            return Optional.empty();
        }
        return Optional.of(target.getFirst());
    }

    @Override
    public MapCodec<UnvisitedBiomeTarget> codec() {
        return CODEC;
    }

}
