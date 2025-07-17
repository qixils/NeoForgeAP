package gg.archipelago.aprandomizer.gifting

import gg.archipelago.aprandomizer.APRandomizer
import gg.archipelago.aprandomizer.APRegistries
import gg.archipelago.aprandomizer.datamaps.APDataMaps
import gg.archipelago.aprandomizer.gifting.DefaultTags.EQUIPMENT_TRAITS
import net.leloubil.archipelago.gifting.api.GiftTrait
import net.leloubil.archipelago.gifting.remote.GiftTraitName
import net.leloubil.archipelago.gifting.utils.BKTreeCloseTraitParser
import net.minecraft.core.Holder
import net.minecraft.core.HolderLookup
import net.minecraft.core.RegistryAccess
import net.minecraft.core.component.DataComponents
import net.minecraft.core.component.TypedDataComponent
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimplePreparableReloadListener
import net.minecraft.util.profiling.ProfilerFiller
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.attributes.RangedAttribute
import net.minecraft.world.flag.FeatureFlagSet
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.*
import net.minecraft.world.item.alchemy.PotionContents
import net.minecraft.world.item.component.Consumable
import net.minecraft.world.item.component.ItemAttributeModifiers
import net.minecraft.world.item.component.Tool
import net.minecraft.world.item.consume_effects.*
import net.minecraft.world.item.crafting.RecipeManager
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.item.enchantment.ItemEnchantments
import net.minecraft.world.item.equipment.Equippable
import net.minecraft.world.level.material.Fluids
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.common.BooleanAttribute
import net.neoforged.neoforge.common.CreativeModeTabRegistry
import net.neoforged.neoforge.common.NeoForgeMod
import net.neoforged.neoforge.common.PercentageAttribute
import net.neoforged.neoforge.event.AddServerReloadListenersEvent
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.registries.datamaps.builtin.FurnaceFuel
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps
import kotlin.jvm.optionals.getOrNull
import kotlin.math.abs
import kotlin.random.Random

