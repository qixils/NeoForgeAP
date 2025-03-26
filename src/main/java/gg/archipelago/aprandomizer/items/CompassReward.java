package gg.archipelago.aprandomizer.items;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gg.archipelago.aprandomizer.structures.level.StructureLevelReference;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;

public record CompassReward(TagKey<Structure> structures, StructureLevelReference level) implements APReward {

    public static final MapCodec<CompassReward> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(
                    TagKey.hashedCodec(Registries.STRUCTURE).fieldOf("structures").forGetter(CompassReward::structures),
                    StructureLevelReference.CODEC.fieldOf("level").forGetter(CompassReward::level))
            .apply(instance, CompassReward::new));

    @Override
    public MapCodec<? extends APReward> codec() {
        return CODEC;
    }

}
