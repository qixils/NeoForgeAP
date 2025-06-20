package gg.archipelago.aprandomizer.dimensions;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;

import java.util.Optional;
import java.util.OptionalLong;

public class APDimensionTypes {
    public static void bootstrap(BootstrapContext<DimensionType> context) {
        context.register(BuiltinDimensionTypes.OVERWORLD,
                new DimensionType(
                        OptionalLong.empty(),
                        true,
                        false,
                        false,
                        true,
                        1,
                        true,
                        false,
                        -64,
                        384,
                        384,
                        BlockTags.INFINIBURN_OVERWORLD,
                        BuiltinDimensionTypes.OVERWORLD_EFFECTS,
                        0,
                        Optional.of(192),
                        new DimensionType.MonsterSettings(
                                true,
                                true,
                                UniformInt.of(0, 7),
                                0)));
        context.register(BuiltinDimensionTypes.NETHER,
                new DimensionType(
                        OptionalLong.of(18000),
                        false,
                        true,
                        true,
                        false,
                        8,
                        false,
                        true,
                        0,
                        256,
                        128,
                        BlockTags.INFINIBURN_NETHER,
                        BuiltinDimensionTypes.NETHER_EFFECTS,
                        0.1F,
                        Optional.empty(),
                        new DimensionType.MonsterSettings(
                                true,
                                true,
                                ConstantInt.of(11),
                                15)));
        context.register(BuiltinDimensionTypes.OVERWORLD_CAVES,
                new DimensionType(
                        OptionalLong.empty(),
                        true,
                        true,
                        false,
                        true,
                        1,
                        true,
                        false,
                        -64,
                        384,
                        384,
                        BlockTags.INFINIBURN_OVERWORLD,
                        BuiltinDimensionTypes.OVERWORLD_EFFECTS,
                        0,
                        Optional.of(192),
                        new DimensionType.MonsterSettings(
                                true,
                                true,
                                UniformInt.of(0, 7),
                                0)));
    }
}
