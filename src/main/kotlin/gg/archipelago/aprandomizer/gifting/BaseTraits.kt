package gg.archipelago.aprandomizer.gifting

import gg.archipelago.aprandomizer.APRegistries
import gg.archipelago.aprandomizer.utils.asHolder
import gg.archipelago.aprandomizer.utils.holder
import gg.archipelago.aprandomizer.utils.holderSet
import net.minecraft.core.Holder
import net.minecraft.core.HolderLookup
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceKey
import net.minecraft.tags.ItemTags
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.material.Fluid
import net.neoforged.neoforge.common.Tags
import net.neoforged.neoforge.common.data.DataMapProvider

context(builder: DataMapProvider.Builder<TraitData, T>, _: Registry<T>, _: HolderLookup.RegistryLookup<GiftTraitDefinition>)
private fun <T : Any> add(item: T, vararg traits: ResourceKey<GiftTraitDefinition>) =
    builder.add(item.asHolder, TraitData(*traits.map { it.holder }.toTypedArray()), false)

context(builder: DataMapProvider.Builder<TraitData, T>, _: Registry<T>)
private fun <T : Any> add(item: T, vararg traits: Pair<Holder<GiftTraitDefinition>, GiftTraitData>) =
    builder.add(item.asHolder, TraitData(*traits), false)


context(builder: DataMapProvider.Builder<TraitData, T>, _: Registry<T>)
private fun <T : Any> add(items: TagKey<T>, vararg traits: Pair<Holder<GiftTraitDefinition>, GiftTraitData>) =
    builder.add(items, TraitData(*traits), false)

context(builder: DataMapProvider.Builder<TraitData, T>, _: Registry<T>, _: HolderLookup.RegistryLookup<GiftTraitDefinition>)
private fun <T : Any> add(items: TagKey<T>, vararg traits: ResourceKey<GiftTraitDefinition>) =
    builder.add(items, TraitData(*traits.map { it.holder }.toTypedArray()), false)



context(_: HolderLookup.RegistryLookup<GiftTraitDefinition>)
private fun ResourceKey<GiftTraitDefinition>.with(quality: Float? = null, duration: Float? = null) =
    this.holder to GiftTraitData(quality, duration)

fun baseFluidTraits(builder: DataMapProvider.Builder<TraitData, Fluid>, provider: HolderLookup.Provider): Unit =
    context(
        builder, provider.lookupOrThrow(APRegistries.GIFT_TRAITS),
        BuiltInRegistries.FLUID
    ) {
        add(Tags.Fluids.WATER, KnownTraits.Water)
        add(Tags.Fluids.LAVA, KnownTraits.Fire,ExtraTraits.Lava)
        add(Tags.Fluids.MILK, KnownTraits.Milk)
    }

