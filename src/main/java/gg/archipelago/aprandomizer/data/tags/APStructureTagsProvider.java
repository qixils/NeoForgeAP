package gg.archipelago.aprandomizer.data.tags;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.APStructures;
import gg.archipelago.aprandomizer.tags.APStructureTags;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.StructureTagsProvider;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;

import java.util.concurrent.CompletableFuture;

public class APStructureTagsProvider extends StructureTagsProvider {

    public APStructureTagsProvider(PackOutput output, CompletableFuture<Provider> provider) {
        super(output, provider, APRandomizer.MODID);
    }

    @Override
    protected void addTags(Provider p_256087_) {
        tag(APStructureTags.BASTION_REMNANT)
                .add(BuiltinStructures.BASTION_REMNANT);

        tag(APStructureTags.END_CITY)
                .add(BuiltinStructures.END_CITY)
                .add(APStructures.END_CITY_NETHER_STRUCTURE);

        tag(APStructureTags.FORTRESS)
                .add(BuiltinStructures.FORTRESS);

        tag(APStructureTags.PILLAGER_OUTPOST)
                .add(BuiltinStructures.PILLAGER_OUTPOST)
                .add(APStructures.PILLAGER_OUTPOST_NETHER_STRUCTURE);

        tag(APStructureTags.VILLAGE)
                .add(BuiltinStructures.VILLAGE_DESERT)
                .add(BuiltinStructures.VILLAGE_PLAINS)
                .add(BuiltinStructures.VILLAGE_SAVANNA)
                .add(BuiltinStructures.VILLAGE_SNOWY)
                .add(BuiltinStructures.VILLAGE_TAIGA)
                .add(APStructures.VILLAGE_NETHER_STRUCTURE);
    }

}
