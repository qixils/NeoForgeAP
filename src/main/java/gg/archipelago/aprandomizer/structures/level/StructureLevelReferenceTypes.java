package gg.archipelago.aprandomizer.structures.level;

import com.mojang.serialization.MapCodec;
import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.APRegistries;
import net.minecraft.core.Registry;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class StructureLevelReferenceTypes {
    public static final DeferredRegister<MapCodec<? extends StructureLevelReference>> REGISTER = DeferredRegister.create(APRegistries.STRUCTURE_LEVEL_REFERENCE, APRandomizer.MODID);

    public static final Registry<MapCodec<? extends StructureLevelReference>> REGISTRY = REGISTER.makeRegistry(builder -> {});

    public static final DeferredHolder<MapCodec<? extends StructureLevelReference>, MapCodec<ConstantLevel>> CONSTANT = REGISTER.register("constant", () -> ConstantLevel.CODEC);
    public static final DeferredHolder<MapCodec<? extends StructureLevelReference>, MapCodec<RandomizedStructureLevel>> RANDOMIZED_STRUCTURE_LEVEL = REGISTER.register("randomized_structure_level", () -> RandomizedStructureLevel.CODEC);
}