class MinecraftGiftMatcher(
    val randomSeed: Long,
) {
    private var treeParser = BKTreeCloseTraitParser<ItemStack>(::distance)
    private val craftingTraitsHolder: CraftingTraitsHolder =
        CraftingTraitsHolder({ it, re ->
            itemTraits(it.value(), craftingTraitsHolder, re)
        }
        )


    private fun distance(
        originalTraits: List<GiftTrait>,
        other: MutableMap<String, Pair<Float, Float>>,
        isCompatible: BKTreeCloseTraitParser.BooleanWrapper
    ): Float {
        val traits = originalTraits.groupBy {
            it.name
        }.map { (k, v) ->
            GiftTrait(
                k,
                quality = v.map { it.quality }.average().toFloat(),
                duration = v.map { it.duration }.average().toFloat(),
            )
        }

        isCompatible.value = true
        val traitsCopy = other.toMutableMap()
        var distance = 0.0f
        traits.forEach { giftTrait ->
            val (distanceWeight, isRequired) = matchInfo.getOrDefault(giftTrait.name, MatchInfo(1f, false))
            traitsCopy.remove(giftTrait.name.name)?.let { values ->
                val traitDist = abs(values.first - giftTrait.quality) + abs(values.second - giftTrait.duration)

                distance += traitDist * (1/ distanceWeight)
            } ?: run {
                distance += 10.0f * distanceWeight
                if (isRequired) {
                    isCompatible.value = false
                }
            }
        }
        distance += abs(traits.size - traitsCopy.size) / 2f
        return distance
    }


    fun registerAllMinecraftGifts(
        featureFlagSet: FeatureFlagSet,
        access: RegistryAccess,
        recipeManager: RecipeManager
    ) {
        treeParser = BKTreeCloseTraitParser(::distance)
        println("Registering items as gifts")
        val excluded: List<CreativeModeTab> = CreativeModeTabRegistry.getDefaultTabs() + listOf(
            CreativeModeTabs.OP_BLOCKS, // should already be exluded by setting permission to false, but just in case
            CreativeModeTabs.SPAWN_EGGS
        ).map { BuiltInRegistries.CREATIVE_MODE_TAB.get(it) }.mapNotNull { it.getOrNull()?.value() }
        BuiltInRegistries.CREATIVE_MODE_TAB.subtract(excluded).flatMap<CreativeModeTab, ItemStack> {
            it.buildContents(
                CreativeModeTab.ItemDisplayParameters(
                    featureFlagSet,
                    false,
                    access,
                )
            )
            it.displayItems.asSequence()
        }.distinct()
            .forEach {
                val traits = with(access.lookupOrThrow(APRegistries.GIFT_TRAITS)) {
                    getItemStackTraits(it, recipeManager)
                }
                if (traits.size < 3) {
                    println("Item $it has few traits : $traits")
                }
                treeParser.registerAvailableGift(it, traits)
            }
        println("Finished")
    }

    context(traitLookup: HolderLookup.RegistryLookup<GiftTraitDefinition>) fun getItemStackTraits(
        itemStack: ItemStack,
        recipeManager: RecipeManager
    ): List<GiftTrait> {
        return itemStackTraits(itemStack, craftingTraitsHolder, recipeManager).map {
            GiftTrait(
                it.definition.value().name,
                quality = it.quality ?: 1f,
                duration = it.duration ?: 1f,
            )
        }
    }

    private lateinit var durabilityTraits: List<GiftTraitName>
    private lateinit var matchInfo: Map<GiftTraitName, MatchInfo>

    private val RELOAD_LISTENER_TRAITS_CONFIG = ResourceLocation.fromNamespaceAndPath(
        APRandomizer.MODID, "traits_config"
    )

    private val traitsConfigReloadListener = object : SimplePreparableReloadListener<Pair<
            List<GiftTraitName>, Map<GiftTraitName, MatchInfo>>>() {
        override fun prepare(
            resourceManager: ResourceManager,
            profiler: ProfilerFiller
        ): Pair<List<GiftTraitName>, Map<GiftTraitName, MatchInfo>> {
            val reg = this.context.registryAccess().lookupOrThrow(APRegistries.GIFT_TRAITS)
            val durabilityTraits = reg.mapNotNull {
                if (it.duration !is Duration.ItemDurability) return@mapNotNull null
                return@mapNotNull it.name
            }
            val matchInfo = reg.groupBy { it.name }.map { (n, t) ->
                n to t
                    .map {
                        it.matchInfo
                    }
                    .reduce { acc, info ->
                        MatchInfo(
                            distanceWeight = listOf(acc.distanceWeight, info.distanceWeight).average().toFloat(),
                            isRequired = acc.isRequired || info.isRequired
                        )
                    }
            }.toMap()
            return durabilityTraits to matchInfo
        }

        override fun apply(
            res: Pair<List<GiftTraitName>, Map<GiftTraitName, MatchInfo>>,
            resourceManager: ResourceManager,
            profiler: ProfilerFiller
        ) {
            durabilityTraits = res.first
            matchInfo = res.second
        }
    }

    private val RELOAD_LISTENER_REGISTER_ITEMS = ResourceLocation.fromNamespaceAndPath(
        APRandomizer.MODID, "register_items"
    )

    private fun getRegisterItemsReloadListener(recipeManager: RecipeManager) =
        object : SimplePreparableReloadListener<Unit>() {
            override fun prepare(
                resourceManager: ResourceManager,
                profiler: ProfilerFiller
            ) {

            }

            override fun apply(
                res: Unit,
                resourceManager: ResourceManager,
                profiler: ProfilerFiller
            ) {
                registerAllMinecraftGifts(
                    this.context.enabledFeatures(),
                    this.context.registryAccess(),
                    recipeManager
                )
            }
        }

    fun registerReloadListeners(
        manager: AddServerReloadListenersEvent
    ) {
        manager.addListener(RELOAD_LISTENER_TRAITS_CONFIG, traitsConfigReloadListener)
//        manager.addListener(RELOAD_LISTENER_REGISTER_ITEMS,
//            getRegisterItemsReloadListener(manager.serverResources.recipeManager)
//        )
//        manager.addDependency(RELOAD_LISTENER_TRAITS_CONFIG, RELOAD_LISTENER_REGISTER_ITEMS)
//        manager.addDependency(VanillaServerListeners.LAST, RELOAD_LISTENER_REGISTER_ITEMS)
//        manager.addDependency(NeoForgeReloadListeners.RECIPE_PRIORITIES, RELOAD_LISTENER_REGISTER_ITEMS)
    }


    context(traitLookup: HolderLookup.RegistryLookup<GiftTraitDefinition>)
    fun resolveGift(traits: List<GiftTrait>, recipeManager: RecipeManager): ItemStack? {
        val closest: List<ItemStack> = treeParser.findClosestAvailableGift(traits)
        closest.random(
            Random(
                closest.hashCode() + randomSeed
            )
        )
        val item = closest.getOrNull(0) ?: return null
        println("Closest item found: $closest: ${getItemStackTraits(item, recipeManager)}")
        val durableTraits = traits.filter { durabilityTraits.contains(it.name) }
        if (durableTraits.isNotEmpty() && item.isDamageableItem) {
            // set the damage value based on the average duration of the durability traits
            val averageDurability = durableTraits.map { it.duration }.average().toInt()
            item.damageValue = averageDurability - (averageDurability * item.maxDamage)
        }
        return item
    }

}


