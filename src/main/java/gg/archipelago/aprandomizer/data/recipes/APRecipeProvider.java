package gg.archipelago.aprandomizer.data.recipes;

import gg.archipelago.aprandomizer.APRandomizer;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.criterion.ImpossibleTrigger;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class APRecipeProvider extends RecipeProvider {

    protected APRecipeProvider(Provider registries, RecipeOutput output) {
        super(registries, new NoAdvancementsRecipeOutput(output));
    }

    @Override
    protected void buildRecipes() {
        this.shaped(RecipeCategory.FOOD, Items.ENCHANTED_GOLDEN_APPLE)
                .define('#', Items.GOLD_BLOCK)
                .define('A', Items.APPLE)
                .pattern("###")
                .pattern("#A#")
                .pattern("###")
                .unlockedBy("impossible", CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance()))
                .save(output, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(APRandomizer.MODID, "enchanted_apple")));

    }

    public static class Runner extends RecipeProvider.Runner {

        public Runner(PackOutput packOutput, CompletableFuture<Provider> registries) {
            super(packOutput, registries);
        }

        @Override
        public String getName() {
            return "Archipelago Recipe Provider";
        }

        @Override
        protected RecipeProvider createRecipeProvider(Provider registries, RecipeOutput output) {
            return new APRecipeProvider(registries, output);
        }

    }

}
