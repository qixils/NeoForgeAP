package gg.archipelago.aprandomizer.data.tags;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.tags.APBiomeTags;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biomes;

import java.util.concurrent.CompletableFuture;

public class APBiomeTagsProvider extends BiomeTagsProvider {

    public APBiomeTagsProvider(PackOutput output, CompletableFuture<Provider> provider) {
        super(output, provider, APRandomizer.MODID);
    }

    @Override
    protected void addTags(Provider p_256485_) {
        tag(APBiomeTags.BEE_GROVE_BIOMES)
                .add(Biomes.PLAINS)
                .add(Biomes.MEADOW)
                .add(Biomes.SUNFLOWER_PLAINS)
                .add(Biomes.SAVANNA)
                .add(Biomes.FLOWER_FOREST)
                .addTag(BiomeTags.IS_FOREST);

        tag(APBiomeTags.NONE);

        tag(APBiomeTags.OVERWORLD_STRUCTURE)
                .add(Biomes.PLAINS)
                .add(Biomes.MEADOW)
                .add(Biomes.DESERT)
                .add(Biomes.SAVANNA)
                .add(Biomes.SNOWY_PLAINS)
                .add(Biomes.TAIGA);

        tag(APBiomeTags.NETHER_STRUCTURE)
                .addTag(BiomeTags.IS_NETHER);

        tag(APBiomeTags.END_STRUCTURE)
                .add(Biomes.END_HIGHLANDS)
                .add(Biomes.END_MIDLANDS);
    }

}
