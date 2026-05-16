package gg.archipelago.aprandomizer.items.compass;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;

import java.util.Optional;

public record BiomeTarget(HolderSet<Biome> biomes) implements CompassTarget {
    public static final MapCodec<BiomeTarget> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(
                    RegistryCodecs.homogeneousList(Registries.BIOME).fieldOf("biomes").forGetter(BiomeTarget::biomes))
            .apply(instance, BiomeTarget::new));

    @Override
    public Optional<BlockPos> findTarget(ServerLevel level, BlockPos start) {
        Pair<BlockPos, Holder<Biome>> target = level.findClosestBiome3d(biome -> biomes.contains(biome), start, 6400, 32, 64);
        if (target == null) {
            return Optional.empty();
        }
        return Optional.of(target.getFirst());
    }

    @Override
    public MapCodec<? extends CompassTarget> codec() {
        return CODEC;
    }

}
