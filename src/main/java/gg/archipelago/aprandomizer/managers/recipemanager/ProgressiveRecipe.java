package gg.archipelago.aprandomizer.managers.recipemanager;

import gg.archipelago.aprandomizer.APRandomizer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProgressiveRecipe implements APRecipe {

    final int id;
    final String trackingAdvancementBase;
    final ArrayList<String[]> namespaceIDs;
    final List<Set<RecipeHolder<?>>> recipes = new ArrayList<>();
    int currentTier = 0;
    int totalTiers;

    ProgressiveRecipe(int id, String trackingAchievement, ArrayList<String[]> namespaceIDs) {
        super();
        this.id = id;
        this.namespaceIDs = namespaceIDs;
        this.totalTiers = namespaceIDs.size();
        this.trackingAdvancementBase = trackingAchievement;
        namespaceIDs.forEach((namespaceID) -> recipes.add(new HashSet<>()));
    }

    protected void addIRecipe(RecipeHolder<?> iRecipe, int tier) {
        this.recipes.get(tier).add(iRecipe);
    }

    public int getCurrentTier() {
        return currentTier;
    }

    public void setCurrentTier(int tier) {
        this.currentTier = tier;
    }

    public Set<RecipeHolder<?>> getTier(int tier) {
        if (recipes.size() >= tier)
            return recipes.get(tier - 1);
        return recipes.getLast();
    }


    @Override
    public Set<RecipeHolder<?>> getGrantedRecipes() {
        return getTier(++currentTier);
    }

    @Override
    public Set<ResourceLocation> getUnlockedTrackingAdvancements() {
        HashSet<ResourceLocation> trackingAdvancements = new HashSet<>();
        for (int i = 1; i <= currentTier; i++) {
            trackingAdvancements.add(ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID,"received/"+ trackingAdvancementBase + "_" + i));
        }
        return trackingAdvancements;
    }
}
