package gg.archipelago.aprandomizer.datacomponents;

import gg.archipelago.aprandomizer.APRandomizer;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class APDataComponents {
    // The specialized DeferredRegister.DataComponents simplifies data component registration and avoids some generic inference issues with the `DataComponentType.Builder` within a `Supplier`
    public static final DeferredRegister.DataComponents REGISTER = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, APRandomizer.MODID);

    public static final Supplier<DataComponentType<TrackedStructure>> TRACKED_STRUCTURE = REGISTER.registerComponentType(
            "tracked_structure",
            builder -> builder.persistent(TrackedStructure.CODEC).networkSynchronized(TrackedStructure.STREAM_CODEC)
    );
}
