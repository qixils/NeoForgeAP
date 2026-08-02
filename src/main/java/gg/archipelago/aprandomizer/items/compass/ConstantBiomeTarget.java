package gg.archipelago.aprandomizer.items.compass;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.biome.Biome;

public record ConstantBiomeTarget(HolderSet<Biome> biomes) implements BiomeTarget {
    public static final MapCodec<ConstantBiomeTarget> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(
                    RegistryCodecs.homogeneousList(Registries.BIOME).fieldOf("biomes").forGetter(ConstantBiomeTarget::biomes))
            .apply(instance, ConstantBiomeTarget::new));

    @Override
    public boolean isTargetingBiome(Holder<Biome> biome, ServerPlayer player) {
        return biomes.contains(biome);
    }

    @Override
    public MapCodec<? extends CompassTarget> codec() {
        return CODEC;
    }

}
