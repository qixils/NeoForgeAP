package gg.archipelago.aprandomizer;

import com.mojang.serialization.MapCodec;
import gg.archipelago.aprandomizer.items.APItem;
import gg.archipelago.aprandomizer.items.APReward;
import gg.archipelago.aprandomizer.locations.APLocation;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class APRegistries {
    public static final ResourceKey<Registry<APItem>> ARCHIPELAGO_ITEM = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "archipelago_item"));
    public static final ResourceKey<Registry<APLocation>> ARCHIPELAGO_LOCATION = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "archipelago_location"));
    public static final ResourceKey<Registry<MapCodec<? extends APReward>>> ARCHIPELAGO_REWARD_TYPE = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "archipelago_item_type"));
    public static final ResourceKey<Registry<MapCodec<? extends APLocation>>> ARCHIPELAGO_LOCATION_TYPE = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "archipelago_location_type"));
}
