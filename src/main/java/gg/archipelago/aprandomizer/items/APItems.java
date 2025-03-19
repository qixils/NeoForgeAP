package gg.archipelago.aprandomizer.items;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.APRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class APItems {
    // Group Recipes
    public static final ResourceKey<APItem> GROUP_RECIPES_ARCHERY = id("group_recipes/archery");
    public static final ResourceKey<APItem> GROUP_RECIPES_BREWING = id("group_recipes/brewing");
    public static final ResourceKey<APItem> GROUP_RECIPES_ENCHANTING = id("group_recipes/enchanting");
    public static final ResourceKey<APItem> GROUP_RECIPES_BUCKET = id("group_recipes/bucket");
    public static final ResourceKey<APItem> GROUP_RECIPES_FLINT_AND_STEEL = id("group_recipes/flint_and_steel");
    public static final ResourceKey<APItem> GROUP_RECIPES_BEDS = id("group_recipes/beds");
    public static final ResourceKey<APItem> GROUP_RECIPES_BOTTLES = id("group_recipes/bottles");
    public static final ResourceKey<APItem> GROUP_RECIPES_SHIELD = id("group_recipes/shield");
    public static final ResourceKey<APItem> GROUP_RECIPES_FISHING = id("group_recipes/fishing");
    public static final ResourceKey<APItem> GROUP_RECIPES_CAMPFIRES = id("group_recipes/campfires");
    public static final ResourceKey<APItem> GROUP_RECIPES_SPYGLASS = id("group_recipes/spyglass");
    public static final ResourceKey<APItem> GROUP_RECIPES_LEAD = id("group_recipes/lead");

    // Progressive Recipes
    public static final ResourceKey<APItem> PROGRESSIVE_RECIPES_WEAPONS = id("progressive_recipes/weapons");
    public static final ResourceKey<APItem> PROGRESSIVE_RECIPES_TOOLS = id("progressive_recipes/tools");
    public static final ResourceKey<APItem> PROGRESSIVE_RECIPES_ARMOR = id("progressive_recipes/armor");
    public static final ResourceKey<APItem> PROGRESSIVE_RECIPES_RESOURCE_CRAFTING = id("progressive_recipes/resource_crafting");

    private static ResourceKey<APItem> id(String name) {
        return ResourceKey.create(APRegistries.ARCHIPELAGO_ITEM, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, name));
    }
}
