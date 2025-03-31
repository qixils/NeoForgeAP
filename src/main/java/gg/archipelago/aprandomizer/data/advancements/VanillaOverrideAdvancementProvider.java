package gg.archipelago.aprandomizer.data.advancements;

import gg.archipelago.aprandomizer.APStructures;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.HolderLookup.RegistryLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.function.Consumer;

public class VanillaOverrideAdvancementProvider implements AdvancementSubProvider {

    @Override
    public void generate(Provider registries, Consumer<AdvancementHolder> writer) {
        RegistryLookup<Structure> structures = registries.lookupOrThrow(Registries.STRUCTURE);
        Advancement.Builder.advancement()
                .parent(ResourceLocation.withDefaultNamespace("end/enter_end_gateway"))
                .addCriterion("in_city", PlayerTrigger.TriggerInstance.located(
                        LocationPredicate.Builder.inStructure(structures.getOrThrow(BuiltinStructures.END_CITY))))
                .addCriterion("in_city_nether", PlayerTrigger.TriggerInstance.located(
                        LocationPredicate.Builder.inStructure(structures.getOrThrow(APStructures.END_CITY_NETHER_STRUCTURE))))
                .display(
                        Items.PURPUR_BLOCK,
                        Component.translatable("advancements.end.find_end_city.title"),
                        Component.translatable("advancements.end.find_end_city.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false)
                .requirements(AdvancementRequirements.Strategy.OR)
                .save(writer, ResourceLocation.withDefaultNamespace("end/find_end_city"));
    }

}
