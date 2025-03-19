package gg.archipelago.aprandomizer.locations;

import com.mojang.serialization.MapCodec;
import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.APRegistries;
import net.minecraft.core.Registry;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class APLocationTypes {
    public static final DeferredRegister<MapCodec<? extends APLocation>> REGISTER = DeferredRegister.create(APRegistries.ARCHIPELAGO_LOCATION_TYPE, APRandomizer.MODID);
    
    public static final Registry<MapCodec<? extends APLocation>> REGISTRY = REGISTER.makeRegistry(builder -> {});
    
    public static final DeferredHolder<MapCodec<? extends APLocation>, MapCodec<AdvancementLocation>> ADVANCEMENT = REGISTER.register("advancement", () -> AdvancementLocation.CODEC);
}
