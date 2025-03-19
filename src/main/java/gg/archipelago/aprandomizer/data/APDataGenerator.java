package gg.archipelago.aprandomizer.data;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.APStructures;
import gg.archipelago.aprandomizer.data.advancements.APAdvancementProvider;
import gg.archipelago.aprandomizer.data.advancements.ReceivedAdvancementProvider;
import gg.archipelago.aprandomizer.data.datamaps.APDataMapProvider;
import gg.archipelago.aprandomizer.data.recipes.APRecipeProvider;
import gg.archipelago.aprandomizer.data.tags.APBiomeTagsProvider;
import gg.archipelago.aprandomizer.data.tags.APDamageTypeTagsProvider;
import gg.archipelago.aprandomizer.data.tags.APStructureTagsProvider;
import gg.archipelago.aprandomizer.modifiers.APStructureModifiers;
import gg.archipelago.aprandomizer.structures.APStructureSets;
import gg.archipelago.aprandomizer.structures.APTemplatePools;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.advancements.AdvancementProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(bus = Bus.MOD, modid = APRandomizer.MODID)
public class APDataGenerator {

    @SubscribeEvent
    public static void onDataGen(GatherDataEvent.Client event) {
        CompletableFuture<HolderLookup.Provider> registries = event.addProvider(new DatapackBuiltinEntriesProvider(event.getGenerator().getPackOutput(), event.getLookupProvider(),
                new RegistrySetBuilder()
                        .add(NeoForgeRegistries.Keys.STRUCTURE_MODIFIERS, APStructureModifiers::bootstrap)
                        .add(Registries.STRUCTURE, APStructures::bootstrap)
                        .add(Registries.TEMPLATE_POOL, APTemplatePools::bootstrap)
                        .add(Registries.STRUCTURE_SET, APStructureSets::bootstrap),
                Set.of(APRandomizer.MODID))).getRegistryProvider();
        event.addProvider(new AdvancementProvider(event.getGenerator().getPackOutput(), registries, List.of(
                new APAdvancementProvider(),
                new ReceivedAdvancementProvider())));
        event.addProvider(new APDamageTypeTagsProvider(event.getGenerator().getPackOutput(), registries));
        event.addProvider(new APRecipeProvider.Runner(event.getGenerator().getPackOutput(), registries));
        event.addProvider(new APDataMapProvider(event.getGenerator().getPackOutput(), registries));
        event.addProvider(new APBiomeTagsProvider(event.getGenerator().getPackOutput(), registries));
        event.addProvider(new APStructureTagsProvider(event.getGenerator().getPackOutput(), registries));
    }
}
