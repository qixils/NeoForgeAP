package gg.archipelago.aprandomizer.items.compass;

import com.mojang.serialization.MapCodec;
import gg.archipelago.aprandomizer.attachments.APAttachmentTypes;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.biome.Biome;

import java.util.Set;

public class UnvisitedBiomeTarget implements BiomeTarget {

    public static final MapCodec<UnvisitedBiomeTarget> CODEC = MapCodec.unit(UnvisitedBiomeTarget::new);

    @Override
    public boolean isTargetingBiome(Holder<Biome> biome, ServerPlayer player) {
        Set<ResourceKey<Biome>> visited = player.getData(APAttachmentTypes.AP_PLAYER).getVisitedBiomes();
        return !visited.contains(biome.getKey());
    }

    @Override
    public MapCodec<UnvisitedBiomeTarget> codec() {
        return CODEC;
    }

}
