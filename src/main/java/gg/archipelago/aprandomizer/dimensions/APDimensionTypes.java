package gg.archipelago.aprandomizer.dimensions;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TimelineTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.attribute.BedRule;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.timeline.Timeline;
import net.minecraft.world.timeline.Timelines;

import java.util.Optional;
import java.util.OptionalLong;

public class APDimensionTypes {
    public static void bootstrap(BootstrapContext<DimensionType> context) {
        HolderGetter<Timeline> holdergetter = context.lookup(Registries.TIMELINE);
        context.register(BuiltinDimensionTypes.NETHER,
                new DimensionType(
                        true,
                        false,
                        true,
                        8.0,
                        0,
                        256,
                        128,
                        BlockTags.INFINIBURN_NETHER,
                        0.1F,
                        new DimensionType.MonsterSettings(ConstantInt.of(11), 15),
                        DimensionType.Skybox.NONE,
                        DimensionType.CardinalLightType.NETHER,
                        EnvironmentAttributeMap.builder()
                                .set(EnvironmentAttributes.FOG_START_DISTANCE, 10.0F)
                                .set(EnvironmentAttributes.FOG_END_DISTANCE, 96.0F)
                                .set(EnvironmentAttributes.SKY_LIGHT_COLOR, Timelines.NIGHT_SKY_LIGHT_COLOR)
                                .set(EnvironmentAttributes.SKY_LIGHT_LEVEL, 4.0F)
                                .set(EnvironmentAttributes.SKY_LIGHT_FACTOR, 0.0F)
                                .set(EnvironmentAttributes.DEFAULT_DRIPSTONE_PARTICLE, ParticleTypes.DRIPPING_DRIPSTONE_LAVA)
                                .set(EnvironmentAttributes.BED_RULE, BedRule.EXPLODES)
                                .set(EnvironmentAttributes.RESPAWN_ANCHOR_WORKS, true)
                                .set(EnvironmentAttributes.WATER_EVAPORATES, true)
                                .set(EnvironmentAttributes.FAST_LAVA, true)
                                .set(EnvironmentAttributes.PIGLINS_ZOMBIFY, false)
                                .set(EnvironmentAttributes.CAN_START_RAID, true)
                                .set(EnvironmentAttributes.SNOW_GOLEM_MELTS, true)
                                .build(),
                        holdergetter.getOrThrow(TimelineTags.IN_NETHER)));
    }
}
