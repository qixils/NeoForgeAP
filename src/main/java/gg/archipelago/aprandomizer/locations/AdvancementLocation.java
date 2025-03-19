package gg.archipelago.aprandomizer.locations;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public record AdvancementLocation(ResourceLocation advancement) implements APLocation {
    public static final MapCodec<AdvancementLocation> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(
                    ResourceLocation.CODEC.fieldOf("advancement").forGetter(AdvancementLocation::advancement))
            .apply(instance, AdvancementLocation::new));

    @Override
    public MapCodec<? extends APLocation> codec() {
        return CODEC;
    }
}
