package gg.archipelago.aprandomizer.gifting

import gg.archipelago.aprandomizer.APRandomizer
import gg.archipelago.aprandomizer.APRegistries
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.tags.TagsProvider
import net.minecraft.data.worldgen.BootstrapContext
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.crafting.RecipeType
import java.util.concurrent.CompletableFuture


object DefaultTags {
    // Things that are weapons
    val WEAPON_TRAITS = createTagKey("weapons")
    private val baseWeaponTraits = arrayOf(
        KnownTraits.Weapon,
        KnownTraits.MeleeWeapon,
        KnownTraits.RangedWeapon,
        ExtraTraits.Sword,
        ExtraTraits.Bow,
    )


    // Things that are meant to be used multiple times and usually have a durability
    val EQUIPMENT_TRAITS = createTagKey("equipment")
    private val baseEquipmentTraits = arrayOf(
        KnownTraits.Tool,
        KnownTraits.Armor,

        )

    val CONTAINER_TRAITS = createTagKey("containers")
    private val baseContainerTraits = arrayOf(
        KnownTraits.Container,
        KnownTraits.LiquidContainer,
        ExtraTraits.Saddle
    )

    val ORE_TRAITS = createTagKey("ores")
    private val baseOreTraits = arrayOf(
        KnownTraits.Ore,
        KnownTraits.Copper,
        KnownTraits.Coal,
        KnownTraits.Gold,
        KnownTraits.Silver,
        KnownTraits.Platinum,
        KnownTraits.Diamond,
        KnownTraits.Gem,
        KnownTraits.Crystal,
    )

    // Things that affect the usage of the item
    val USAGE_TRAITS = createTagKey("usages")
    private val baseUsageTraits = arrayOf(
        KnownTraits.Bomb, KnownTraits.Consumable, ExtraTraits.Saddle, KnownTraits.Throwing
    )

    // Traits relating to effects the item can apply
    val EFFECT_TRAITS = createTagKey("effects")
    private val baseEffectTraits = arrayOf(
        KnownTraits.Buff,
        KnownTraits.Trap,
        KnownTraits.Heal,
        KnownTraits.Mana,
        KnownTraits.Cure,
        KnownTraits.Slowness,
        KnownTraits.Damage,
        KnownTraits.Fire,
        KnownTraits.Ice,
        ExtraTraits.AttackSpeed,
        ExtraTraits.Suspicious,
        ExtraTraits.Ominous,
        KnownTraits.Fertilizer
    )


    // Traits relating to the physical form of the item
    val PHYSICAL_STATE_TRAITS = createTagKey("physical_states")
    private val basePhysicalStateTraits = arrayOf(
        KnownTraits.Goo,
        KnownTraits.Powder,
    )

    val MATERIAL_TRAITS = createTagKey("materials")
    private val baseMaterialTraits = arrayOf(
        KnownTraits.Wood,
        KnownTraits.Lumber,
        KnownTraits.Stone,
        KnownTraits.Meat,
        KnownTraits.Vegetable,
        KnownTraits.Fruit,
        KnownTraits.Egg,
        KnownTraits.Metal,
        KnownTraits.Mineral,
        KnownTraits.Gem,
        KnownTraits.Crystal,
        KnownTraits.Rock,
        KnownTraits.Paper,
        KnownTraits.Fish,
        KnownTraits.Mushroom,
        KnownTraits.Copper,
        KnownTraits.Coal,
        KnownTraits.Gold,
        KnownTraits.Silver,
        KnownTraits.Platinum,
        KnownTraits.Diamond,
        KnownTraits.Prismarine,
        KnownTraits.Aquamarine,
        KnownTraits.Sand,
        KnownTraits.Bone,
        KnownTraits.Clay,
        KnownTraits.Iron,
        KnownTraits.Light,
        KnownTraits.Ceramic,
        KnownTraits.Fire,
        KnownTraits.Ice,
        KnownTraits.Energy,
        KnownTraits.Electronics,
        KnownTraits.Leather,
        KnownTraits.AnimalProduct,
        KnownTraits.Explosive,
    )

