package gg.archipelago.aprandomizer.managers.recipemanager;

import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.Set;

public interface APRecipe {

    Set<RecipeHolder<?>> getGrantedRecipes();

}
