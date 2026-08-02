package gg.archipelago.aprandomizer.timelines;

import gg.archipelago.aprandomizer.APRandomizer;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.clock.WorldClocks;
import net.minecraft.world.timeline.Timeline;

public class APTimelines {
    public static final ResourceKey<Timeline> FORCE_ALLOW_RAIDS = ResourceKey.create(Registries.TIMELINE, Identifier.fromNamespaceAndPath(APRandomizer.MODID, "force_allow_raids"));

    public static void bootstrap(BootstrapContext<Timeline> context) {
        HolderGetter<WorldClock> clocks = context.lookup(Registries.WORLD_CLOCK);
        context.register(FORCE_ALLOW_RAIDS,
                Timeline.builder(clocks.getOrThrow(WorldClocks.OVERWORLD))
                        .addTrack(EnvironmentAttributes.CAN_START_RAID, keyframes -> keyframes.addKeyframe(0, true))
                        .build());
    }
}
