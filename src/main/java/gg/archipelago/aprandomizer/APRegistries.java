package gg.archipelago.aprandomizer;

import com.mojang.serialization.MapCodec;
import gg.archipelago.aprandomizer.items.APItem;
import gg.archipelago.aprandomizer.items.APReward;
import gg.archipelago.aprandomizer.locations.APLocation;
import gg.archipelago.aprandomizer.structures.level.StructureLevelReference;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;

public class APRegistries {
    public static final ResourceKey<Registry<APItem>> ARCHIPELAGO_ITEM = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(APRandomizer.MODID, "archipelago_item"));
    public static final ResourceKey<Registry<APLocation>> ARCHIPELAGO_LOCATION = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(APRandomizer.MODID, "archipelago_location"));
    public static final ResourceKey<Registry<MapCodec<? extends APReward>>> ARCHIPELAGO_REWARD_TYPE = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(APRandomizer.MODID, "archipelago_item_type"));
    public static final ResourceKey<Registry<MapCodec<? extends APLocation>>> ARCHIPELAGO_LOCATION_TYPE = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(APRandomizer.MODID, "archipelago_location_type"));
    public static final ResourceKey<Registry<MapCodec<? extends StructureLevelReference>>> STRUCTURE_LEVEL_REFERENCE = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(APRandomizer.MODID, "structure_level_reference"));
}