private const val averageDurability = 250f


fun potionDurationToTraitDuration(duration: Int): Float {
    return if (duration < 0) -1f else (duration.toFloat() / 20f) / 180f // 3 min is standard potion duration
}

context(traitLookup: HolderLookup.RegistryLookup<GiftTraitDefinition>) private fun modifierToTraits(modifier: ItemAttributeModifiers.Entry): Pair<List<GiftTraitWrapper>, ComponentValues> =
    traitSourceScope(GiftTraitSource.ATTRIBUTE_MODIFIERS) {
        return when (modifier.attribute) {
            Attributes.ATTACK_DAMAGE -> KnownTraits.Damage.with(
                quality = scaleQuality(
                    modifier.attribute.value(),
                    modifier.modifier,
                    6.0,
                ).toFloat()
            ).let {
                listOf(it) to ComponentValues(
                    attackDamage = it.quality,
                )
            }

            Attributes.ATTACK_SPEED -> {
                ExtraTraits.AttackSpeed.with(
                    quality = scaleQuality(
                        modifier.attribute.value(),
                        modifier.modifier,
                        4.0,
                    ).toFloat()
                ).let {
                    listOf(it) to ComponentValues(
                        attackSpeed = it.quality,
                    )
                }
            }

            Attributes.MOVEMENT_SPEED -> listOf(
                KnownTraits.Speed.with(
                    quality = scaleQuality(modifier.attribute.value(), modifier.modifier).toFloat()
                ),
            ) to ComponentValues()

            Attributes.MAX_HEALTH, Attributes.MAX_ABSORPTION -> listOf(
                KnownTraits.Life.with(
                    quality = scaleQuality(modifier.attribute.value(), modifier.modifier).toFloat()
                ),
            ) to ComponentValues()

            Attributes.ARMOR -> KnownTraits.Armor.with(
                quality = scaleQuality(
                    modifier.attribute.value(), modifier.modifier
                ).toFloat()
            ).let {
                listOf(it) to ComponentValues(
                    armor = it.quality,
                )
            }

            Attributes.ARMOR_TOUGHNESS -> ExtraTraits.ArmorToughness.with(
                quality = scaleQuality(
                    modifier.attribute.value(), modifier.modifier
                ).toFloat()
            ).let {
                listOf(it) to ComponentValues(
                    armorToughness = it.quality,
                )
            }

            else -> emptyList<GiftTraitWrapper>() to ComponentValues()
        }
    }


