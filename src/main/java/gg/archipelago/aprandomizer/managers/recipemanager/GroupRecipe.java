package gg.archipelago.aprandomizer.managers.recipemanager;

import gg.archipelago.aprandomizer.APRandomizer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.HashSet;
import java.util.Set;

public class GroupRecipe implements APRecipe {
    final int id;
    final ResourceLocation trackingAdvancement;
    final String[] namespaceIDs;
    final Set<RecipeHolder<?>> iRecipes = new HashSet<>();

    GroupRecipe(int id, String trackingAdvancement, String[] namespaceIDs) {
        this.id = id;
        this.trackingAdvancement = ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID,"received/"+trackingAdvancement);
        this.namespaceIDs = namespaceIDs;
    }

    protected void addIRecipe(RecipeHolder<?> iRecipe) {
        this.iRecipes.add(iRecipe);
    }

    public Set<RecipeHolder<?>> getIRecipes() {
        return iRecipes;
    }

    @Override
    public Set<RecipeHolder<?>> getGrantedRecipes() {
        return iRecipes;
    }

    @Override
    public Set<ResourceLocation> getUnlockedTrackingAdvancements() {
        return Set.of(trackingAdvancement);
    }

}