    fun defaultMatchInfo(key: ResourceKey<GiftTraitDefinition>): MatchInfo {
        val distanceWeight = when (key) {
            in baseEquipmentTraits, in baseWeaponTraits -> 2f
            in baseUsageTraits, in baseContainerTraits -> 1.8f
            in baseEffectTraits -> 1.6f
            in baseMaterialTraits -> 1.4f
            in basePhysicalStateTraits -> 1.3f
            else -> 1f
        }

        val isRequired = when (key) {
            in baseMaterialTraits,
            in baseUsageTraits,
            in baseWeaponTraits,
            in baseContainerTraits,
            in baseEquipmentTraits,
            KnownTraits.Book -> true

            else -> false
        }

        return MatchInfo(distanceWeight, isRequired)
    }

    fun defaultCraftingInheritance(key: ResourceKey<GiftTraitDefinition>): Map<RecipeType<*>, CraftingInheritance> {
        val shapedInheritance = when (key) {
            in baseMaterialTraits, KnownTraits.Book, KnownTraits.Resource -> CraftingInheritance(
                minQuality = 0.01f,
                qualityKeptMultiplier = 1f,
                durationKeptMultiplier = 1f,
            )

            in baseEffectTraits -> CraftingInheritance(
                qualityKeptMultiplier = 0.2f,
                durationKeptMultiplier = 0f
            )

            in baseContainerTraits -> CraftingInheritance(
                qualityKeptMultiplier = 0.5f,
                durationKeptMultiplier = 0f
            )

            else -> null
        }
        val smeltingInheritance = when (key) {
            in baseOreTraits -> CraftingInheritance(
                qualityKeptMultiplier = 1.2f,
                durationKeptMultiplier = 1f
            )

            in baseMaterialTraits -> CraftingInheritance(
                qualityKeptMultiplier = 1f,
                durationKeptMultiplier = 1f
            )

            else -> null
        }
        return listOfNotNull(
            shapedInheritance?.let { RecipeType.CRAFTING to it },
            smeltingInheritance?.let { RecipeType.SMELTING to it }
        ).toMap()
    }

    class GiftTagsProvider(output: PackOutput, lookupProvider: CompletableFuture<HolderLookup.Provider>) :
        GiftTraitsTagProvider(output, lookupProvider, APRandomizer.MODID) {
        override fun addTags(lookupProvider: HolderLookup.Provider) {
            this.tag(MATERIAL_TRAITS)
                .add(*baseMaterialTraits)
            tag(PHYSICAL_STATE_TRAITS)
                .add(
                    *basePhysicalStateTraits
                )
            tag(ORE_TRAITS)
                .add(*baseOreTraits)
            tag(EFFECT_TRAITS)
                .add(*baseEffectTraits)
            tag(CONTAINER_TRAITS)
                .add(
                    *baseContainerTraits
                )
            tag(WEAPON_TRAITS)
                .add(
                    *baseWeaponTraits
                )
            tag(EQUIPMENT_TRAITS)
                .add(
                    *baseEquipmentTraits
                ).addTag(WEAPON_TRAITS)
            tag(USAGE_TRAITS)
                .add(
                    *baseUsageTraits
                )
                .addTags(
                    EQUIPMENT_TRAITS,
                    CONTAINER_TRAITS
                )
        }
    }
}


abstract class GiftTraitsTagProvider(
    output: PackOutput,
    lookupProvider: CompletableFuture<HolderLookup.Provider>,
    modId: String
) :
    TagsProvider<GiftTraitDefinition>(output, APRegistries.GIFT_TRAITS, lookupProvider, modId) {
}


private fun createKey(path: String): ResourceKey<GiftTraitDefinition> = ResourceKey.create(
    APRegistries.GIFT_TRAITS,
    ResourceLocation.fromNamespaceAndPath(
        APRandomizer.MODID,
        path
    )
)


private fun createTagKey(path: String): TagKey<GiftTraitDefinition> = TagKey.create(
    APRegistries.GIFT_TRAITS,
    ResourceLocation.fromNamespaceAndPath(
        APRandomizer.MODID,
        path
    )
)

// region Trait definitions
object ExtraTraits {
    val Bamboo = createKey("bamboo")
    val Pickaxe = createKey("pickaxe")
    val Axe = createKey("axe")
    val Shovel = createKey("shovel")
    val Hoe = createKey("hoe")
    val Sword = createKey("sword")
    val Bow = createKey("bow")
    val ArmorToughness = createKey("armor_toughness")
    val AttackSpeed = createKey("attack_speed")
    val Saddle = createKey("saddle")
    val Suspicious = createKey("suspicious")
    val Ominous = createKey("ominous")
    val Lava = createKey("lava")


