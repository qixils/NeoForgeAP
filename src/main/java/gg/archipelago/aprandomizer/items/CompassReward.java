package gg.archipelago.aprandomizer.items;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gg.archipelago.aprandomizer.structures.level.StructureLevelReference;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;

public record CompassReward(TagKey<Structure> structures, StructureLevelReference level, Component name) implements APReward {

    public static final MapCodec<CompassReward> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(
                    TagKey.hashedCodec(Registries.STRUCTURE).fieldOf("structures").forGetter(CompassReward::structures),
                    StructureLevelReference.CODEC.fieldOf("level").forGetter(CompassReward::level),
                    ComponentSerialization.CODEC.fieldOf("name").forGetter(CompassReward::name))
            .apply(instance, CompassReward::new));

    public static final Codec<CompassReward> CODEC = MAP_CODEC.codec();

    @Override
    public MapCodec<? extends APReward> codec() {
        return MAP_CODEC;
    }

}