fun baseItemTraits(builder: DataMapProvider.Builder<TraitData, Item>, provider: HolderLookup.Provider): Unit =
    context(
        builder,
        provider.lookupOrThrow(APRegistries.GIFT_TRAITS),
        BuiltInRegistries.ITEM,
        BuiltInRegistries.BLOCK
    ) {


        add(Items.TNT, KnownTraits.Bomb)
        add(
            Items.TOTEM_OF_UNDYING,
            KnownTraits.Artifact, KnownTraits.Ancient
        )
        add(Items.SNOWBALL, KnownTraits.Ice, KnownTraits.Throwing)
        add(Items.ICE, KnownTraits.Ice)
        add(Items.PACKED_ICE, KnownTraits.Ice)
        add(Items.BLUE_ICE, KnownTraits.Ice.with(quality = 0.5f))
        add(Items.SNOW_BLOCK, KnownTraits.Ice.with(quality = 0.1f))
        add(Items.SNOW, KnownTraits.Ice.with(quality = 0.05f))
        add(Items.BAMBOO, KnownTraits.Grass, ExtraTraits.Bamboo)
        add(Items.DECORATED_POT, KnownTraits.Ceramic)
        add(Items.ENCHANTED_BOOK, KnownTraits.Scroll, KnownTraits.IQ, KnownTraits.Buff)
        add(Items.BONE, KnownTraits.Bone)
        add(Items.BONE_BLOCK, KnownTraits.Fossil, KnownTraits.Bone)
        add(Items.BONE_MEAL, KnownTraits.Bone)
        add(Items.REDSTONE, KnownTraits.Energy.with(quality = 0.1f))
        add(Items.REDSTONE_BLOCK, KnownTraits.Energy.with(quality = 0.5f))
        add(Items.GLOWSTONE_DUST, KnownTraits.Light.with(quality = 0.1f))
        add(Items.GLOWSTONE, KnownTraits.Light.with(quality = 0.5f))
        add(Items.NOTE_BLOCK, KnownTraits.Instrument)
        add(Items.JUKEBOX, KnownTraits.Instrument.with(quality = 0.5f))
        add(Items.REPEATER, KnownTraits.Electronics.with(quality = 0.1f))
        add(Items.COMPARATOR, KnownTraits.Electronics.with(quality = 0.5f))
        add(Items.PAINTING, KnownTraits.Luxury)
        add(Items.ITEM_FRAME, KnownTraits.Luxury.with(quality = 0.5f))
        add(Items.ARMOR_STAND, KnownTraits.Luxury.with(quality = 0.1f))
        add(Items.GOAT_HORN, KnownTraits.Instrument, KnownTraits.Goat)
        add(Items.CLAY, KnownTraits.Beach, KnownTraits.Clay)
        add(Items.DEAD_BUSH, KnownTraits.Bush, KnownTraits.Dry)
        add(Items.HEART_OF_THE_SEA, KnownTraits.Artifact, KnownTraits.Ocean, KnownTraits.Ancient)
        add(Items.NAUTILUS_SHELL, KnownTraits.Ocean, KnownTraits.Ancient)
        add(Items.TRIDENT, KnownTraits.Trident, KnownTraits.Ocean, KnownTraits.Ancient)
        add(Items.PAPER, KnownTraits.Paper)
        add(Items.FIREWORK_ROCKET, KnownTraits.Firework, KnownTraits.Rocket)
        add(Items.SPLASH_POTION, KnownTraits.Throwing)
        add(Items.LINGERING_POTION, KnownTraits.Throwing)
        add(Items.ENDER_PEARL, KnownTraits.Teleport, KnownTraits.Throwing)
        add(Items.VINE, KnownTraits.Vine, KnownTraits.Grass)
        add(Items.TWISTING_VINES, KnownTraits.Vine, KnownTraits.Grass)
        add(Items.WEEPING_VINES, KnownTraits.Vine, KnownTraits.Grass)
        add(Items.FLOWER_POT, KnownTraits.Ceramic)
        add(Items.COOKED_CHICKEN, KnownTraits.Chicken)
        add(Items.CHICKEN, KnownTraits.Chicken)
        add(Items.WHEAT, KnownTraits.Grain)

        listOf(
            Items.PRISMARINE,
            Items.DARK_PRISMARINE,
        ).forEach { add(it, KnownTraits.Prismarine, KnownTraits.Aquamarine, KnownTraits.Water, KnownTraits.Ocean) }

        listOf(
            Items.COOKED_BEEF,
            Items.BEEF,
        ).forEach {
            add(it, KnownTraits.Cow, KnownTraits.Beef)
        }

        listOf(
            Items.TORCH,
            Items.REDSTONE_TORCH,
            Items.SOUL_TORCH,
        ).forEach {
            add(it, KnownTraits.Light)
        }


        //todo wood types

        // dies
        tags()
    }

context(builder: DataMapProvider.Builder<TraitData, Item>, _: Registry<Item>, _: HolderLookup.RegistryLookup<GiftTraitDefinition>)
private fun dyes() {
    add(Tags.Items.DYES_BLACK, KnownTraits.Black)
    add(Tags.Items.DYED_BLACK, KnownTraits.Black)
    add(Tags.Items.DYES_BLUE, KnownTraits.Blue)
    add(Tags.Items.DYED_BLUE, KnownTraits.Blue)
    add(Tags.Items.DYES_BROWN, KnownTraits.Brown)
    add(Tags.Items.DYED_BROWN, KnownTraits.Brown)
    add(Tags.Items.DYES_CYAN, KnownTraits.Cyan)
    add(Tags.Items.DYED_CYAN, KnownTraits.Cyan)
    add(Tags.Items.DYES_GRAY, KnownTraits.Gray)
    add(Tags.Items.DYED_GRAY, KnownTraits.Gray)
    add(Tags.Items.DYES_GREEN, KnownTraits.Green)
    add(Tags.Items.DYED_GREEN, KnownTraits.Green)
    add(Tags.Items.DYES_LIGHT_BLUE, KnownTraits.LightBlue)
    add(Tags.Items.DYED_LIGHT_BLUE, KnownTraits.LightBlue)
    add(Tags.Items.DYES_LIGHT_GRAY, KnownTraits.LightGray)
    add(Tags.Items.DYED_LIGHT_GRAY, KnownTraits.LightGray)
    add(Tags.Items.DYES_LIME, KnownTraits.Lime)
    add(Tags.Items.DYED_LIME, KnownTraits.Lime)
    add(Tags.Items.DYES_MAGENTA, KnownTraits.Magenta)
    add(Tags.Items.DYED_MAGENTA, KnownTraits.Magenta)
    add(Tags.Items.DYES_ORANGE, KnownTraits.Orange)
    add(Tags.Items.DYED_ORANGE, KnownTraits.Orange)
    add(Tags.Items.DYES_PINK, KnownTraits.Pink)
    add(Tags.Items.DYED_PINK, KnownTraits.Pink)
    add(Tags.Items.DYES_PURPLE, KnownTraits.Purple)
    add(Tags.Items.DYED_PURPLE, KnownTraits.Purple)
    add(Tags.Items.DYES_RED, KnownTraits.Red)
    add(Tags.Items.DYED_RED, KnownTraits.Red)
    add(Tags.Items.DYES_WHITE, KnownTraits.White)
    add(Tags.Items.DYED_WHITE, KnownTraits.White)
    add(Tags.Items.DYES_YELLOW, KnownTraits.Yellow)
    add(Tags.Items.DYED_YELLOW, KnownTraits.Yellow)
    add(Tags.Items.DYES, KnownTraits.Dye)
}

