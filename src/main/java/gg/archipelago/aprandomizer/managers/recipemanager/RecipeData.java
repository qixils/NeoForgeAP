package gg.archipelago.aprandomizer.managers.recipemanager;

import net.minecraft.world.item.crafting.RecipeHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class RecipeData {

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    HashMap<Long, GroupRecipe> recipes = new HashMap<>() {{
        put(45000L, new GroupRecipe(45000, "archery", new String[]{
                "minecraft:bow",
                "minecraft:arrow",
                "minecraft:crossbow"
        }));
        //put(45001, new GroupRecipe(45001, "Ingot Crafting", new String[]{"minecraft:iron_ingot_from_nuggets", "minecraft:iron_nugget", "minecraft:gold_ingot_from_nuggets", "minecraft:gold_nugget", "minecraft:furnace", "minecraft:blast_furnace"}));
        //put(45002, new GroupRecipe(45002, "Resource Blocks", new String[]{"minecraft:redstone_block", "minecraft:redstone", "minecraft:glowstone", "minecraft:iron_ingot_from_iron_block", "minecraft:iron_block", "minecraft:gold_block", "minecraft:gold_block", "minecraft:gold_ingot_from_gold_block", "minecraft:diamond", "minecraft:diamond_block", "minecraft:netherite_block", "minecraft:netherite_ingot_from_netherite_block", "minecraft:anvil"}));
        put(45003L, new GroupRecipe(45003, "brewing", new String[]{
                "minecraft:blaze_powder",
                "minecraft:brewing_stand"
        }));
        put(45004L, new GroupRecipe(45004, "enchanting", new String[]{
                "minecraft:enchanting_table",
                "minecraft:bookshelf"
        }));
        put(45005L, new GroupRecipe(45005, "bucket", new String[]{
                "minecraft:bucket"
        }));
        put(45006L, new GroupRecipe(45006, "flint_and_steel", new String[]{
                "minecraft:flint_and_steel"
        }));
        put(45007L, new GroupRecipe(45007, "beds", new String[]{
                "minecraft:black_bed",
                "minecraft:blue_bed",
                "minecraft:brown_bed",
                "minecraft:cyan_bed",
                "minecraft:gray_bed",
                "minecraft:green_bed",
                "minecraft:light_blue_bed",
                "minecraft:light_gray_bed",
                "minecraft:lime_bed",
                "minecraft:magenta_bed",
                "minecraft:orange_bed",
                "minecraft:pink_bed",
                "minecraft:purple_bed",
                "minecraft:red_bed",
                "minecraft:white_bed",
                "minecraft:yellow_bed"
        }));
        put(45008L, new GroupRecipe(45008, "bottles", new String[]{
                "minecraft:glass_bottle"
        }));
        put(45009L, new GroupRecipe(45009, "shield", new String[]{
                "minecraft:shield"
        }));
        put(45010L, new GroupRecipe(45010, "fishing", new String[]{
                "minecraft:fishing_rod",
                "minecraft:carrot_on_a_stick",
                "minecraft:warped_fungus_on_a_stick"
        }));
        put(45011L, new GroupRecipe(45011, "campfires", new String[]{
                "minecraft:campfire",
                "minecraft:soul_campfire"
        }));
        put(45044L, new GroupRecipe(45044, "spyglass", new String[]{
                "minecraft:spyglass"
        }));
        put(45045L, new GroupRecipe(45044, "lead", new String[]{
                "minecraft:lead"
        }));
    }};

    HashMap<Long, ProgressiveRecipe> progressiveRecipes = new HashMap<>() {{
        put(45012L,
                new ProgressiveRecipe(45012, "progressive_weapons",
                        new ArrayList<>(
                                Arrays.asList(
                                        new String[]{
                                                "minecraft:stone_sword",
                                                "minecraft:stone_axe"
                                        },
                                        new String[]{
                                                "minecraft:iron_sword",
                                                "minecraft:iron_axe"
                                        },
                                        new String[]{
                                                "minecraft:diamond_sword",
                                                "minecraft:diamond_axe"
                                        }
                                )
                        )
                )
        );
        put(45013L,
                new ProgressiveRecipe(45013, "progressive_tools",
                        new ArrayList<>(
                                Arrays.asList(
                                        new String[]{"minecraft:stone_pickaxe",
                                                "minecraft:stone_shovel",
                                                "minecraft:stone_hoe"
                                        },
                                        new String[]{"minecraft:iron_pickaxe",
                                                "minecraft:iron_shovel",
                                                "minecraft:iron_hoe"
                                        },
                                        new String[]{"minecraft:diamond_pickaxe",
                                                "minecraft:diamond_shovel",
                                                "minecraft:diamond_hoe",
                                                "minecraft:netherite_ingot"}
                                )
                        )
                )
        );
        put(45014L,
                new ProgressiveRecipe(45013, "progressive_armor",
                        new ArrayList<>(
                                Arrays.asList(
                                        new String[]{
                                                "minecraft:iron_helmet",
                                                "minecraft:iron_chestplate",
                                                "minecraft:iron_leggings",
                                                "minecraft:iron_boots"
                                        },
                                        new String[]{"minecraft:diamond_helmet",
                                                "minecraft:diamond_chestplate",
                                                "minecraft:diamond_leggings",
                                                "minecraft:diamond_boots"
                                        }
                                )
                        )
                )
        );
        put(45001L,
                new ProgressiveRecipe(45001, "progressive_resource_crafting",
                        new ArrayList<>(
                                Arrays.asList(
                                        new String[]{
                                                "minecraft:iron_ingot_from_nuggets",
                                                "minecraft:iron_nugget",
                                                "minecraft:gold_ingot_from_nuggets",
                                                "minecraft:gold_nugget",
                                                "minecraft:furnace",
                                                "minecraft:blast_furnace"
                                        },
                                        new String[]{
                                                "minecraft:redstone",
                                                "minecraft:redstone_block",
                                                "minecraft:glowstone",
                                                "minecraft:iron_ingot_from_iron_block",
                                                "minecraft:iron_block",
                                                "minecraft:gold_ingot_from_gold_block",
                                                "minecraft:gold_block",
                                                "minecraft:diamond",
                                                "minecraft:diamond_block",
                                                "minecraft:netherite_block",
                                                "minecraft:netherite_ingot_from_netherite_block",
                                                "minecraft:anvil",
                                                "minecraft:emerald",
                                                "minecraft:emerald_block",
                                                "minecraft:copper_block"
                                        }
                                )
                        )
                )
        );
    }};

    protected boolean injectIRecipe(RecipeHolder<?> iRecipe) {
        for (var entry : recipes.entrySet()) {
            for (String namespaceID : entry.getValue().namespaceIDs) {
                LOGGER.trace("checking {} vs {},", iRecipe.id().toString(), namespaceID);
                if (iRecipe.id().toString().equals(namespaceID)) {
                    entry.getValue().addIRecipe(iRecipe);
                    return true;
                }
            }
        }
        for (var entry : progressiveRecipes.entrySet()) {
            for (int i = 0; entry.getValue().namespaceIDs.size() > i; ++i) {
                String[] namespaceIDs = entry.getValue().namespaceIDs.get(i);
                for (String s : namespaceIDs) {
                    LOGGER.trace("checking {} vs {},", iRecipe.id().toString(), s);
                    if (iRecipe.id().toString().equals(s)) {
                        entry.getValue().addIRecipe(iRecipe, i);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean hasID(long id) {
        return recipes.containsKey(id) || progressiveRecipes.containsKey(id);
    }

    public APRecipe getID(long id) {
        if (recipes.containsKey(id)) {
            return recipes.get(id);
        } else if (progressiveRecipes.containsKey(id)) {
            return progressiveRecipes.get(id);
        }
        return null;
    }

    //our reset here is simple just reset what tier it thinks our progressive recipes are at.
    public void reset() {
        progressiveRecipes.forEach((id, recipe) -> recipe.setCurrentTier(0));
    }
}