private fun effectTraits(effect: Holder<MobEffect>): List<ResourceKey<GiftTraitDefinition>> {
    return when (effect) {
        MobEffects.SPEED -> listOf(KnownTraits.Speed)
        MobEffects.SLOWNESS -> listOf(KnownTraits.Slowness)
        MobEffects.HASTE -> listOf(ExtraTraits.Haste)
        MobEffects.MINING_FATIGUE -> listOf(ExtraTraits.MiningFatigue)
        MobEffects.STRENGTH -> listOf(ExtraTraits.Strength)
        MobEffects.INSTANT_HEALTH -> listOf(KnownTraits.Heal)
        MobEffects.INSTANT_DAMAGE -> listOf(KnownTraits.Damage)
        MobEffects.JUMP_BOOST -> listOf(ExtraTraits.JumpBoost)
        MobEffects.NAUSEA -> listOf(ExtraTraits.Nausea)
        MobEffects.REGENERATION -> listOf(KnownTraits.Heal)
        MobEffects.RESISTANCE -> listOf(KnownTraits.Defense)
        MobEffects.FIRE_RESISTANCE -> listOf(KnownTraits.Defense, KnownTraits.Fire)
        MobEffects.WATER_BREATHING -> listOf(ExtraTraits.WaterBreathing, KnownTraits.Water)
        MobEffects.INVISIBILITY -> listOf(ExtraTraits.Invisibility)
        MobEffects.BLINDNESS -> listOf(ExtraTraits.Blindness)
        MobEffects.NIGHT_VISION -> listOf(ExtraTraits.NightVision)
        MobEffects.HUNGER -> listOf(ExtraTraits.Hunger)
        MobEffects.WEAKNESS -> listOf(ExtraTraits.Weakness)
        MobEffects.POISON -> listOf(KnownTraits.Poison)
        MobEffects.WITHER -> listOf(KnownTraits.Poison, ExtraTraits.Wither)
        MobEffects.HEALTH_BOOST -> listOf(KnownTraits.Life)
        MobEffects.ABSORPTION -> listOf(KnownTraits.Life)
        MobEffects.SATURATION -> listOf(KnownTraits.Food)
        MobEffects.GLOWING -> listOf(KnownTraits.Light)
        MobEffects.LEVITATION -> listOf(KnownTraits.Flight)
        MobEffects.LUCK -> listOf(KnownTraits.Luck)
        MobEffects.UNLUCK -> listOf(KnownTraits.Unluck)
        MobEffects.SLOW_FALLING -> listOf(ExtraTraits.SlowFalling, KnownTraits.Flight)
        MobEffects.CONDUIT_POWER -> listOf(
            KnownTraits.Ocean, KnownTraits.Water, ExtraTraits.NightVision, ExtraTraits.WaterBreathing
        )

        MobEffects.DOLPHINS_GRACE -> listOf(
            KnownTraits.Ocean, KnownTraits.Water, KnownTraits.Speed
        )

        MobEffects.BAD_OMEN -> listOf(ExtraTraits.Ominous, KnownTraits.Trap)
        MobEffects.HERO_OF_THE_VILLAGE -> listOf(KnownTraits.Currency)
        else -> emptyList()
    }
}

//todo datamap
context(_: HolderLookup.RegistryLookup<GiftTraitDefinition>) private fun mobEffectsTraits(
    effects: Iterable<MobEffectInstance>, source: GiftTraitSource
): List<GiftTraitWrapper> = traitSourceScope(source) {
    return effects.flatMap {
        effectTraits(it.effect).map { n ->
            n.with(quality = it.amplifier.toFloat() + 1, duration = potionDurationToTraitDuration(it.duration))

        } + when (it.effect.value().category) {
            MobEffectCategory.BENEFICIAL -> listOf(
                KnownTraits.Buff.with(
                    quality = it.amplifier.toFloat(), duration = potionDurationToTraitDuration(it.duration)
                )
            )

            MobEffectCategory.HARMFUL -> listOf(
                KnownTraits.Trap.with(
                    quality = it.amplifier.toFloat(), duration = potionDurationToTraitDuration(it.duration)
                )
            )

            MobEffectCategory.NEUTRAL -> emptyList()
        }
    }
}

private fun applyOperation(attribute: Double, value: AttributeModifier): Double {
    return when (value.operation) {
        AttributeModifier.Operation.ADD_VALUE -> attribute + value.amount
        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL -> attribute * (1 + value.amount)
        AttributeModifier.Operation.ADD_MULTIPLIED_BASE -> attribute * (1 + value.amount)
    }
}

private fun scaleQuality(
    attribute: Attribute, value: AttributeModifier, base: Double = attribute.defaultValue
): Double {
    return when (attribute) {
        is PercentageAttribute -> (applyOperation(attribute.defaultValue, value) / base)
        is RangedAttribute -> (applyOperation(attribute.defaultValue, value) / base)
        is BooleanAttribute -> value.amount
        else -> error("unreachable")
    }
}

