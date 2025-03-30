package gg.archipelago.aprandomizer.locations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Function;

public interface APLocation {
    public static final Codec<APLocation> CODEC = APLocationTypes.REGISTRY.byNameCodec().dispatch(APLocation::codec, Function.identity());

    MapCodec<? extends APLocation> codec();

    void give(ServerPlayer player);

    boolean hasFound(ServerPlayer player);
}
