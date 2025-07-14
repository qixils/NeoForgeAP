package gg.archipelago.aprandomizer.data.datamaps;

import gg.archipelago.aprandomizer.datamaps.APDataMaps;
import gg.archipelago.aprandomizer.gifting.BaseTraitsKt;
import gg.archipelago.aprandomizer.tags.APBiomeTags;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.data.DataMapProvider;

import java.util.concurrent.CompletableFuture;

public class APDataMapProvider extends DataMapProvider {

    public APDataMapProvider(PackOutput packOutput, CompletableFuture<Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather(Provider provider) {
        HolderGetter<Biome> biomes = provider.lookupOrThrow(Registries.BIOME);
        builder(APDataMaps.DEFAULT_STRUCTURE_BIOMES)
                .add(Level.OVERWORLD, biomes.getOrThrow(APBiomeTags.OVERWORLD_STRUCTURE), false)
                .add(Level.NETHER, biomes.getOrThrow(APBiomeTags.NETHER_STRUCTURE), false)
                .add(Level.END, biomes.getOrThrow(APBiomeTags.END_STRUCTURE), false);
        BaseTraitsKt.baseItemTraits(builder(APDataMaps.ITEMS_GIFTS_BASE_TRAITS),provider);
        BaseTraitsKt.baseFluidTraits(builder(APDataMaps.FLUIDS_GIFTS_TRAITS),provider);
    }

}
