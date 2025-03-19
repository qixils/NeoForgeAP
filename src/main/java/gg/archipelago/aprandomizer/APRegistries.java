package gg.archipelago.aprandomizer;

import gg.archipelago.aprandomizer.items.APItem;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class APRegistries {
    public static final ResourceKey<Registry<APItem>> ARCHIPELAGO_ITEMS = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "archipelago_items"));
}
