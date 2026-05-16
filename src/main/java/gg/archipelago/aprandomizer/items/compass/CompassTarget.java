package gg.archipelago.aprandomizer.items.compass;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

import java.util.Optional;
import java.util.function.Function;

public interface CompassTarget {
    Codec<CompassTarget> CODEC = CompassTargetTypes.REGISTRY.byNameCodec().dispatch(CompassTarget::codec, Function.identity());

    public Optional<BlockPos> findTarget(ServerLevel level, BlockPos start);

    public MapCodec<? extends CompassTarget> codec();
}