context(lookup: HolderLookup.RegistryLookup<GiftTraitDefinition>) private fun componentTraits(component: TypedDataComponent<*>): Pair<List<GiftTraitWrapper>, ComponentValues> =
    traitSourceScope(GiftTraitSource.ITEM_COMPONENTS) {
        return when (component.type) {
            // enchantment are handled in updateEquipmentTraitValues
            DataComponents.CONSUMABLE -> {
                val data = component.value as Consumable
                return listOf(KnownTraits.Consumable.with()) + data.onConsumeEffects.flatMap {
                    when (it) {
                        is ApplyStatusEffectsConsumeEffect -> mobEffectsTraits(
                            it.effects, GiftTraitSource.ITEM_COMPONENTS
                        )

                        is ClearAllStatusEffectsConsumeEffect -> listOf(KnownTraits.Cure.with())
                        is TeleportRandomlyConsumeEffect -> listOf(KnownTraits.Teleport.with())
                        is RemoveStatusEffectsConsumeEffect -> listOf() //todo ?
                        is PlaySoundConsumeEffect -> listOf() //todo ?
                        else -> error("unreachable")
                    }
                } + when (data.animation) {
                    ItemUseAnimation.EAT -> listOf(KnownTraits.Food.with())
                    ItemUseAnimation.DRINK -> listOf(KnownTraits.Drink.with())
                    else -> emptyList()
                } to ComponentValues()
            }

            DataComponents.RARITY -> when (component.value as Rarity) {
                Rarity.COMMON -> emptyList()
                Rarity.UNCOMMON -> emptyList()
                Rarity.RARE -> emptyList()
                Rarity.EPIC -> listOf(KnownTraits.Legendary.with())
            } to ComponentValues()

            DataComponents.SUSPICIOUS_STEW_EFFECTS -> listOf(
                ExtraTraits.Suspicious.with(),
                KnownTraits.Random.with(),
                KnownTraits.Food.with(quality = 0.1f),
                KnownTraits.Buff.with(quality = 0.5f),
                KnownTraits.Trap.with(quality = 0.5f)
            ) to ComponentValues()

            DataComponents.TOOL -> {
                val data = component.value as Tool
                val qual = (listOf(data.defaultMiningSpeed) + data.rules.map {
                    it.speed.orElse(
                        0f
                    )
                }).maxOrNull() ?: 0f
                listOf(KnownTraits.Tool.with(quality = qual)) to ComponentValues()
            }

            DataComponents.WEAPON -> listOf(KnownTraits.Weapon.with()) to ComponentValues()
            DataComponents.FOOD -> listOf(KnownTraits.Food.with(quality = (component.value as FoodProperties).nutrition() / 3f)) to ComponentValues()

            DataComponents.EQUIPPABLE -> {
                val slot = (component.value as Equippable).slot
                when (slot) {
                    EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.BODY, EquipmentSlot.HEAD -> listOf(
                        KnownTraits.Armor.with()
                    )

                    EquipmentSlot.SADDLE -> listOf(ExtraTraits.Saddle.with())
                    else -> emptyList()
                } + if (slot == EquipmentSlot.HEAD) {
                    listOf(KnownTraits.Head.with())
                } else {
                    emptyList()
                } to ComponentValues()
            }

            DataComponents.POTION_CONTENTS -> mobEffectsTraits(
                (component.value as PotionContents).allEffects, GiftTraitSource.ITEM_COMPONENTS
            ) to ComponentValues()

            DataComponents.DEATH_PROTECTION -> listOf(
                KnownTraits.Life.with(quality = 10f),
                KnownTraits.Buff.with(quality = 10f),
                KnownTraits.Invincible.with(duration = 0.1f),
            ) to ComponentValues()

            DataComponents.BUNDLE_CONTENTS -> listOf(KnownTraits.Container.with()) to ComponentValues()
            DataComponents.CONTAINER -> listOf(KnownTraits.Container.with()) to ComponentValues()

            DataComponents.ATTRIBUTE_MODIFIERS -> (component.value as ItemAttributeModifiers).modifiers.map {
                modifierToTraits(it)
            }
                .ifEmpty {
                    listOf(emptyList<GiftTraitWrapper>() to ComponentValues())
                }
                .reduce { (acct, accv), (t, v) ->
                    (acct + t) to ComponentValues(
                        attackDamage = listOfNotNull(accv.attackDamage, v.attackDamage).maxOrNull(),
                        attackSpeed = listOfNotNull(accv.attackSpeed, v.attackSpeed).maxOrNull(),
                        armor = listOfNotNull(accv.armor, v.armor).maxOrNull(),
                        armorToughness = listOfNotNull(accv.armorToughness, v.armorToughness).maxOrNull(),
                    )
                }

            else -> return emptyList<GiftTraitWrapper>() to ComponentValues()
        }
    }


context(_: HolderLookup.RegistryLookup<GiftTraitDefinition>) private fun derivedTraits(traits: List<GiftTraitWrapper>): List<GiftTraitWrapper> =
    traitSourceScope(GiftTraitSource.DERIVED_TRAITS) {
        val derived = mutableListOf<GiftTraitWrapper>()
        if (traits.none { it.definition.`is`(EQUIPMENT_TRAITS) }) {
            // if no traits are from object with usage, add a resource trait
            //todo configurable
            derived.add(KnownTraits.Resource.with(quality = 0.1f))
        }

        return derived
    }

