package gg.archipelago.aprandomizer.locations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import java.util.function.Function;

public interface APLocation {
    public static final Codec<APLocation> CODEC = APLocationTypes.REGISTRY.byNameCodec().dispatch(APLocation::codec, Function.identity());

    MapCodec<? extends APLocation> codec();
}
