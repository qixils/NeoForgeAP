package gg.archipelago.aprandomizer.items;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.APRegistries;
import gg.archipelago.aprandomizer.items.traps.MobTrap;
import gg.archipelago.aprandomizer.modifiers.APStructureModifiers;
import gg.archipelago.aprandomizer.structures.level.RandomizedStructureLevel;
import gg.archipelago.aprandomizer.tags.APStructureTags;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.List;
import java.util.Optional;

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
    public static final ResourceKey<APItem> GROUP_RECIPES_SADDLE = id("group_recipes/saddle");
    public static final ResourceKey<APItem> GROUP_RECIPES_SPYGLASS = id("group_recipes/spyglass");
    public static final ResourceKey<APItem> GROUP_RECIPES_LEAD = id("group_recipes/lead");
    public static final ResourceKey<APItem> GROUP_RECIPES_BRUSH = id("group_recipes/brush");

    // Progressive Recipes
    public static final ResourceKey<APItem> PROGRESSIVE_RECIPES_WEAPONS = id("progressive_recipes/weapons");
    public static final ResourceKey<APItem> PROGRESSIVE_RECIPES_TOOLS = id("progressive_recipes/tools");
    public static final ResourceKey<APItem> PROGRESSIVE_RECIPES_ARMOR = id("progressive_recipes/armor");
    public static final ResourceKey<APItem> PROGRESSIVE_RECIPES_RESOURCE_CRAFTING = id("progressive_recipes/resource_crafting");

    // ItemStacks
    public static final ResourceKey<APItem> ITEMSTACK_NETHERITE_SCRAP = id("itemstack/netherite_scrap");
    public static final ResourceKey<APItem> ITEMSTACK_EIGHT_EMERALD = id("itemstack/eight_emerald");
    public static final ResourceKey<APItem> ITEMSTACK_FOUR_EMERALD = id("itemstack/four_emerald");
    public static final ResourceKey<APItem> ITEMSTACK_ENCHANTMENT_CHANNELING_ONE = id("itemstack/enchantment/channeling_one");
    public static final ResourceKey<APItem> ITEMSTACK_ENCHANTMENT_SILK_TOUCH_ONE = id("itemstack/enchantment/silk_touch_one");
    public static final ResourceKey<APItem> ITEMSTACK_ENCHANTMENT_SHARPNESS_THREE = id("itemstack/enchantment/sharpness_three");
    public static final ResourceKey<APItem> ITEMSTACK_ENCHANTMENT_PIERCING_FOUR = id("itemstack/enchantment/piercing_four");
    public static final ResourceKey<APItem> ITEMSTACK_ENCHANTMENT_MOB_LOOTING_THREE = id("itemstack/enchantment/mob_looting_three");
    public static final ResourceKey<APItem> ITEMSTACK_ENCHANTMENT_INFINITY_ARROWS_ONE = id("itemstack/enchantment/infinity_arrows_one");
    public static final ResourceKey<APItem> ITEMSTACK_DIAMOND_ORE = id("itemstack/diamond_ore");
    public static final ResourceKey<APItem> ITEMSTACK_IRON_ORE = id("itemstack/iron_ore");
    public static final ResourceKey<APItem> ITEMSTACK_ENDER_PEARL = id("itemstack/ender_pearl");
    public static final ResourceKey<APItem> ITEMSTACK_LAPIS_LAZULI = id("itemstack/lapis_lazuli");
    public static final ResourceKey<APItem> ITEMSTACK_COOKED_PORKCHOP = id("itemstack/cooked_porkchop");
    public static final ResourceKey<APItem> ITEMSTACK_GOLD_ORE = id("itemstack/gold_ore");
    public static final ResourceKey<APItem> ITEMSTACK_ROTTEN_FLESH = id("itemstack/rotten_flesh");
    public static final ResourceKey<APItem> ITEMSTACK_THE_ARROW = id("itemstack/the_arrow");
    public static final ResourceKey<APItem> ITEMSTACK_THIRTY_TWO_ARROW = id("itemstack/thirty_two_arrow");
    public static final ResourceKey<APItem> ITEMSTACK_SHULKER_BOX = id("itemstack/shulker_box");

    // Experience
    public static final ResourceKey<APItem> EXPERIENCE_FIVE_HUNDRED = id("experience/five_hundred");
    public static final ResourceKey<APItem> EXPERIENCE_ONE_HUNDRED = id("experience/one_hundred");
    public static final ResourceKey<APItem> EXPERIENCE_FIFTY = id("experience/fifty");

    // Compasses
    public static final ResourceKey<APItem> COMPASS_VILLAGE = id("compass/village");
    public static final ResourceKey<APItem> COMPASS_PILLAGER_OUTPOST = id("compass/pillager_outpost");
    public static final ResourceKey<APItem> COMPASS_FORTRESS = id("compass/fortress");
    public static final ResourceKey<APItem> COMPASS_BASTION_REMNANT = id("compass/bastion_remnant");
    public static final ResourceKey<APItem> COMPASS_END_CITY = id("compass/end_city");
    public static final ResourceKey<APItem> COMPASS_OCEAN_MONUMENT = id("compass/ocean_monument");
    public static final ResourceKey<APItem> COMPASS_WOODLAND_MANSION = id("compass/woodland_mansion");
    public static final ResourceKey<APItem> COMPASS_ANCIENT_CITY = id("compass/ancient_city");
    public static final ResourceKey<APItem> COMPASS_TRAIL_RUINS = id("compass/trail_ruins");
    public static final ResourceKey<APItem> COMPASS_TRIAL_CHAMBERS = id("compass/trial_chambers");

    // Traps
    public static final ResourceKey<APItem> TRAP_BEES = id("trap/bees");

    public static final ResourceKey<APItem> DRAGON_EGG_SHARD = id("dragon_egg_shard");

    private static ResourceKey<APItem> id(String name) {
        return ResourceKey.create(APRegistries.ARCHIPELAGO_ITEM, Identifier.fromNamespaceAndPath(APRandomizer.MODID, name));
    }

    public static void bootstrap(BootstrapContext<APItem> context) {
        HolderGetter<Enchantment> enchantments = context.lookup(Registries.ENCHANTMENT);

        context.register(GROUP_RECIPES_ARCHERY,
                APItem.ofRewards(List.of(
                        new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.BOW)),
                        new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.ARROW)),
                        new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.CROSSBOW)))));

        context.register(GROUP_RECIPES_BREWING,
                APItem.ofRewards(List.of(
                        new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.BLAZE_POWDER)),
                        new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.BREWING_STAND)))));

        context.register(GROUP_RECIPES_ENCHANTING,
                APItem.ofRewards(List.of(
                        new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.ENCHANTING_TABLE)),
                        new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.BOOKSHELF)),
                        new ItemReward(new ItemStack(Items.LAPIS_LAZULI, 4)))));

        context.register(GROUP_RECIPES_BUCKET,
                APItem.ofRewards(List.of(
                        new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.BUCKET)))));

        context.register(GROUP_RECIPES_FLINT_AND_STEEL,
                APItem.ofRewards(List.of(
                        new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.FLINT_AND_STEEL)))));

        context.register(GROUP_RECIPES_BEDS,
                APItem.ofRewards(List.of(
                        new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.BLACK_BED)),
                        new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.BLUE_BED)),
                        new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.BROWN_BED)),
                        new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.CYAN_BED)),
                        new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.GRAY_BED)),
                        new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.GREEN_BED)),
                        new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.LIGHT_BLUE_BED)),
                        new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.LIGHT_GRAY_BED)),
                        new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.LIME_BED)),
                        new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.MAGENTA_BED)),
                        new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.ORANGE_BED)),
                        new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.PINK_BED)),
                        new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.PURPLE_BED)),
                        new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.RED_BED)),
                        new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.WHITE_BED)),
                        new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.YELLOW_BED)))));

        context.register(GROUP_RECIPES_BOTTLES,
                APItem.ofRewards(List.of(
                        new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.GLASS_BOTTLE)))));

        context.register(GROUP_RECIPES_SHIELD,
                APItem.ofRewards(List.of(
                        new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.SHIELD)))));

        context.register(GROUP_RECIPES_FISHING,
                APItem.ofRewards(List.of(
                        new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.FISHING_ROD)),
                        new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.CARROT_ON_A_STICK)),
                        new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.WARPED_FUNGUS_ON_A_STICK)))));

        context.register(GROUP_RECIPES_CAMPFIRES,
                APItem.ofRewards(List.of(
                        new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.CAMPFIRE)),
                        new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.SOUL_CAMPFIRE)))));

        context.register(GROUP_RECIPES_SPYGLASS,
                APItem.ofRewards(List.of(
                        new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.SPYGLASS)))));

        context.register(GROUP_RECIPES_LEAD,
                APItem.ofRewards(List.of(
                        new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.LEAD)))));

        context.register(GROUP_RECIPES_BRUSH,
                APItem.ofRewards(List.of(
                        new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.BRUSH)))));

        context.register(GROUP_RECIPES_SADDLE,
                APItem.ofRewards(List.of(
                        new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.SADDLE)))));

        context.register(PROGRESSIVE_RECIPES_WEAPONS,
                APItem.ofTiers(List.of(
                        new APTier(List.of(
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.STONE_SWORD)),
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.STONE_AXE)),
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.STONE_SPEAR)),
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.COPPER_SWORD)),
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.COPPER_AXE)),
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.COPPER_SPEAR)))),
                        new APTier(List.of(
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.IRON_SWORD)),
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.IRON_AXE)),
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.IRON_SPEAR)))),
                        new APTier(List.of(
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.DIAMOND_SWORD)),
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.DIAMOND_AXE)),
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.DIAMOND_SPEAR)))))));

        context.register(PROGRESSIVE_RECIPES_TOOLS,
                APItem.ofTiers(List.of(
                        new APTier(List.of(
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.STONE_PICKAXE)),
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.STONE_SHOVEL)),
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.STONE_HOE)),
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.COPPER_PICKAXE)),
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.COPPER_SHOVEL)),
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.COPPER_HOE)))),
                        new APTier(List.of(
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.IRON_PICKAXE)),
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.IRON_SHOVEL)),
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.IRON_HOE)))),
                        new APTier(List.of(
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.DIAMOND_PICKAXE)),
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.DIAMOND_SHOVEL)),
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.DIAMOND_HOE)),
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.NETHERITE_INGOT)))))));

        context.register(PROGRESSIVE_RECIPES_ARMOR,
                APItem.ofTiers(List.of(
                        /*new APTier(List.of(
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.COPPER_HELMET)),
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.COPPER_CHESTPLATE)),
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.COPPER_LEGGINGS)),
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.COPPER_BOOTS))
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.GOLD_HELMET))
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.GOLD_CHESTPLATE))
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.GOLD_LEGGINGS))
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.GOLD_BOOTS)))),*/
                        new APTier(List.of(
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.IRON_HELMET)),
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.IRON_CHESTPLATE)),
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.IRON_LEGGINGS)),
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.IRON_BOOTS)))),
                        new APTier(List.of(
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.DIAMOND_HELMET)),
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.DIAMOND_CHESTPLATE)),
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.DIAMOND_LEGGINGS)),
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.DIAMOND_BOOTS)))))));

        context.register(PROGRESSIVE_RECIPES_RESOURCE_CRAFTING,
                APItem.ofTiers(List.of(
                        new APTier(List.of(
                                new RecipeReward(Identifier.withDefaultNamespace("copper_ingot_from_nuggets")),
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.COPPER_NUGGET)),
                                new RecipeReward(Identifier.withDefaultNamespace("iron_ingot_from_nuggets")),
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.IRON_NUGGET)),
                                new RecipeReward(Identifier.withDefaultNamespace("gold_ingot_from_nuggets")),
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.GOLD_NUGGET)),
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.FURNACE)),
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.BLAST_FURNACE)))),
                        new APTier(List.of(
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.REDSTONE)),
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.REDSTONE_BLOCK)),
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.GLOWSTONE)),
                                new RecipeReward(Identifier.withDefaultNamespace("copper_ingot_from_copper_block")),
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.COPPER_BLOCK)),
                                new RecipeReward(Identifier.withDefaultNamespace("iron_ingot_from_iron_block")),
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.IRON_BLOCK)),
                                new RecipeReward(Identifier.withDefaultNamespace("gold_ingot_from_gold_block")),
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.GOLD_BLOCK)),
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.DIAMOND)),
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.DIAMOND_BLOCK)),
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.NETHERITE_BLOCK)),
                                new RecipeReward(Identifier.withDefaultNamespace("netherite_ingot_from_netherite_block")),
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.ANVIL)),
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.RESIN_CLUMP)),
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.RESIN_BLOCK)),
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.EMERALD)),
                                new RecipeReward(RecipeBuilder.getDefaultRecipeId(Items.EMERALD_BLOCK)))))));

        context.register(ITEMSTACK_NETHERITE_SCRAP,
                APItem.ofReward(
                        new ItemReward(new ItemStack(Items.NETHERITE_SCRAP, 8))));

        context.register(ITEMSTACK_EIGHT_EMERALD,
                APItem.ofReward(
                        new ItemReward(new ItemStack(Items.EMERALD, 8))));

        context.register(ITEMSTACK_FOUR_EMERALD,
                APItem.ofReward(
                        new ItemReward(new ItemStack(Items.EMERALD, 4))));

        context.register(ITEMSTACK_ENCHANTMENT_CHANNELING_ONE,
                APItem.ofReward(
                        new ItemReward(enchantment(enchantments.getOrThrow(Enchantments.CHANNELING), 1))));

        context.register(ITEMSTACK_ENCHANTMENT_SILK_TOUCH_ONE,
                APItem.ofReward(
                        new ItemReward(enchantment(enchantments.getOrThrow(Enchantments.SILK_TOUCH), 1))));

        context.register(ITEMSTACK_ENCHANTMENT_SHARPNESS_THREE,
                APItem.ofReward(
                        new ItemReward(enchantment(enchantments.getOrThrow(Enchantments.SHARPNESS), 3))));

        context.register(ITEMSTACK_ENCHANTMENT_PIERCING_FOUR,
                APItem.ofReward(
                        new ItemReward(enchantment(enchantments.getOrThrow(Enchantments.PIERCING), 4))));

        context.register(ITEMSTACK_ENCHANTMENT_MOB_LOOTING_THREE,
                APItem.ofReward(
                        new ItemReward(enchantment(enchantments.getOrThrow(Enchantments.LOOTING), 3))));

        context.register(ITEMSTACK_ENCHANTMENT_INFINITY_ARROWS_ONE,
                APItem.ofReward(
                        new ItemReward(enchantment(enchantments.getOrThrow(Enchantments.INFINITY), 1))));

        context.register(ITEMSTACK_DIAMOND_ORE,
                APItem.ofReward(
                        new ItemReward(new ItemStack(Items.DIAMOND_ORE, 4))));

        context.register(ITEMSTACK_IRON_ORE,
                APItem.ofReward(
                        new ItemReward(new ItemStack(Items.IRON_ORE, 16))));

        context.register(ITEMSTACK_ENDER_PEARL,
                APItem.ofReward(
                        new ItemReward(new ItemStack(Items.ENDER_PEARL, 3))));

        context.register(ITEMSTACK_LAPIS_LAZULI,
                APItem.ofReward(
                        new ItemReward(new ItemStack(Items.LAPIS_LAZULI, 4))));

        context.register(ITEMSTACK_COOKED_PORKCHOP,
                APItem.ofReward(
                        new ItemReward(new ItemStack(Items.COOKED_PORKCHOP, 16))));

        context.register(ITEMSTACK_GOLD_ORE,
                APItem.ofReward(
                        new ItemReward(new ItemStack(Items.GOLD_ORE, 8))));

        context.register(ITEMSTACK_ROTTEN_FLESH,
                APItem.ofReward(
                        new ItemReward(new ItemStack(Items.ROTTEN_FLESH, 8))));

        context.register(ITEMSTACK_THE_ARROW,
                APItem.ofReward(
                        new ItemReward(new ItemStack(BuiltInRegistries.ITEM.wrapAsHolder(Items.ARROW), 1,
                                DataComponentPatch.builder()
                                        .set(DataComponents.ITEM_NAME, Component.literal("The Arrow"))
                                        .build()))));

        context.register(ITEMSTACK_THIRTY_TWO_ARROW,
                APItem.ofReward(
                        new ItemReward(new ItemStack(Items.ARROW, 32))));

        context.register(ITEMSTACK_SHULKER_BOX,
                APItem.ofReward(
                        new ItemReward(new ItemStack(Items.SHULKER_BOX, 1))));

        context.register(EXPERIENCE_FIVE_HUNDRED,
                APItem.ofReward(
                        new ExperienceReward(500)));

        context.register(EXPERIENCE_ONE_HUNDRED,
                APItem.ofReward(
                        new ExperienceReward(100)));

        context.register(EXPERIENCE_FIFTY,
                APItem.ofReward(
                        new ExperienceReward(50)));
        
        context.register(COMPASS_VILLAGE, 
                APItem.ofReward(
                        new CompassReward(APStructureTags.VILLAGE, new RandomizedStructureLevel(APStructureModifiers.VILLAGE_NAME), Component.literal("Village"))));

        context.register(COMPASS_PILLAGER_OUTPOST,
                APItem.ofReward(
                        new CompassReward(APStructureTags.PILLAGER_OUTPOST, new RandomizedStructureLevel(APStructureModifiers.PILLAGER_OUTPOST_NAME), Component.literal("Pillager Outpost"))));

        context.register(COMPASS_FORTRESS,
                APItem.ofReward(
                        new CompassReward(APStructureTags.FORTRESS, new RandomizedStructureLevel(APStructureModifiers.FORTRESS_NAME), Component.literal("Nether Fortress"))));

        context.register(COMPASS_BASTION_REMNANT,
                APItem.ofReward(
                        new CompassReward(APStructureTags.BASTION_REMNANT, new RandomizedStructureLevel(APStructureModifiers.BASTION_REMNANT_NAME), Component.literal("Bastion Remnant"))));

        context.register(COMPASS_END_CITY,
                APItem.ofReward(
                        new CompassReward(APStructureTags.END_CITY, new RandomizedStructureLevel(APStructureModifiers.END_CITY_NAME), Component.literal("End City"))));

        context.register(COMPASS_WOODLAND_MANSION,
                APItem.ofReward(
                        new CompassReward(APStructureTags.WOODLAND_MANSION, new RandomizedStructureLevel(APStructureModifiers.WOODLAND_MANSION_NAME), Component.literal("Woodland Mansion"))));

        context.register(COMPASS_OCEAN_MONUMENT,
                APItem.ofReward(
                        new CompassReward(APStructureTags.OCEAN_MONUMENT, new RandomizedStructureLevel(APStructureModifiers.OCEAN_MONUMENT_NAME), Component.literal("Ocean Monument"))));

        context.register(COMPASS_ANCIENT_CITY,
                APItem.ofReward(
                        new CompassReward(APStructureTags.ANCIENT_CITY, new RandomizedStructureLevel(APStructureModifiers.ANCIENT_CITY_NAME), Component.literal("Ancient City"))));

        context.register(COMPASS_TRAIL_RUINS,
                APItem.ofReward(
                        new CompassReward(APStructureTags.TRAIL_RUINS, new RandomizedStructureLevel(APStructureModifiers.TRAIL_RUINS_NAME), Component.literal("Trail Ruins"))));

        context.register(COMPASS_TRIAL_CHAMBERS,
                APItem.ofReward(
                        new CompassReward(APStructureTags.TRIAL_CHAMBERS, new RandomizedStructureLevel(APStructureModifiers.TRIAL_CHAMBERS_NAME), Component.literal("Trial Chambers"))));


        context.register(TRAP_BEES,
                APItem.ofReward(
                        new MobTrap(EntityType.BEE, 3, 5, true, Optional.of(1200))));

        context.register(DRAGON_EGG_SHARD,
                APItem.ofReward(
                        new DragonEggShardReward()));
    }

    private static ItemStack enchantment(Holder<Enchantment> enchantment, int level) {
        ItemStack stack = new ItemStack(Items.ENCHANTED_BOOK);
        stack.enchant(enchantment, level);
        return stack;
    }
}