data class ComponentValues(
    val attackDamage: Float? = null,
    val attackSpeed: Float? = null,
    val armor: Float? = null,
    val armorToughness: Float? = null,
)

context(_: HolderLookup.RegistryLookup<GiftTraitDefinition>) private fun updateEquipmentTraitValues(
    itemStack: ItemStack, traits: List<GiftTraitWrapper>, componentValues: ComponentValues

): List<GiftTraitWrapper> {
    fun <K> Map<K, Number>.getScaled(vararg scalePairs: Pair<K, Float>): List<Float> =
        scalePairs.map { (k, v) -> this[k]?.toFloat()?.times(v) ?: 1f }


    val enchantments = itemStack.components.find { it.type == DataComponents.ENCHANTMENTS }
        ?.let { (it.value as ItemEnchantments).entrySet() }?.associate { (h, l) -> h.key to l }.orEmpty()

    val attackDamage = componentValues.attackDamage
    //todo datamap for enchantment values
    val damageEnchantmentValue = enchantments.getScaled(
        Enchantments.SHARPNESS to 0.8f,
        Enchantments.POWER to 0.8f,
        Enchantments.FIRE_ASPECT to 0.5f,
        Enchantments.FLAME to 0.5f,
        Enchantments.IMPALING to 0.4f,
        Enchantments.PIERCING to 0.4f,
        Enchantments.KNOCKBACK to 0.2f,
        Enchantments.PUNCH to 0.2f,
        Enchantments.BANE_OF_ARTHROPODS to 0.2f,
        Enchantments.SMITE to 0.2f,
    ).sum()
    val attackSpeed = componentValues.attackSpeed
    val armor = componentValues.armor
    val armorToughness = componentValues.armorToughness
    val armorEnchantValue = enchantments.getScaled(
        Enchantments.PROTECTION to 1f,
        Enchantments.PROJECTILE_PROTECTION to 0.8f,
        Enchantments.BLAST_PROTECTION to 0.5f,
        Enchantments.FIRE_PROTECTION to 0.5f,
        Enchantments.FEATHER_FALLING to 0.65f
    ).sum()
    val toolValue = enchantments.getScaled(
        Enchantments.EFFICIENCY to 1f,
        Enchantments.FORTUNE to 0.8f,
        Enchantments.LOOTING to 0.8f,
        Enchantments.LURE to 0.5f,
    ).sum()
    val mendingMultiplier = if (enchantments.containsKey(Enchantments.MENDING)) 1.5f else 1f
    val infinityMultiplier = if (enchantments.containsKey(Enchantments.INFINITY)) 1.2f else 1f
    val thornsValue = enchantments[Enchantments.THORNS]?.times(0.5f)

    val normalizedItemStackDurability =
        (averageDurability - itemStack.damageValue) / (averageDurability) * mendingMultiplier * infinityMultiplier
    val weaponValue = (attackSpeed ?: 1f) * (attackDamage ?: 1f) * (1 + damageEnchantmentValue)
    val toughnessBonus = armorToughness?.div(20f) ?: 0f
    val armorValue = armor?.times(toughnessBonus.plus(1))?.times((1 + armorEnchantValue)) ?: 0f

    val maybeDura =
        if (traits.none { it.definition.value().duration is Duration.ItemDurability } && itemStack.isDamageableItem) {
            listOf(
                KnownTraits.Tool.with(
                    duration = normalizedItemStackDurability, quality = 0.1f, source = GiftTraitSource.ITEM_COMPONENTS
                )
            )
        } else emptyList()

    val mapped = traits.flatMap { trait ->
        when (val d = trait.definition.value().duration) {
            is Duration.ItemDurability -> listOf(
                trait.copy(
                    source = GiftTraitSource.ITEM_COMPONENTS,
                    duration = normalizedItemStackDurability * d.multiplier,
                )
            )

            null -> listOf(trait)
        }
    }.flatMap { trait ->
        if (trait.definition.key == KnownTraits.Armor && thornsValue != null) {
            //todo config option
            listOf(trait, KnownTraits.Damage.with(quality = thornsValue, source = GiftTraitSource.ITEM_COMPONENTS))
        } else listOf(trait)
    }.map { trait ->
        when (val q = trait.definition.value().quality) {
            is Quality.ArmorQuality -> trait.copy(quality = armorValue * q.multiplier)
            is Quality.WeaponValue -> trait.copy(quality = weaponValue * q.multiplier)
            is Quality.ToolEfficiency -> trait.copy(quality = (trait.quality ?: 0f) * ((1 + toolValue)) * q.multiplier)
            null -> trait
        }
    }
    return mapped + maybeDura
}

