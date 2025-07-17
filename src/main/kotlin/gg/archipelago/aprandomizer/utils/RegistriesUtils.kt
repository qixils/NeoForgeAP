package gg.archipelago.aprandomizer.utils

import net.minecraft.core.Holder
import net.minecraft.core.HolderLookup
import net.minecraft.core.HolderSet
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.tags.TagKey

context(lookup: HolderLookup.RegistryLookup<T>)
val <T> ResourceKey<T>.holder: Holder.Reference<T>
    get() = lookup.getOrThrow(this)


context(lookup: HolderLookup.RegistryLookup<T>)
val <T> TagKey<T>.holderSet: HolderSet.Named<T>
    get() = lookup.getOrThrow(this)

context(registry: Registry<T>)
val <T : Any> T.asHolder: Holder<T>
    get() {
        val value: T = this@asHolder
        return registry.wrapAsHolder(value)
    }