    val Haste = createKey("haste")
    val MiningFatigue = createKey("mining_fatigue")
    val Strength = createKey("strength")
    val JumpBoost = createKey("jump_boost")
    val Nausea = createKey("nausea")
    val WaterBreathing = createKey("water_breathing")
    val NightVision = createKey("night_vision")
    val Hunger = createKey("hunger")
    val Weakness = createKey("weakness")
    val Wither = createKey("wither")
    val SlowFalling = createKey("slow_falling")
    val Invisibility = createKey("invisibility")
    val Blindness = createKey("blindness")

    fun bootstrap(context: BootstrapContext<GiftTraitDefinition>) {
        fun register(key: ResourceKey<GiftTraitDefinition>, name: String) {
            context.register(
                key, defaultTrait(key, name)
            )
        }
        register(Bamboo, "Bamboo")
        register(Pickaxe, "Pickaxe")
        register(Axe, "Axe")
        register(Shovel, "Shovel")
        register(Hoe, "Hoe")
        register(Sword, "Sword")
        register(Bow, "Bow")
        register(ArmorToughness, "ArmorToughness")
        register(AttackSpeed, "AttackSpeed")
        register(Saddle, "Saddle")
        register(Suspicious, "Suspicious")
        register(Ominous, "Ominous")
        register(Lava, "Lava")

        register(Haste, "Haste")
        register(MiningFatigue, "MiningFatigue")
        register(Strength, "Strength")
        register(JumpBoost, "JumpBoost")
        register(Nausea, "Nausea")
        register(WaterBreathing, "WaterBreathing")
        register(NightVision, "NightVision")
        register(Hunger, "Hunger")
        register(Weakness, "Weakness")
        register(Wither, "Wither")
        register(SlowFalling, "SlowFalling")
        register(Invisibility, "Invisibility")
        register(Blindness, "Blindness")
    }
}

object KnownTraits {

    val Defense = createKey("defense")
    val Speed = createKey("speed")
    val Consumable = createKey("consumable")
    val Food = createKey("food")
    val Drink = createKey("drink")
    val Heal = createKey("heal")
    val Mana = createKey("mana")
    val Key = createKey("key")
    val Trap = createKey("trap")
    val Buff = createKey("buff")
    val Life = createKey("life")
    val Weapon = createKey("weapon")
    val Armor = createKey("armor")
    val Tool = createKey("tool")
    val Fish = createKey("fish")
    val Animal = createKey("animal")
    val Cure = createKey("cure")
    val Seed = createKey("seed")
    val Metal = createKey("metal")
    val Bomb = createKey("bomb")
    val Monster = createKey("monster")
    val Resource = createKey("resource")
    val Material = createKey("material")
    val Wood = createKey("wood")
    val Stone = createKey("stone")
    val Ore = createKey("ore")
    val Grass = createKey("grass")
    val Meat = createKey("meat")
    val Vegetable = createKey("vegetable")
    val Fruit = createKey("fruit")
    val Egg = createKey("egg")
    val Slowness = createKey("slowness")
    val Damage = createKey("damage")
    val Fire = createKey("fire")
    val Ice = createKey("ice")
    val Currency = createKey("currency")
    val Energy = createKey("energy")
    val Light = createKey("light")