context(builder: DataMapProvider.Builder<TraitData, Item>,
    _: Registry<Item>,
    _: HolderLookup.RegistryLookup<GiftTraitDefinition>,
    _: HolderLookup.RegistryLookup<Block>
)
private fun tags() {
    dyes()
    // c: are in Tags.Items
    add(ItemTags.PICKAXES, ExtraTraits.Pickaxe)
    add(
        Tags.Items.MINING_TOOL_TOOLS, KnownTraits.Stone.with(quality = 0.5f),
        KnownTraits.Ore.with(quality = 0.5f)
    )
    add(
        ItemTags.AXES,
        ExtraTraits.Axe.with(),
        KnownTraits.Wood.with(quality = 0.5f),
        KnownTraits.Lumber.with(quality = 0.5f)
    )
    add(
        ItemTags.SHOVELS,
        ExtraTraits.Shovel.with(),
        KnownTraits.Grass.with(quality = 0.5f),
        KnownTraits.Sand.with(quality = 0.5f)
    )
    add(
        ItemTags.HOES,
        ExtraTraits.Hoe.with(),
        KnownTraits.Grass.with(quality = 0.5f),
        KnownTraits.Grain.with(quality = 0.5f)
    )
    add(Tags.Items.STONES, KnownTraits.Stone.with(quality = 1f),KnownTraits.Mineral.with(quality = 0.2f))
    add(Tags.Items.COBBLESTONES, KnownTraits.Stone.with(quality = 0.8f))
    add(Tags.Items.FOODS_VEGETABLE, KnownTraits.Vegetable)
    add(Tags.Items.FOODS, KnownTraits.Food)
    add(Tags.Items.RAW_MATERIALS, KnownTraits.Material)
    add(Tags.Items.DRINKS, KnownTraits.Drink)
    add(
        ItemTags.DIRT,
        KnownTraits.Grass.with(quality = 0.5f),
    )
    add(
        ItemTags.FLOWERS,
        KnownTraits.Grass,
        KnownTraits.Flower
    )
    add(
        ItemTags.LEAVES,
        KnownTraits.Grass.with(quality = 0.8f),
        KnownTraits.Tree.with(quality = 0.8f),
    )
    add(Tags.Items.CROPS, KnownTraits.Grass)
    add(Tags.Items.SEEDS, KnownTraits.Grass, KnownTraits.Seed)
    add(Tags.Items.ORES, KnownTraits.Ore)
    add(Tags.Items.EGGS, KnownTraits.Egg)
    add(Tags.Items.FOODS_RAW_FISH, KnownTraits.Fish)
    add(Tags.Items.FOODS_COOKED_FISH, KnownTraits.Fish)
    add(Tags.Items.TOOLS, KnownTraits.Tool)
    add(Tags.Items.MELEE_WEAPON_TOOLS, KnownTraits.Weapon, KnownTraits.MeleeWeapon)
    add(Tags.Items.RANGED_WEAPON_TOOLS, KnownTraits.Weapon, KnownTraits.RangedWeapon)
    add(Tags.Items.BUCKETS_ENTITY_WATER, KnownTraits.Water, KnownTraits.Animal)
    add(Tags.Items.FOODS_RAW_MEAT, KnownTraits.Meat)
    add(Tags.Items.FOODS_COOKED_MEAT, KnownTraits.Meat)
    add(Tags.Items.ARMORS, KnownTraits.Armor)
    add(Tags.Items.FOODS_FRUIT, KnownTraits.Fruit)
    add(Tags.Items.INGOTS_COPPER, KnownTraits.Copper.with(quality = 1f))
    add(Tags.Items.INGOTS_IRON, KnownTraits.Iron.with(quality = 1f))
    add(Tags.Items.ORES_COPPER, KnownTraits.Copper)
    add(Tags.Items.ORES_COAL, KnownTraits.Coal.with(quality = 1f))
    add(Tags.Items.INGOTS_NETHERITE, KnownTraits.Metal.with(quality = 5f))
    add(Tags.Items.ORES_NETHERITE_SCRAP, KnownTraits.Metal.with(quality = 4f))
    add(Tags.Items.STORAGE_BLOCKS_COAL, KnownTraits.Coal)
    add(Tags.Items.INGOTS, KnownTraits.Metal.with(quality = 1f))
    add(Tags.Items.FOODS_RAW_MEAT, KnownTraits.Meat)
    add(Tags.Items.FOODS_COOKED_MEAT, KnownTraits.Meat)
    add(Tags.Items.INGOTS_GOLD, KnownTraits.Gold.with(quality = 1f))
    add(Tags.Items.ORES_GOLD, KnownTraits.Gold)
    add(Tags.Items.FOODS_COOKIE, KnownTraits.Cooking)
    add(Tags.Items.FOODS_SOUP, KnownTraits.Cooking)
    add(Tags.Items.NETHERRACKS, KnownTraits.Fire.with(quality = 0.2f), KnownTraits.Stone.with())
    add(Tags.Items.RODS_BLAZE, KnownTraits.Fire)
    add(Tags.Items.FLOWERS, KnownTraits.Flower)
    add(ItemTags.PLANKS, KnownTraits.Lumber.with(), KnownTraits.Wood.with(quality = 1f))
    add(Tags.Items.STRIPPED_LOGS, KnownTraits.Wood.with(quality = 4f), KnownTraits.Lumber.with())
    add(ItemTags.LOGS, KnownTraits.Wood.with(quality = 4f), KnownTraits.Tree.with())
    add(Tags.Items.GEMS, KnownTraits.Gem)
    add(Tags.Items.OBSIDIANS, KnownTraits.Mineral, KnownTraits.Gem)
    add(Tags.Items.DRINKS_WATERY, KnownTraits.Water)
    add(Tags.Items.SHULKER_BOXES, KnownTraits.Container)
    add(Tags.Items.CHESTS, KnownTraits.Container)
    add(Tags.Items.BARRELS, KnownTraits.Container)
    add(Tags.Items.STONES, KnownTraits.Rock)
    add(Tags.Items.BOOKSHELVES, KnownTraits.Book, KnownTraits.Paper)
    add(ItemTags.BOOKSHELF_BOOKS, KnownTraits.Book, KnownTraits.Paper)
    add(Tags.Items.SEEDS, KnownTraits.Seed)
    add(ItemTags.DECORATED_POT_SHERDS, KnownTraits.Ceramic)
    add(Tags.Items.GEMS_EMERALD, KnownTraits.Currency)
    add(Tags.Items.GEMS_DIAMOND, KnownTraits.Diamond)
    add(Tags.Items.MUSHROOMS, KnownTraits.Mushroom)
    add(Tags.Items.LEATHERS, KnownTraits.Leather)
    add(Tags.Items.BONES, KnownTraits.Bone)
    add(Tags.Items.FOODS_BERRY, KnownTraits.Berry)
    add(Tags.Items.PLAYER_WORKSTATIONS_FURNACES, KnownTraits.Machine, KnownTraits.Furnace)
    add(Tags.Items.PLAYER_WORKSTATIONS_CRAFTING_TABLES, KnownTraits.Machine, KnownTraits.Crafting)
    add(Tags.Items.SLIME_BALLS, KnownTraits.Goo)
    add(Tags.Items.STORAGE_BLOCKS_SLIME, KnownTraits.Goo)
    add(Tags.Items.CLUSTERS, KnownTraits.Crystal)
    add(Tags.Items.BUDS, KnownTraits.Crystal)
    add(Tags.Items.GEMS_AMETHYST, KnownTraits.Crystal.with(quality = 0.8f))
    add(Tags.Items.GEMS_QUARTZ, KnownTraits.Crystal.with(quality = 1f))
    add(Tags.Items.DRINKS_JUICE, KnownTraits.Juice)
    add(Tags.Items.DRINKS_MILK, KnownTraits.Milk, KnownTraits.AnimalProduct)
    add(Tags.Items.POTIONS, KnownTraits.Potion)
    add(Tags.Items.SANDS, KnownTraits.Sand, KnownTraits.Beach)
    add(Tags.Items.FERTILIZERS, KnownTraits.Fertilizer)
    add(ItemTags.SAPLINGS, KnownTraits.TreeSeed)
    add(Tags.Items.SANDS, KnownTraits.Desert)
    add(Tags.Items.CROPS_CACTUS, KnownTraits.Desert)
    add(Tags.Items.GUNPOWDERS, KnownTraits.Explosive, KnownTraits.Powder)
    add(Tags.Items.DUSTS, KnownTraits.Powder)


}

