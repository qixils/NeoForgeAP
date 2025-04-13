package gg.archipelago.aprandomizer.managers.recipemanager;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.Set;

public interface APRecipe {

    Set<RecipeHolder<?>> getGrantedRecipes();

    Set<ResourceLocation> getUnlockedTrackingAdvancements();
}
