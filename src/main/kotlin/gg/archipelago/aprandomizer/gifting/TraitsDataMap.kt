package gg.archipelago.aprandomizer.gifting

import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import gg.archipelago.aprandomizer.APRegistries
import net.leloubil.archipelago.gifting.remote.GiftTraitName
import net.minecraft.core.Holder
import net.minecraft.core.HolderSet
import net.minecraft.core.Registry
import net.minecraft.core.RegistryCodecs
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries.RECIPE_TYPE
import net.minecraft.data.worldgen.BootstrapContext
import net.minecraft.resources.RegistryFixedCodec
import net.minecraft.resources.ResourceKey
import net.minecraft.tags.TagKey
import net.minecraft.util.StringRepresentable
import net.minecraft.world.item.crafting.RecipeType
import net.neoforged.neoforge.registries.datamaps.DataMapValueMerger
import net.neoforged.neoforge.registries.datamaps.DataMapValueRemover
import java.util.*
import kotlin.jvm.optionals.getOrNull


data class GiftTraitData(
    val quality: Float?, val duration: Float?
) {

    companion object {
        val CODEC: Codec<GiftTraitData> = RecordCodecBuilder.create<GiftTraitData> { instance ->
            instance.group(
                Codec.FLOAT.optionalFieldOf("quality").forGetter { Optional.ofNullable(it.quality) },
                Codec.FLOAT.optionalFieldOf("duration").forGetter { Optional.ofNullable(it.duration) },
            ).apply(instance) { a, b -> GiftTraitData(a.getOrNull(), b.getOrNull()) }
        }
    }
}


sealed interface Duration {
    enum class DurationType(val id: String) : StringRepresentable {
        ITEM_DURABILITY("item_durability");

        override fun getSerializedName(): String = id

        companion object {
            val CODEC: Codec<DurationType> = StringRepresentable.fromEnum(DurationType::values)
        }

    }

    val type: DurationType

    data class ItemDurability(val multiplier: Float = 1f) : Duration {
        companion object {
            val CODEC: MapCodec<ItemDurability> = RecordCodecBuilder.mapCodec {
                it.group(
                    Codec.FLOAT.fieldOf("multiplier").forGetter(ItemDurability::multiplier)
                ).apply(it, ::ItemDurability)
            }
        }

        override val type = DurationType.ITEM_DURABILITY
    }

    companion object {
        val CODEC: Codec<Duration> = DurationType.CODEC.dispatch<Duration>(Duration::type) {
            when (it) {
                DurationType.ITEM_DURABILITY -> ItemDurability.CODEC
            }
        }
    }

}

sealed interface Quality {
    enum class QualityType(val id: String) : StringRepresentable {
        TOOL_EFFICIENCY("tool_efficiency"), DAMAGE("damage"),
        ARMOR("armor");

        override fun getSerializedName(): String = id

        companion object {
            val CODEC: Codec<QualityType> = StringRepresentable.fromEnum(QualityType::values)
        }
    }

    val type: QualityType

    data class ToolEfficiency(val multiplier: Float = 1f) : Quality {
        companion object {
            val CODEC: MapCodec<ToolEfficiency> = RecordCodecBuilder.mapCodec {
                it.group(
                    Codec.FLOAT.fieldOf("multiplier").forGetter(ToolEfficiency::multiplier)
                ).apply(it, ::ToolEfficiency)
            }
        }

        override val type = QualityType.TOOL_EFFICIENCY
    }

    data class WeaponValue(val multiplier: Float = 1f) : Quality {
        companion object {
            val CODEC: MapCodec<WeaponValue> = RecordCodecBuilder.mapCodec {
                it.group(
                    Codec.FLOAT.fieldOf("multiplier").forGetter(WeaponValue::multiplier)
                ).apply(it, ::WeaponValue)
            }
        }

        override val type = QualityType.DAMAGE
    }

    data class ArmorQuality(
        val multiplier: Float = 1f,
    ) : Quality {
        companion object {
            val CODEC: MapCodec<ArmorQuality> = RecordCodecBuilder.mapCodec {
                it.group(
                    Codec.FLOAT.fieldOf("multiplier").forGetter(ArmorQuality::multiplier)
                ).apply(it, ::ArmorQuality)
            }
        }

        override val type = QualityType.ARMOR
    }

