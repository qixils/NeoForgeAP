package gg.archipelago.aprandomizer.data.recipes;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jetbrains.annotations.Nullable;

public class NoAdvancementsRecipeOutput implements RecipeOutput {

    private final RecipeOutput inner;

    public NoAdvancementsRecipeOutput(RecipeOutput inner) {
        this.inner = inner;
    }

    @Override
    public void accept(ResourceKey<Recipe<?>> key, Recipe<?> recipe, @Nullable AdvancementHolder advancement, ICondition... conditions) {
        inner.accept(key, recipe, null, conditions);
    }

    @Override
    public Advancement.Builder advancement() {
        return inner.advancement();
    }

    @Override
    public void includeRootAdvancement() {

    }

}