private fun mergeDuplicateTraits(traits: List<GiftTraitWrapper>): List<GiftTraitWrapper> {

    fun reduceValue(values: List<Pair<GiftTraitSource, Float?>>): Float? {
        return values.groupBy { it.first }.map { (s, v) -> s to v.map { it.second } }.map { (source, vals) ->
            val min = vals.filterNotNull().minOrNull()
            val max = vals.filterNotNull().maxOrNull()
            val avg = vals.filterNotNull().average().toFloat().takeUnless { it.isNaN() }
            source to when (source) {
                GiftTraitSource.CRAFTING -> min
                GiftTraitSource.ITEM_COMPONENTS -> max
                GiftTraitSource.ITEM_SPECIFIC -> max
                GiftTraitSource.CAPABILITIES -> min
                GiftTraitSource.DERIVED_TRAITS -> avg
                GiftTraitSource.ATTRIBUTE_MODIFIERS -> max
                GiftTraitSource.FLUID_COMPONENTS -> max
                GiftTraitSource.FLUID_SPECIFIC -> max
                GiftTraitSource.MERGED -> max
            }
        }.reduce { left, right ->
            if (left.first.priority > right.first.priority) {
                left
            } else if (left.first.priority < right.first.priority) {
                right
            } else {
                // if both sources have the same priority, take the average value
                left.first to listOfNotNull(left.second, right.second)
                    .average()
                    .toFloat()
                    .takeIf { !it.isNaN() }
            }
        }.second
    }

    return traits.groupBy { it.definition }.map { (def, traits) ->
        GiftTraitWrapper(
            definition = def,
            source = GiftTraitSource.MERGED,
            quality = reduceValue(traits.map { it.source to it.quality }),
            duration = reduceValue(traits.map { it.source to it.duration })
        )
    }
}


const val COAL_BURN_TIME = 16000

context(lookup: HolderLookup.RegistryLookup<GiftTraitDefinition>) private fun itemTraits(
    item: Item, craftingTraitsHolder: CraftingTraitsHolder, recipeManager: RecipeManager
): List<GiftTraitWrapper> = traitSourceScope(GiftTraitSource.ITEM_SPECIFIC) {
    val baseTraits = BuiltInRegistries.ITEM.getDataMap(APDataMaps.ITEMS_GIFTS_BASE_TRAITS).getOrDefault(
        BuiltInRegistries.ITEM.wrapAsHolder(item).key!!, TraitData.EMPTY
    )
    val fuelValue: FurnaceFuel? = BuiltInRegistries.ITEM.getDataMap(NeoForgeDataMaps.FURNACE_FUELS)
        .getOrDefault(BuiltInRegistries.ITEM.wrapAsHolder(item).key!!, null)


    baseTraits.wrapped(GiftTraitSource.ITEM_SPECIFIC) + fuelValue?.let { listOf(KnownTraits.Fuel.with(it.burnTime.toFloat() / COAL_BURN_TIME)) }
        .orEmpty() + craftingTraitsHolder.resolveTraits(item, recipeManager)
}