    companion object {
        val CODEC: Codec<Quality> = QualityType.CODEC.dispatch(Quality::type) {
            when (it) {
                QualityType.TOOL_EFFICIENCY -> ToolEfficiency.CODEC
                QualityType.DAMAGE -> WeaponValue.CODEC
                QualityType.ARMOR -> ArmorQuality.CODEC
            }
        }
    }
}

data class CraftingInheritance(
    val maxQuality: Float? = null,
    val minQuality: Float? = null,
    val maxDuration: Float? = null,
    val minDuration: Float? = null,
    val qualityKeptMultiplier: Float = DEFAULT_QUALITY_KEPT_MULTIPLIER,
    val durationKeptMultiplier: Float = DEFAULT_DURATION_KEPT_MULTIPLIER,
    val qualityThreshold: Float = DEFAULT_QUALITY_THRESHOLD,
    val inheritanceDefaultQuality: Float = DEFAULT_INHERITANCE_DEFAULT_QUALITY,
    val inheritanceDefaultDuration: Float = DEFAULT_INHERITANCE_DEFAULT_DURATION
) {

    companion object {
        private const val DEFAULT_QUALITY_KEPT_MULTIPLIER: Float = 1f
        private const val DEFAULT_DURATION_KEPT_MULTIPLIER: Float = 1f
        private const val DEFAULT_QUALITY_THRESHOLD: Float = 0.01f
        private const val DEFAULT_INHERITANCE_DEFAULT_QUALITY: Float = 0.01f
        private const val DEFAULT_INHERITANCE_DEFAULT_DURATION: Float = 0f
        val CODEC: Codec<CraftingInheritance> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.FLOAT.optionalFieldOf("max_quality")
                    .forGetter { Optional.ofNullable(it.maxQuality) },
                Codec.FLOAT.optionalFieldOf("min_quality")
                    .forGetter { Optional.ofNullable(it.minQuality) },
                Codec.FLOAT.optionalFieldOf("max_duration")
                    .forGetter { Optional.ofNullable(it.maxDuration) },
                Codec.FLOAT.optionalFieldOf("min_duration")
                    .forGetter { Optional.ofNullable(it.minDuration) },
                Codec.FLOAT.optionalFieldOf("quality_kept_multiplier", DEFAULT_QUALITY_KEPT_MULTIPLIER)
                    .forGetter { it.qualityKeptMultiplier },
                Codec.FLOAT.optionalFieldOf("duration_kept_multiplier", DEFAULT_DURATION_KEPT_MULTIPLIER)
                    .forGetter { it.durationKeptMultiplier },
                Codec.FLOAT.optionalFieldOf("quality_threshold", DEFAULT_QUALITY_THRESHOLD)
                    .forGetter { it.qualityThreshold },
                Codec.FLOAT.optionalFieldOf("inheritance_default_value", DEFAULT_INHERITANCE_DEFAULT_QUALITY)
                    .forGetter { it.inheritanceDefaultQuality },
                Codec.FLOAT.optionalFieldOf("inheritance_default_duration", DEFAULT_INHERITANCE_DEFAULT_DURATION)
                    .forGetter { it.inheritanceDefaultDuration }
            ).apply(instance) { maxQ, minQ, maxD, minD, qKept, dKept, qTreshold, idf, idfd ->
                CraftingInheritance(
                    maxQuality = maxQ.getOrNull(),
                    minQuality = minQ.getOrNull(),
                    maxDuration = maxD.getOrNull(),
                    minDuration = minD.getOrNull(),
                    qualityKeptMultiplier = qKept,
                    durationKeptMultiplier = dKept,
                    qualityThreshold = qTreshold,
                    inheritanceDefaultQuality = idf,
                    inheritanceDefaultDuration = idfd
                )
            }

        }
    }
}

data class MatchInfo(
    val distanceWeight: Float,
    val isRequired: Boolean,
) {
    companion object {
        val CODEC: Codec<MatchInfo> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.FLOAT.optionalFieldOf("distance_weight", 1f)
                    .forGetter { it.distanceWeight },
                Codec.BOOL.optionalFieldOf("is_required", false)
                    .forGetter { it.isRequired }
            ).apply(instance) { d, r ->
                MatchInfo(
                    distanceWeight = d,
                    isRequired = r
                )
            }
        }
    }
}

