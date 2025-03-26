package gg.archipelago.aprandomizer.structures.level;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import java.util.function.Function;

public interface StructureLevelReference {
    public static final Codec<StructureLevelReference> CODEC = StructureLevelReferenceTypes.REGISTRY.byNameCodec().dispatch(StructureLevelReference::codec, Function.identity());

    MapCodec<? extends StructureLevelReference> codec();
}
