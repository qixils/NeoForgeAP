package gg.archipelago.aprandomizer.structures.level;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public record RandomizedStructureLevel(ResourceLocation name) implements StructureLevelReference {

    public static final MapCodec<RandomizedStructureLevel> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(
                    ResourceLocation.CODEC.fieldOf("name").forGetter(RandomizedStructureLevel::name))
            .apply(instance, RandomizedStructureLevel::new));

    @Override
    public MapCodec<? extends StructureLevelReference> codec() {
        return CODEC;
    }

}
