package gg.archipelago.aprandomizer.data.advancements;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.advancements.ReceivedItemCriteria;
import gg.archipelago.aprandomizer.items.APItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

public class ReceivedAdvancementProvider implements AdvancementSubProvider {

    @Override
    public void generate(Provider registries, Consumer<AdvancementHolder> writer) {
        AdvancementHolder root = Advancement.Builder.recipeAdvancement()
                .display(
                        Items.STRUCTURE_BLOCK,
                        Component.literal("Received Items"),
                        Component.literal("This tab will track items that you have received from Archipelago"),
                        ResourceLocation.withDefaultNamespace("block/basalt_side"),
                        AdvancementType.TASK,
                        false,
                        false,
                        false)
                .addCriterion("auto", PlayerTrigger.TriggerInstance.tick())
                .save(writer, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "received/root"));

        AdvancementHolder archery = Advancement.Builder.recipeAdvancement()
                .display(
                        Items.BOW,
                        Component.literal("Archery"),
                        Component.literal("Bow\nArrow\nCrossbow"),
                        null,
                        AdvancementType.TASK,
                        true,
                        false,
                        false)
                .parent(root)
                .addCriterion("received", ReceivedItemCriteria.TriggerInstance.receivedItem(APItems.GROUP_RECIPES_ARCHERY))
                .save(writer, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "received/archery"));

        AdvancementHolder beds = Advancement.Builder.recipeAdvancement()
                .display(
                        Items.PINK_BED,
                        Component.literal("Beds"),
                        Component.empty(),
                        null,
                        AdvancementType.TASK,
                        true,
                        false,
                        false)
                .parent(root)
                .addCriterion("received", ReceivedItemCriteria.TriggerInstance.receivedItem(APItems.GROUP_RECIPES_BEDS))
                .save(writer, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "received/beds"));

        AdvancementHolder bottles = Advancement.Builder.recipeAdvancement()
                .display(
                        Items.GLASS_BOTTLE,
                        Component.literal("Bottles"),
                        Component.empty(),
                        null,
                        AdvancementType.TASK,
                        true,
                        false,
                        false)
                .parent(root)
                .addCriterion("received", ReceivedItemCriteria.TriggerInstance.receivedItem(APItems.GROUP_RECIPES_BOTTLES))
                .save(writer, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "received/bottles"));

        AdvancementHolder brewing = Advancement.Builder.recipeAdvancement()
                .display(
                        Items.BREWING_STAND,
                        Component.literal("Brewing"),
                        Component.literal("Brewing Stand\nBlaze Powder"),
                        null,
                        AdvancementType.TASK,
                        true,
                        false,
                        false)
                .parent(root)
                .addCriterion("received", ReceivedItemCriteria.TriggerInstance.receivedItem(APItems.GROUP_RECIPES_BREWING))
                .save(writer, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "received/brewing"));

        AdvancementHolder bucket = Advancement.Builder.recipeAdvancement()
                .display(
                        Items.BUCKET,
                        Component.literal("Bucket"),
                        Component.empty(),
                        null,
                        AdvancementType.TASK,
                        true,
                        false,
                        false)
                .parent(root)
                .addCriterion("received", ReceivedItemCriteria.TriggerInstance.receivedItem(APItems.GROUP_RECIPES_BUCKET))
                .save(writer, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "received/bucket"));

        AdvancementHolder campfires = Advancement.Builder.recipeAdvancement()
                .display(
                        Items.CAMPFIRE,
                        Component.literal("Campfires"),
                        Component.literal("Campfire\nSoul Campfire"),
                        null,
                        AdvancementType.TASK,
                        true,
                        false,
                        false)
                .parent(root)
                .addCriterion("received", ReceivedItemCriteria.TriggerInstance.receivedItem(APItems.GROUP_RECIPES_CAMPFIRES))
                .save(writer, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "received/campfires"));

        AdvancementHolder enchanting = Advancement.Builder.recipeAdvancement()
                .display(
                        Items.ENCHANTING_TABLE,
                        Component.literal("Enchanting"),
                        Component.literal("Enchanting Table\nBookshelf"),
                        null,
                        AdvancementType.TASK,
                        true,
                        false,
                        false)
                .parent(root)
                .addCriterion("received", ReceivedItemCriteria.TriggerInstance.receivedItem(APItems.GROUP_RECIPES_ENCHANTING))
                .save(writer, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "received/enchanting"));

        AdvancementHolder fishing = Advancement.Builder.recipeAdvancement()
                .display(
                        Items.FISHING_ROD,
                        Component.literal("Fishing Rods"),
                        Component.literal("Fishing Rod\nCarrot on a Stick\nWarped Fungus on a Stick"),
                        null,
                        AdvancementType.TASK,
                        true,
                        false,
                        false)
                .parent(root)
                .addCriterion("received", ReceivedItemCriteria.TriggerInstance.receivedItem(APItems.GROUP_RECIPES_FISHING))
                .save(writer, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "received/fishing"));

        AdvancementHolder flintAndSteel = Advancement.Builder.recipeAdvancement()
                .display(
                        Items.FLINT_AND_STEEL,
                        Component.literal("Flint & Steel"),
                        Component.empty(),
                        null,
                        AdvancementType.TASK,
                        true,
                        false,
                        false)
                .parent(root)
                .addCriterion("received", ReceivedItemCriteria.TriggerInstance.receivedItem(APItems.GROUP_RECIPES_FLINT_AND_STEEL))
                .save(writer, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "received/flint_and_steel"));

        AdvancementHolder lead = Advancement.Builder.recipeAdvancement()
                .display(
                        Items.LEAD,
                        Component.literal("Lead"),
                        Component.empty(),
                        null,
                        AdvancementType.TASK,
                        true,
                        false,
                        false)
                .parent(root)
                .addCriterion("received", ReceivedItemCriteria.TriggerInstance.receivedItem(APItems.GROUP_RECIPES_LEAD))
                .save(writer, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "received/lead"));

        AdvancementHolder brush = Advancement.Builder.recipeAdvancement()
                .display(
                        Items.BRUSH,
                        Component.literal("Brush"),
                        Component.empty(),
                        null,
                        AdvancementType.TASK,
                        true,
                        false,
                        false)
                .parent(root)
                .addCriterion("received", ReceivedItemCriteria.TriggerInstance.receivedItem(APItems.GROUP_RECIPES_BRUSH))
                .save(writer, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "received/brush"));

        AdvancementHolder progressiveArmor1 = Advancement.Builder.recipeAdvancement()
                .display(
                        Items.IRON_CHESTPLATE,
                        Component.literal("Progressive Armor 1"),
                        Component.literal("Iron:\n  Helmet\n  Chestplate\n  Leggings\n  Boots"),
                        null,
                        AdvancementType.TASK,
                        true,
                        false,
                        false)
                .parent(root)
                .addCriterion("received", ReceivedItemCriteria.TriggerInstance.receivedItem(APItems.PROGRESSIVE_RECIPES_ARMOR, 1))
                .save(writer, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "received/progressive_armor_1"));

        AdvancementHolder progressiveArmor2 = Advancement.Builder.recipeAdvancement()
                .display(
                        Items.DIAMOND_CHESTPLATE,
                        Component.literal("Progressive Armor 2"),
                        Component.literal("Diamond:\n  Helmet\n  Chestplate\n  Leggings\n  Boots"),
                        null,
                        AdvancementType.TASK,
                        true,
                        false,
                        false)
                .parent(progressiveArmor1)
                .addCriterion("received", ReceivedItemCriteria.TriggerInstance.receivedItem(APItems.PROGRESSIVE_RECIPES_ARMOR, 2))
                .save(writer, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "received/progressive_armor_2"));

        AdvancementHolder progressiveArmorAfter = Advancement.Builder.recipeAdvancement()
                .parent(progressiveArmor2)
                .addCriterion("auto", PlayerTrigger.TriggerInstance.tick())
                .save(writer, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "received/progressive_armor_after"));

        AdvancementHolder progressiveResourceCrafting1 = Advancement.Builder.recipeAdvancement()
                .display(
                        Items.BLAST_FURNACE,
                        Component.literal("Progressive Resource Crafting 1"),
                        Component.literal("Iron Nuggets <-> Bars\n  Gold Nuggets <-> Bars\n  Furnace\n  Blast Furnace"),
                        null,
                        AdvancementType.TASK,
                        true,
                        false,
                        false)
                .parent(root)
                .addCriterion("received", ReceivedItemCriteria.TriggerInstance.receivedItem(APItems.PROGRESSIVE_RECIPES_RESOURCE_CRAFTING, 1))
                .save(writer, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "received/progressive_resource_crafting_1"));

        AdvancementHolder progressiveResourceCrafting2 = Advancement.Builder.recipeAdvancement()
                .display(
                        Items.ANVIL,
                        Component.literal("Progressive Resource Crafting 2"),
                        Component.literal("Anvil\n  Redstone Blocks <-> Dust\n  Glowstone Dust -> Blocks\n  Iron Ingots <-> Blocks\n  Gold Ingots <-> Blocks\n  Diamond Ingots <-> Blocks\n  Nethrite Ingots <-> Blocks\n  Emeralds <-> Blocks\n  Copper Ingots -> Blocks"),
                        null,
                        AdvancementType.TASK,
                        true,
                        false,
                        false)
                .parent(progressiveResourceCrafting1)
                .addCriterion("received", ReceivedItemCriteria.TriggerInstance.receivedItem(APItems.PROGRESSIVE_RECIPES_RESOURCE_CRAFTING, 2))
                .save(writer, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "received/progressive_resource_crafting_2"));

        AdvancementHolder progressiveResourceCraftingAfter = Advancement.Builder.recipeAdvancement()
                .parent(progressiveResourceCrafting2)
                .addCriterion("auto", PlayerTrigger.TriggerInstance.tick())
                .save(writer, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "received/progressive_resource_crafting_after"));

        AdvancementHolder progressiveTools1 = Advancement.Builder.recipeAdvancement()
                .display(
                        Items.STONE_PICKAXE,
                        Component.literal("Progressive Tools 1"),
                        Component.literal("Stone:\n  Pickaxe\n  Shovel\n  Hoe"),
                        null,
                        AdvancementType.TASK,
                        true,
                        false,
                        false)
                .parent(root)
                .addCriterion("received", ReceivedItemCriteria.TriggerInstance.receivedItem(APItems.PROGRESSIVE_RECIPES_TOOLS, 1))
                .save(writer, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "received/progressive_tools_1"));

        AdvancementHolder progressiveTools2 = Advancement.Builder.recipeAdvancement()
                .display(
                        Items.IRON_PICKAXE,
                        Component.literal("Progressive Tools 2"),
                        Component.literal("Iron:\n  Pickaxe\n  Shovel\n  Hoe"),
                        null,
                        AdvancementType.TASK,
                        true,
                        false,
                        false)
                .parent(progressiveTools1)
                .addCriterion("received", ReceivedItemCriteria.TriggerInstance.receivedItem(APItems.PROGRESSIVE_RECIPES_TOOLS, 2))
                .save(writer, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "received/progressive_tools_2"));

        AdvancementHolder progressiveTools3 = Advancement.Builder.recipeAdvancement()
                .display(
                        Items.DIAMOND_PICKAXE,
                        Component.literal("Progressive Tools 3"),
                        Component.literal("Diamond:\n  Pickaxe\n  Shovel\n  Hoe\nNetherite Ingots"),
                        null,
                        AdvancementType.TASK,
                        true,
                        false,
                        false)
                .parent(progressiveTools2)
                .addCriterion("received", ReceivedItemCriteria.TriggerInstance.receivedItem(APItems.PROGRESSIVE_RECIPES_TOOLS, 3))
                .save(writer, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "received/progressive_tools_3"));

        AdvancementHolder progressiveToolsAfter = Advancement.Builder.recipeAdvancement()
                .parent(progressiveTools3)
                .addCriterion("auto", PlayerTrigger.TriggerInstance.tick())
                .save(writer, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "received/progressive_tools_after"));

        AdvancementHolder progressiveWeapons1 = Advancement.Builder.recipeAdvancement()
                .display(
                        Items.STONE_SWORD,
                        Component.literal("Progressive Weapons 1"),
                        Component.literal("Stone:\n  Sword\n  Axe"),
                        null,
                        AdvancementType.TASK,
                        true,
                        false,
                        false)
                .parent(root)
                .addCriterion("received", ReceivedItemCriteria.TriggerInstance.receivedItem(APItems.PROGRESSIVE_RECIPES_WEAPONS, 1))
                .save(writer, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "received/progressive_weapons_1"));

        AdvancementHolder progressiveWeapons2 = Advancement.Builder.recipeAdvancement()
                .display(
                        Items.IRON_SWORD,
                        Component.literal("Progressive Weapons 2"),
                        Component.literal("Iron:\n  Sword\n  Axe"),
                        null,
                        AdvancementType.TASK,
                        true,
                        false,
                        false)
                .parent(progressiveWeapons1)
                .addCriterion("received", ReceivedItemCriteria.TriggerInstance.receivedItem(APItems.PROGRESSIVE_RECIPES_WEAPONS, 2))
                .save(writer, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "received/progressive_weapons_2"));

        AdvancementHolder progressiveWeapons3 = Advancement.Builder.recipeAdvancement()
                .display(
                        Items.DIAMOND_SWORD,
                        Component.literal("Progressive Weapons 3"),
                        Component.literal("Diamond:\n  Sword\n  Axe"),
                        null,
                        AdvancementType.TASK,
                        true,
                        false,
                        false)
                .parent(progressiveWeapons2)
                .addCriterion("received", ReceivedItemCriteria.TriggerInstance.receivedItem(APItems.PROGRESSIVE_RECIPES_WEAPONS, 3))
                .save(writer, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "received/progressive_weapons_3"));

        AdvancementHolder progressiveWeaponsAfter = Advancement.Builder.recipeAdvancement()
                .parent(progressiveWeapons3)
                .addCriterion("auto", PlayerTrigger.TriggerInstance.tick())
                .save(writer, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "received/progressive_weapons_after"));

        AdvancementHolder shield = Advancement.Builder.recipeAdvancement()
                .display(
                        Items.SHIELD,
                        Component.literal("Shield"),
                        Component.empty(),
                        null,
                        AdvancementType.TASK,
                        true,
                        false,
                        false)
                .parent(root)
                .addCriterion("received", ReceivedItemCriteria.TriggerInstance.receivedItem(APItems.GROUP_RECIPES_SHIELD))
                .save(writer, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "received/shield"));

        AdvancementHolder spyglass = Advancement.Builder.recipeAdvancement()
                .display(
                        Items.SPYGLASS,
                        Component.literal("Spyglass"),
                        Component.empty(),
                        null,
                        AdvancementType.TASK,
                        true,
                        false,
                        false)
                .parent(root)
                .addCriterion("received", ReceivedItemCriteria.TriggerInstance.receivedItem(APItems.GROUP_RECIPES_SPYGLASS))
                .save(writer, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "received/spyglass"));
    }

}
