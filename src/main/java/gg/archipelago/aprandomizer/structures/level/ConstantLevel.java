package gg.archipelago.aprandomizer.structures.level;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public record ConstantLevel(ResourceKey<Level> level) implements StructureLevelReference {
    public static final MapCodec<ConstantLevel> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(
                    ResourceKey.codec(Registries.DIMENSION).fieldOf("level").forGetter(ConstantLevel::level))
            .apply(instance, ConstantLevel::new));

    @Override
    public MapCodec<? extends StructureLevelReference> codec() {
        return CODEC;
    }

}
