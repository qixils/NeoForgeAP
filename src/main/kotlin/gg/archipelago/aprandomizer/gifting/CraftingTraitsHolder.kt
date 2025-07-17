package gg.archipelago.aprandomizer.gifting

import net.minecraft.core.Holder
import net.minecraft.core.HolderLookup
import net.minecraft.util.context.ContextMap
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeHolder
import net.minecraft.world.item.crafting.RecipeInput
import net.minecraft.world.item.crafting.RecipeManager
import net.minecraft.world.item.crafting.RecipeType

class CraftingTraitsHolder(
    private val itemTraits: context(HolderLookup.RegistryLookup<GiftTraitDefinition>)
        (Holder<Item>, RecipeManager) -> Collection<GiftTraitWrapper>,
    private val materialTraitMinimumRecipes: Int = 3,
    private val materialTraitQualityPerRecipe: Float = 0.1f,
    private val materialTraitMaxValue: Float = 3f,
) {
    private val cache: MutableMap<Item, List<GiftTraitWrapper>> = mutableMapOf()


    //todo figure out proper ordering
    context(_: HolderLookup.RegistryLookup<GiftTraitDefinition>)
    fun resolveTraits(item: Item, recipeManager: RecipeManager): List<GiftTraitWrapper> {
        if (cache.containsKey(item)) {
            return cache[item]!!
        }
        cache[item] = emptyList() // todo find better way to avoid infinite recursion
        val recipeMap = recipeManager.recipeMap()
        // find recipes outputting the item
        val recipesMaking = recipeMap.byType(RecipeType.CRAFTING).mapNotNull { recipe ->
            recipe.value.display().flatMap {
                it.result().resolveForStacks(ContextMap.EMPTY)
            }.find { it.item == item }?.let { recipe to it }
        } + recipeMap.byType(RecipeType.SMELTING).mapNotNull { recipe ->
            recipe.value.display().flatMap {
                it.result().resolveForStacks(ContextMap.EMPTY)
            }.find { it.item == item }?.let { recipe to it }
        }

        //find recipes which use the item as an ingredient
        val recipesUsing = recipeMap.byType(RecipeType.CRAFTING).filter { recipe ->
            recipe.value.placementInfo().ingredients().any { ing ->
                if (ing.isCustom) {
                    ing.customIngredient!!.items().anyMatch { it.value() == item }
                } else {
                    ing.values.any { it.value() == item }
                }
            }
        } + recipeMap.byType(RecipeType.SMELTING).filter { recipe ->
            recipe.value.placementInfo().ingredients().any { ing ->
                if (ing.isCustom) {
                    ing.customIngredient!!.items().anyMatch { it.value() == item }
                } else {
                    ing.values.any { it.value() == item }
                }
            }
        }


        val materialTrait = if (recipesUsing.count() > materialTraitMinimumRecipes) {
            val quality = (recipesUsing.count() * materialTraitQualityPerRecipe).coerceAtMost(materialTraitMaxValue)
            listOf(
                KnownTraits.Material.with(
                    quality = quality, source = GiftTraitSource.CRAFTING
                )
            )
        } else emptyList()
        val recipeTraits = recipesMaking.map { (r, o) -> r to recipeTraits(r, o,recipeManager) }
        val finalInheritedTraits = recipeTraits.map { (recipe, traits) ->
            // keep traits that are inheritable and above a certain quality threshold
            traits.filter { t ->
                val inheritance = t.definition.value().craftingInheritance[recipe.value.type]
                inheritance != null && (t.quality == null || t.quality >= inheritance.qualityThreshold)
            }.map { t ->
                val inheritance = t.definition.value().craftingInheritance[recipe.value.type]!!
                t.copy(
                    quality = (t.quality ?: inheritance.inheritanceDefaultQuality)
                        .coerceIn(inheritance.minQuality, inheritance.maxQuality),
                    duration = (t.duration ?: inheritance.inheritanceDefaultDuration)
                        .coerceIn(inheritance.minDuration, inheritance.maxDuration)
                )
            }
        }.reduceOrNull { acc, tl ->
            // only keep traits that are common to all recipes
            val commonDefs = acc.map { it.definition }.intersect(tl.map { it.definition })
            (acc + tl).filter { it.definition in commonDefs }
        }.orEmpty()
            .groupBy { it.definition }
            .map { (def, traits) -> // keep lowest values for each trait
                GiftTraitWrapper(
                    definition = def,
                    quality = traits.mapNotNull { it.quality }.minOrNull(),
                    duration = traits.mapNotNull { it.duration }.minOrNull(),
                    source = GiftTraitSource.CRAFTING
                )
            }
        return (finalInheritedTraits + materialTrait).also { cache[item] = it }
    }

    context(traitLookup: HolderLookup.RegistryLookup<GiftTraitDefinition>) private fun recipeTraits(
        recipe: RecipeHolder<out Recipe<out RecipeInput>>, output: ItemStack, recipeManager: RecipeManager
    ): List<GiftTraitWrapper> {
        val ingredients =
            recipe.value.placementInfo().ingredients().map { ing -> // list of "actual" ingredient in each slot
                if (ing.isCustom) {
                    ing.customIngredient!!.items().toList()
                } else ing.values.toList()
            }

        val ingredientTraitsPerSlot = ingredients.map { possibleIngredientsInSlot -> // for each slot
            possibleIngredientsInSlot.map { itemTraits(it, recipeManager) } // turn each ingredient into its traits
                .reduceOrNull { acc, traits -> // only take traits that are common to alternative possible ingredients in this slot
                    val commonDefs = acc.map { it.definition }.intersect(traits.map { it.definition })
                    (acc + traits).filter { it.definition in commonDefs }
                }.orEmpty()
        }
        val traitsFromAllIngredients = ingredientTraitsPerSlot.map { slotTraits ->
            slotTraits.groupBy { it.definition }.mapNotNull { (traitDef, traitList) ->
                val inheritance =
                    traitDef.value().craftingInheritance[recipe.value.type] ?: return@mapNotNull null
                val quality = traitList.mapNotNull { it.quality }.minOrNull()
                val duration = traitList.mapNotNull { it.duration }.minOrNull()

                GiftTraitWrapper(
                    definition = traitDef,
                    quality = quality?.times(inheritance.qualityKeptMultiplier),
                    duration = duration?.times(inheritance.durationKeptMultiplier),
                    source = GiftTraitSource.CRAFTING
                )
            }
        }.map { slotTraits ->
            slotTraits.map { trait -> // divide quality and duration by the recipe result amount
                GiftTraitWrapper(
                    definition = trait.definition,
                    source = GiftTraitSource.CRAFTING,
                    quality = trait.quality?.div(output.count),
                    duration = trait.duration?.div(output.count)
                )
            }
        }.flatten()
            .groupBy { it.definition }
            .mapNotNull { (d, t) ->
                val inheritance = d.value().craftingInheritance[recipe.value.type] ?: return@mapNotNull null
                GiftTraitWrapper(
                    definition = d,
                    quality = t.map { it.quality ?: inheritance.inheritanceDefaultQuality }.sum(),
                    duration = t.map { it.duration ?: inheritance.inheritanceDefaultDuration }.sum(),
                    source = GiftTraitSource.CRAFTING
                )
            }
        return traitsFromAllIngredients
    }
}