    val Copper = createKey("copper")
    val Coal = createKey("coal")
    val Gold = createKey("gold")
    val Silver = createKey("silver")
    val Platinum = createKey("platinum")
    val Diamond = createKey("diamond")
    val Gem = createKey("gem")
    val Crystal = createKey("crystal")
    val Rock = createKey("rock")
    val Boulder = createKey("boulder")
    val Book = createKey("book")
    val Paper = createKey("paper")
    val Dye = createKey("dye")
    val Potion = createKey("potion")
    val MeleeWeapon = createKey("melee_weapon")
    val RangedWeapon = createKey("ranged_weapon")
    val Container = createKey("container")
    val LiquidContainer = createKey("liquid_container")
    val Electronics = createKey("electronics")
    val Furnace = createKey("furnace")
    val Crafting = createKey("crafting")
    val Machine = createKey("machine")
    val Luxury = createKey("luxury")
    val Statue = createKey("statue")
    val Goat = createKey("goat")
    val Clay = createKey("clay")
    val Cooking = createKey("cooking")
    val Bush = createKey("bush")
    val Dry = createKey("dry")
    val Ocean = createKey("ocean")
    val Trident = createKey("trident")
    val Artifact = createKey("artifact")
    val Ancient = createKey("ancient")
    val Water = createKey("water")
    val Aquamarine = createKey("aquamarine")
    val Prismarine = createKey("prismarine")
    val Insect = createKey("insect")
    val Sand = createKey("sand")
    val Desert = createKey("desert")
    val Fertilizer = createKey("fertilizer")
    val Fiber = createKey("fiber")
    val Explosive = createKey("explosive")
    val Powder = createKey("powder")
    val Broken = createKey("broken")
    val Goo = createKey("goo")
    val Salted = createKey("salted")
    val Leather = createKey("leather")
    val Milk = createKey("milk")
    val Chicken = createKey("chicken")
    val Cow = createKey("cow")
    val Beef = createKey("beef")
    val Tree = createKey("tree")
    val TreeSeed = createKey("tree_seed")
    val Oil = createKey("oil")
    val Juice = createKey("juice")
    val AnimalProduct = createKey("animal_product")
    val Beach = createKey("beach")
    val Grain = createKey("grain")
    val Mushroom = createKey("mushroom")
    val Bone = createKey("bone")
    val Iron = createKey("iron")
    val Unluck = createKey("unluck")
    val Luck = createKey("luck")
    val Vine = createKey("vine")
    val Ceramic = createKey("ceramic")
    val Berry = createKey("berry")
    val Coffee = createKey("coffee")
    val Flower = createKey("flower")
    val Lumber = createKey("lumber")
    val Mineral = createKey("mineral")
    val Poison = createKey("poison")
    val Flight = createKey("flight")
    val Head = createKey("head")
    val Invincible = createKey("invincible")
    val Random = createKey("random")
    val Legendary = createKey("legendary")
    val Instrument = createKey("instrument")
    val Firework = createKey("firework")
    val Rocket = createKey("rocket")
    val Fuel = createKey("fuel")
    val Throwing = createKey("throwing")
    val Scroll = createKey("scroll")
    val IQ = createKey("iq")
    val Fossil = createKey("fossil")
    val Teleport = createKey("teleport")

    val Black = createKey("black")
    val Blue = createKey("blue")
    val Brown = createKey("brown")
    val Cyan = createKey("cyan")
    val Gray = createKey("gray")
    val Green = createKey("green")
    val LightBlue = createKey("light_blue")
    val LightGray = createKey("light_gray")
    val Lime = createKey("lime")
    val Magenta = createKey("magenta")
    val Orange = createKey("orange")
    val Pink = createKey("pink")
    val Purple = createKey("purple")
    val Red = createKey("red")
    val White = createKey("white")
    val Yellow = createKey("yellow")