// Not a data class since instances are meant to be unique, even if they have the same data
class GiftTraitDefinition(
    val name: GiftTraitName,
    val craftingInheritance: Map<RecipeType<*>, CraftingInheritance>,
    val matchInfo: MatchInfo,
    val quality: Quality? = null,
    val duration: Duration? = null,
) {


    fun copy(
        strName: String = name.name,
        craftingInheritance: Map<RecipeType<*>, CraftingInheritance>? = this.craftingInheritance,
        matchInfo: MatchInfo = this.matchInfo,
        quality: Quality? = this.quality,
        duration: Duration? = this.duration
    ): GiftTraitDefinition {
        return GiftTraitDefinition(
            strName, matchInfo, craftingInheritance, quality, duration
        )
    }


    companion object {

        @JvmStatic
        @JvmName("create")
        operator fun invoke(
            name: String,
            matchInfo: MatchInfo,
            craftingInheritance: Map<RecipeType<*>, CraftingInheritance>? = null,
            quality: Quality? = null,
            duration: Duration? = null
        ): GiftTraitDefinition = GiftTraitDefinition(
            GiftTraitName(name), craftingInheritance ?: emptyMap(),
            matchInfo,
            quality,
            duration
        )

        @JvmField
        val CODEC: Codec<GiftTraitDefinition> = RecordCodecBuilder.create { builder ->
            builder.group(
                GIFT_TRAIT_NAME_CODEC.fieldOf("name")
                    .forGetter { it.name },
                Codec.simpleMap(
                    ResourceKey.codec(RECIPE_TYPE),
                    CraftingInheritance.CODEC,
                    BuiltInRegistries.RECIPE_TYPE
                ).xmap({
                    it.mapKeys { (k, _) -> BuiltInRegistries.RECIPE_TYPE.getValueOrThrow(k) }
                }, {
                    it.mapKeys { (k, _) -> BuiltInRegistries.RECIPE_TYPE.wrapAsHolder(k).key }
                })
                    .codec().optionalFieldOf("craft_inheritance")
                    .forGetter { Optional.ofNullable(it.craftingInheritance) },
                MatchInfo.CODEC.optionalFieldOf("matching", MatchInfo(1f, false))
                    .forGetter { it.matchInfo },
                Quality.CODEC.optionalFieldOf("quality")
                    .forGetter { Optional.ofNullable(it.quality) },
                Duration.CODEC.optionalFieldOf("duration")
                    .forGetter { Optional.ofNullable(it.duration) })
                .apply(builder) { n, c, di, q, d ->
                    GiftTraitDefinition(
                        n.name,
                        di,
                        c.getOrNull(),
                        q.getOrNull(),
                        d.getOrNull()
                    )
                }
        }

        @JvmStatic
        fun bootstrap(context: BootstrapContext<GiftTraitDefinition>) {
            KnownTraits.bootstrap(context)
            ExtraTraits.bootstrap(context)
        }
    }

}

val GIFT_TRAIT_NAME_CODEC: Codec<GiftTraitName> = Codec.STRING.validate {
    if (it.contains(' ')) {
        return@validate DataResult.error { "Trait name should not contain spaces" }
    }
    if (!(it.first().isUpperCase())) {
        return@validate DataResult.error { "Trait name should be in PascalCase" }
    }
    return@validate DataResult.success(it)
}.xmap({ GiftTraitName(it) }) { it.name }

data class TraitData(val traits: Map<Holder<GiftTraitDefinition>, GiftTraitData>) {
    constructor(vararg traits: Pair<Holder<GiftTraitDefinition>, GiftTraitData>) :
            this(traits.toMap())

    constructor(vararg traits: Holder<GiftTraitDefinition>)
            : this(traits.associateWith { GiftTraitData(null, null) })

    companion object {
        val EMPTY: TraitData = TraitData(emptyMap())

        @JvmField
        val CODEC: Codec<TraitData> = Codec.unboundedMap(
            RegistryFixedCodec.create(APRegistries.GIFT_TRAITS), GiftTraitData.CODEC
        ).xmap({ TraitData(it) }) { it.traits }
    }
}