context(lookup: HolderLookup.RegistryLookup<GiftTraitDefinition>) private fun itemStackTraits(
    stack: ItemStack, craftingTraitsHolder: CraftingTraitsHolder, recipeManager: RecipeManager
): List<GiftTraitWrapper> {
    val item = stack.item
    val (componentTraits, componentValues) = stack.components.map { componentTraits(it) }.ifEmpty {
        listOf(emptyList<GiftTraitWrapper>() to ComponentValues())
    }
        .reduce { (acct, accv), (t, v) ->
            Pair(
                acct + t, ComponentValues(
                    attackDamage = listOfNotNull(accv.attackDamage, v.attackDamage).maxOrNull(),
                    attackSpeed = listOfNotNull(accv.attackSpeed, v.attackSpeed).maxOrNull(),
                    armor = listOfNotNull(accv.armor, v.armor).maxOrNull(),
                    armorToughness = listOfNotNull(accv.armorToughness, v.armorToughness).maxOrNull(),
                )
            )
        }
    val baseTraits = listOf(
        componentTraits,
        itemTraits(item, craftingTraitsHolder, recipeManager),
        capabilitiesTraits(stack, craftingTraitsHolder, recipeManager),
        stack.takeIf { it.nextDamageWillBreak() || it.isBroken }?.let {
            listOf(KnownTraits.Broken.with(GiftTraitSource.ITEM_COMPONENTS))
        }.orEmpty(),
    ).flatten()


    val traits = updateEquipmentTraitValues(stack, baseTraits, componentValues) + derivedTraits(baseTraits)
    val merged = mergeDuplicateTraits(
        traits
    )
    val finalTraitInfo = BuiltInRegistries.ITEM.wrapAsHolder(item)
        .getData(APDataMaps.ITEMS_GIFTS_FINAL_TRAITS) ?: FinalTraitData.EMPTY


    val withFinal = merged.filterNot {
        finalTraitInfo.removed.contains(it.definition)
    } + finalTraitInfo.traits.map { (k, v) ->
        GiftTraitWrapper(
            source = GiftTraitSource.ITEM_SPECIFIC,
            definition = k,
            quality = v.quality,
            duration = v.duration
        )
    }

    return withFinal
}


context(_: HolderLookup.RegistryLookup<GiftTraitDefinition>) private fun fluidTraits(stack: FluidStack) =
    stack.components.flatMap { componentTraits(it).first.map { it.copy(source = GiftTraitSource.FLUID_COMPONENTS) } } + (stack.fluidHolder.getData(
        APDataMaps.FLUIDS_GIFTS_TRAITS
    ) ?: TraitData.EMPTY).wrapped(GiftTraitSource.FLUID_SPECIFIC)


context(_: HolderLookup.RegistryLookup<GiftTraitDefinition>) private fun capabilitiesTraits(
    item: ItemStack, craftingTraitsHolder: CraftingTraitsHolder, recipeManager: RecipeManager
): List<GiftTraitWrapper> = traitSourceScope(GiftTraitSource.CAPABILITIES) {
    val traits = mutableListOf<GiftTraitWrapper>()
    if (item.getCapability(Capabilities.ItemHandler.ITEM) != null) {
        traits.add(KnownTraits.Container.with())
    }
    val fluidcap = item.getCapability(Capabilities.FluidHandler.ITEM)
    if (fluidcap != null) {
        traits.add(KnownTraits.Container.with())
        traits.add(KnownTraits.LiquidContainer.with())
        val fls = (0..fluidcap.tanks).map { fluidcap.getFluidInTank(it) }
        fls.filter { it.fluid != Fluids.EMPTY }.forEach { fluid ->
            traits.addAll(fluidTraits(fluid).map { trait ->
                if (trait.quality != null) trait.copy(quality = trait.quality * (fluid.amount / 1000f))
                else trait
            })
        }

        val copied = item.copy()
        val capability = copied.getCapability(Capabilities.FluidHandler.ITEM)
        for (i in 0 until capability!!.tanks) {
            val fluidInTank = capability.getFluidInTank(i)
            if (fluidInTank.isEmpty) continue
            capability.drain(fluidInTank.copyWithAmount(Int.MAX_VALUE), IFluidHandler.FluidAction.EXECUTE);
        }
        val itemWithoutFluid = capability.container
        if (itemWithoutFluid.item != item.item) {
            // add all traits that weren't already present
            val resolveItemTraits: List<GiftTraitWrapper> = itemStackTraits(
                itemWithoutFluid, craftingTraitsHolder,
                recipeManager
            )
            traits.addAll(resolveItemTraits)
        }
    } else if (item.item == Items.MILK_BUCKET && !NeoForgeMod.MILK.isBound) {
        // if no mods adds a milk fluid
        itemStackTraits(ItemStack(Items.BUCKET, 1), craftingTraitsHolder, recipeManager).forEach { trait ->
            traits.add(trait)
        }
        traits.add(KnownTraits.Milk.with())
    } else if (item.item == Items.POWDER_SNOW_BUCKET) {
        // if no mods have powder snow fluid
        itemStackTraits(ItemStack(Items.BUCKET, 1), craftingTraitsHolder, recipeManager).forEach { trait ->
            traits.add(trait)
        }
        traits.add(KnownTraits.Ice.with())
    }
    val energyCap = item.getCapability(Capabilities.EnergyStorage.ITEM)
    if (energyCap != null) {
        traits.add(KnownTraits.Energy.with(quality = energyCap.energyStored / 1000f))
    }
    return traits
}


