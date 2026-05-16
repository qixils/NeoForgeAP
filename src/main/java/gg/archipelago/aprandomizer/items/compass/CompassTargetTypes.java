package gg.archipelago.aprandomizer.items.compass;

import com.mojang.serialization.MapCodec;
import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.APRegistries;
import net.minecraft.core.Registry;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CompassTargetTypes {
    public static final DeferredRegister<MapCodec<? extends CompassTarget>> REGISTER = DeferredRegister.create(APRegistries.COMPASS_TARGET_TYPE, APRandomizer.MODID);

    public static final Registry<MapCodec<? extends CompassTarget>> REGISTRY = REGISTER.makeRegistry(builder -> {});

    public static final DeferredHolder<MapCodec<? extends CompassTarget>, MapCodec<StructureTarget>> STRUCTURE = REGISTER.register("structure", () -> StructureTarget.CODEC);
    public static final DeferredHolder<MapCodec<? extends CompassTarget>, MapCodec<BiomeTarget>> BIOME = REGISTER.register("biome", () -> BiomeTarget.CODEC);
    public static final DeferredHolder<MapCodec<? extends CompassTarget>, MapCodec<UnvisitedBiomeTarget>> UNVISITED_BIOME = REGISTER.register("unvisited_biome", () -> UnvisitedBiomeTarget.CODEC);

}
