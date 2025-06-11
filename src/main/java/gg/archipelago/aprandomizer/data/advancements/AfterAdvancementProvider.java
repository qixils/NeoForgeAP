package gg.archipelago.aprandomizer.data.advancements;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class AfterAdvancementProvider implements AdvancementSubProvider {

    private final List<AdvancementSubProvider> baseAdvancements;
    private final Function<ResourceLocation, ResourceLocation> idConverter;

    public AfterAdvancementProvider(List<AdvancementSubProvider> baseAdvancements, Function<ResourceLocation, ResourceLocation> idConverter) {
        this.baseAdvancements = baseAdvancements;
        this.idConverter = idConverter;
    }

    @Override
    public void generate(Provider registries, Consumer<AdvancementHolder> writer) {
        Object2BooleanMap<ResourceLocation> noChildren = new Object2BooleanOpenHashMap<>();
        for (AdvancementSubProvider provider : baseAdvancements) {
            provider.generate(registries, holder -> {
                if (holder.value().display().isPresent() && !holder.value().display().get().isHidden()) {
                    if (!noChildren.containsKey(holder.id())) {
                        noChildren.put(holder.id(), true);
                    }
                    if (holder.value().parent().isPresent()) {
                        noChildren.put(holder.value().parent().get(), false);
                    }
                }
            });
        }

        for (Object2BooleanMap.Entry<ResourceLocation> entry : noChildren.object2BooleanEntrySet()) {
            if (entry.getBooleanValue()) {
                Advancement.Builder.recipeAdvancement()
                        .parent(entry.getKey())
                        .addCriterion("auto", PlayerTrigger.TriggerInstance.tick())
                        .save(writer, this.idConverter.apply(entry.getKey()));
            }
        }
    }

}