    fun bootstrap(context: BootstrapContext<GiftTraitDefinition>) {

        fun register(key: ResourceKey<GiftTraitDefinition>, name: String) {
            context.register(
                key, defaultTrait(key, name)
            )
        }

        register(Defense, "Defense")
        register(Speed, "Speed")
        register(Consumable, "Consumable")
        register(Food, "Food")
        register(Drink, "Drink")
        register(Heal, "Heal")
        register(Mana, "Mana")
        register(Key, "Key")
        register(Trap, "Trap")
        register(Buff, "Buff")
        register(Life, "Life")
        context.register(
            Weapon,
            defaultTrait(Weapon, "Weapon").copy(
                duration = Duration.ItemDurability(),
                quality = Quality.WeaponValue(),
            )
        )
        context.register(
            Armor,
            defaultTrait(Armor, "Armor").copy(
                duration = Duration.ItemDurability(),
                quality = Quality.ArmorQuality(),
            )
        )
        context.register(
            Tool, defaultTrait(
                Tool,
                "Tool"
            ).copy(
                duration = Duration.ItemDurability(),
                quality = Quality.ToolEfficiency(),
            )
        )
        register(Fish, "Fish")
        register(Animal, "Animal")
        register(Cure, "Cure")
        register(Seed, "Seed")
        register(Metal, "Metal")
        register(Bomb, "Bomb")
        register(Monster, "Monster")
        register(Resource, "Resource")
        register(Material, "Material")
        register(Wood, "Wood")
        register(Stone, "Stone")
        register(Ore, "Ore")
        register(Grass, "Grass")
        register(Meat, "Meat")
        register(Vegetable, "Vegetable")
        register(Fruit, "Fruit")
        register(Egg, "Egg")
        register(Slowness, "Slowness")
        register(Damage, "Damage")
        register(Fire, "Fire")
        register(Ice, "Ice")
        register(Currency, "Currency")
        register(Energy, "Energy")
        register(Light, "Light")
        register(Copper, "Copper")
        register(Coal, "Coal")
        register(Gold, "Gold")
        register(Silver, "Silver")
        register(Platinum, "Platinum")
        register(Diamond, "Diamond")
        register(Gem, "Gem")
        register(Crystal, "Crystal")
        register(Rock, "Rock")
        register(Boulder, "Boulder")
        register(Book, "Book")
        register(Paper, "Paper")
        register(Dye, "Dye")
        register(Potion, "Potion")
        context.register(
            MeleeWeapon,
            defaultTrait(MeleeWeapon, "MeleeWeapon").copy(
                duration = Duration.ItemDurability(),
                quality = Quality.WeaponValue(),
            )
        )
        context.register(
            RangedWeapon,
            defaultTrait(RangedWeapon, "RangedWeapon").copy(
                duration = Duration.ItemDurability(),
                quality = Quality.WeaponValue(),
            )
        )
        register(Container, "Container")
        register(LiquidContainer, "LiquidContainer")
        register(Electronics, "Electronics")
        register(Furnace, "Furnace")
        register(Crafting, "Crafting")
        register(Machine, "Machine")
        register(Luxury, "Luxury")
        register(Statue, "Statue")
        register(Goat, "Goat")
        register(Clay, "Clay")
        register(Cooking, "Cooking")
        register(Bush, "Bush")
        register(Dry, "Dry")
        register(Ocean, "Ocean")
        register(Trident, "Trident")
        register(Artifact, "Artifact")
        register(Ancient, "Ancient")
        register(Water, "Water")
        register(Aquamarine, "Aquamarine")
        register(Prismarine, "Prismarine")
        register(Insect, "Insect")
        register(Sand, "Sand")
        register(Desert, "Desert")
        register(Fertilizer, "Fertilizer")
        register(Fiber, "Fiber")
        register(Explosive, "Explosive")
        register(Powder, "Powder")
        register(Broken, "Broken")
        register(Goo, "Goo")
        register(Salted, "Salted")
        register(Leather, "Leather")
        register(Milk, "Milk")
        register(Chicken, "Chicken")
        register(Cow, "Cow")
        register(Beef, "Beef")
        register(Tree, "Tree")
        register(TreeSeed, "TreeSeed")
        register(Oil, "Oil")
        register(Juice, "Juice")
        register(AnimalProduct, "AnimalProduct")
        register(Beach, "Beach")
        register(Grain, "Grain")
        register(Mushroom, "Mushroom")
        register(Bone, "Bone")
        register(Iron, "Iron")
        register(Unluck, "Unluck")
        register(Luck, "Luck")
        register(Vine, "Vine")
        register(Ceramic, "Ceramic")
        register(Berry, "Berry")
        register(Coffee, "Coffee")
        register(Flower, "Flower")
        register(Lumber, "Lumber")
        register(Mineral, "Mineral")
        register(Poison, "Poison")
        register(Flight, "Flight")
        register(Head, "Head")
        register(Invincible, "Invincible")
        register(Random, "Random")
        register(Legendary, "Legendary")
        register(Instrument, "Instrument")
        register(Firework, "Firework")
        register(Rocket, "Rocket")
        register(Fuel, "Fuel")
        register(Throwing, "Throwing")
        register(Scroll, "Scroll")
        register(IQ, "IQ")
        register(Fossil, "Fossil")
        register(Teleport, "Teleport")
        register(Black, "Black")
        register(Blue, "Blue")
        register(Brown, "Brown")
        register(Cyan, "Cyan")
        register(Gray, "Gray")
        register(Green, "Green")
        register(LightBlue, "LightBlue")
        register(LightGray, "LightGray")
        register(Lime, "Lime")
        register(Magenta, "Magenta")
        register(Orange, "Orange")
        register(Pink, "Pink")
        register(Purple, "Purple")
        register(Red, "Red")
        register(White, "White")
        register(Yellow, "Yellow")
    }
}

fun defaultTrait(
    key: ResourceKey<GiftTraitDefinition>,
    name: String
) = GiftTraitDefinition(
    name,
    craftingInheritance = DefaultTags.defaultCraftingInheritance(key),
    matchInfo = DefaultTags.defaultMatchInfo(key)
)