data class FinalGiftTraitData(
    val quality: Float, val duration: Float
) {
    companion object {
        val CODEC: Codec<FinalGiftTraitData> = RecordCodecBuilder.create<FinalGiftTraitData> { instance ->
            instance.group(
                Codec.FLOAT.optionalFieldOf("quality", 1f).forGetter { it.quality },
                Codec.FLOAT.optionalFieldOf("duration", 1f).forGetter { it.duration },
            ).apply(instance) { a, b -> FinalGiftTraitData(a, b) }
        }
    }
}

data class FinalTraitData(
    val traits: Map<Holder<GiftTraitDefinition>, FinalGiftTraitData>,
    val removed: List<Holder<GiftTraitDefinition>> = emptyList()
) {

    companion object {
        val EMPTY: FinalTraitData = FinalTraitData(emptyMap())

        @JvmField
        val CODEC: Codec<FinalTraitData> = Codec.unboundedMap(
            RegistryFixedCodec.create(APRegistries.GIFT_TRAITS), FinalGiftTraitData.CODEC
        ).xmap({ FinalTraitData(it) }) { it.traits }
    }
}

class FinalDataGiftTraitsMerger<T> : DataMapValueMerger<T, FinalTraitData> {
    override fun merge(
        registry: Registry<T>,
        first: Either<TagKey<T>, ResourceKey<T>>,
        firstData: FinalTraitData,
        second: Either<TagKey<T>, ResourceKey<T>>,
        secondData: FinalTraitData
    ): FinalTraitData {
        val added = firstData.traits.keys.union(secondData.traits.keys).associateWith { k ->
            val secondTrait = secondData.traits[k]
            val firstTrait = firstData.traits[k]
            FinalGiftTraitData(
                quality = secondTrait?.quality ?: firstTrait?.quality ?: 1f,
                duration = secondTrait?.duration ?: firstTrait?.duration ?: 1f
            )
        }
        return FinalTraitData(
            added,
            removed = (firstData.removed.filter { !secondData.traits.keys.contains(it) }) +
                    (secondData.removed)
        )
    }

}


class FinalDataGiftTraitsRemover<T : Any>(private val names: HolderSet<GiftTraitDefinition>) :
    DataMapValueRemover<T, FinalTraitData> {
    override fun remove(
        data: FinalTraitData, registry: Registry<T>, key: Either<TagKey<T>, ResourceKey<T>>, item: T
    ): Optional<FinalTraitData> {
        return Optional.of(
            data.copy(
                traits = data.traits.filterKeys { !names.contains(it) },
                removed = data.removed + names.toList()
            )
        )
    }

    companion object {
        @JvmStatic
        fun <T : Any> codec(): Codec<FinalDataGiftTraitsRemover<T>> =
            RegistryCodecs.homogeneousList(APRegistries.GIFT_TRAITS)
                .xmap(::FinalDataGiftTraitsRemover, FinalDataGiftTraitsRemover<T>::names)

    }
}


class DataGiftTraitsMerger<T> : DataMapValueMerger<T, TraitData> {
    override fun merge(
        registry: Registry<T>,
        first: Either<TagKey<T>, ResourceKey<T>>,
        firstData: TraitData,
        second: Either<TagKey<T>, ResourceKey<T>>,
        secondData: TraitData
    ): TraitData = TraitData(
        firstData.traits.keys.union(secondData.traits.keys).associateWith { k ->
            val secondTrait = secondData.traits[k]
            val firstTrait = firstData.traits[k]
            GiftTraitData(
                quality = secondTrait?.quality ?: firstTrait?.quality,
                duration = secondTrait?.duration ?: firstTrait?.duration
            )
        },
    )

}


class DataGiftTraitsRemover<T : Any>(private val names: HolderSet<GiftTraitDefinition>) :
    DataMapValueRemover<T, TraitData> {
    override fun remove(
        data: TraitData, registry: Registry<T>, key: Either<TagKey<T>, ResourceKey<T>>, item: T
    ): Optional<TraitData> {
        return Optional.of(
            TraitData(
                data.traits.filterKeys { !names.contains(it) })
        )
    }

    companion object {
        @JvmStatic
        fun <T : Any> codec(): Codec<DataGiftTraitsRemover<T>> =
            RegistryCodecs.homogeneousList(APRegistries.GIFT_TRAITS)
                .xmap(::DataGiftTraitsRemover, DataGiftTraitsRemover<T>::names)

    }
}



