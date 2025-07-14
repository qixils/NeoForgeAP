package gg.archipelago.aprandomizer.gifting

import gg.archipelago.aprandomizer.utils.holder
import net.minecraft.core.Holder
import net.minecraft.core.HolderLookup
import net.minecraft.resources.ResourceKey

enum class GiftTraitSource(val priority: Int) {
    ATTRIBUTE_MODIFIERS(8),
    ITEM_COMPONENTS(7),
    FLUID_COMPONENTS(6),
    //dynamic
    CAPABILITIES(5),

    //calculated
    CRAFTING(4),
    DERIVED_TRAITS(3),
    //static
    ITEM_SPECIFIC(2),
    FLUID_SPECIFIC(1),
    MERGED(0);

}

data class GiftTraitWrapper(
    val definition: Holder<GiftTraitDefinition>,
    val quality: Float? = null,
    val duration: Float? = null,
    val source: GiftTraitSource,
) {

    fun copy(
        source: GiftTraitSource,
        quality: Float? = this.quality,
        duration: Float? = this.duration
    ): GiftTraitWrapper {
        return GiftTraitWrapper(definition, quality, duration, source)
    }
}

fun Holder<GiftTraitDefinition>.with(
    source: GiftTraitSource,
    quality: Float? = null,
    duration: Float? = null
): GiftTraitWrapper {
    return GiftTraitWrapper(this, quality, duration, source)
}

fun TraitData.wrapped(source: GiftTraitSource): List<GiftTraitWrapper> =
    this.traits.map { (k, v) -> GiftTraitWrapper(k, v.quality, v.duration, source) }


interface TraitSourceScope {

    fun Holder<GiftTraitDefinition>.with(
        quality: Float? = null,
        duration: Float? = null
    ): GiftTraitWrapper

    context(lookup: HolderLookup.RegistryLookup<GiftTraitDefinition>)
    fun ResourceKey<GiftTraitDefinition>.with(
        quality: Float? = null,
        duration: Float? = null
    ): GiftTraitWrapper

    val Holder<GiftTraitDefinition>.default
        get() = this.with()

}

inline fun <T> traitSourceScope(source: GiftTraitSource, block: TraitSourceScope.() -> T): T {
    val obj = object : TraitSourceScope {

        override fun Holder<GiftTraitDefinition>.with(
            quality: Float?,
            duration: Float?
        ): GiftTraitWrapper {
            return GiftTraitWrapper(this, quality, duration, source)
        }

        context(lookup: HolderLookup.RegistryLookup<GiftTraitDefinition>)
        override fun ResourceKey<GiftTraitDefinition>.with(
            quality: Float?,
            duration: Float?
        ): GiftTraitWrapper = this.with(source, quality, duration)
    }
    return obj.block()
}


//fun splitResourceLocationToElements(location: ResourceLocation): List<String> {
//    return location.path.split('/', '_', '-')
//}
//
//interface ResourceLocationMatchScope {
//    infix fun String.locationMeans(trait: GiftTrait)
//    fun String.locationMeans(vararg traits: GiftTrait)
//
//    infix fun String.stringInLocationMeans(trait: GiftTrait)
//
//    infix fun List<String>.locationMeans(trait: GiftTrait) {
//        this.forEach { t ->
//            t locationMeans trait
//        }
//    }
//
//    infix fun String.elementInLocationMeans(trait: GiftTrait)
//    infix fun List<String>.elementInLocationMeans(trait: GiftTrait) {
//        this.forEach { t ->
//            t elementInLocationMeans trait
//        }
//    }
//
//    fun String.elementInLocationMeans(vararg traits: GiftTrait)
//}
//
//inline fun TraitSourceScope.matchResourceLocation(
//    location: ResourceLocation,
//    block: ResourceLocationMatchScope.() -> Unit
//): List<GiftTraitWrapper> {
//    val res = mutableListOf<GiftTraitWrapper>()
//    val obj = object : ResourceLocationMatchScope {
//        override fun String.locationMeans(trait: GiftTrait) {
//            assert(this.contains(':'))
//            if (this@locationMeans == location.toString()) {
//                res.add(trait.withSource())
//            }
//        }
//
//        override fun String.locationMeans(vararg traits: GiftTrait) {
//            assert(this.contains(':'))
//            if (this@locationMeans == location.toString()) {
//                res.addAll(traits.toList().withSource())
//            }
//        }
//
//        override fun String.stringInLocationMeans(trait: GiftTrait) {
//            assert(!this.contains(':'))
//            if (splitResourceLocationToElements(location).contains(this)) {
//                res.add(trait.withSource())
//            }
//        }
//
//        override fun String.elementInLocationMeans(trait: GiftTrait) {
//            assert(!this.contains(':'))
//            if (splitResourceLocationToElements(location).contains(this@elementInLocationMeans)) {
//                res.add(trait.withSource())
//            }
//        }
//
//        override fun String.elementInLocationMeans(vararg traits: GiftTrait) {
//            assert(!this.contains(':'))
//            if (splitResourceLocationToElements(location).contains(this@elementInLocationMeans)) {
//                res.addAll(traits.toList().withSource())
//            }
//        }
//    }
//    block.invoke(obj)
//    return res
//}
context(_: HolderLookup.RegistryLookup<GiftTraitDefinition>)
fun ResourceKey<GiftTraitDefinition>.with(source: GiftTraitSource, quality: Float? = null, duration: Float? = null) =
    GiftTraitWrapper(
        this.holder, quality, duration, source
    )
