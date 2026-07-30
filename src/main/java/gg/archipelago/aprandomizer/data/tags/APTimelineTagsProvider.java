package gg.archipelago.aprandomizer.data.tags;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.timelines.APTimelines;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.TimelineTags;
import net.minecraft.world.timeline.Timeline;

import java.util.concurrent.CompletableFuture;

public class APTimelineTagsProvider extends TagsProvider<Timeline> {

    public APTimelineTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, Registries.TIMELINE, provider, APRandomizer.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        tag(TimelineTags.IN_NETHER)
                .add(APTimelines.FORCE_ALLOW_RAIDS);

    }

}
