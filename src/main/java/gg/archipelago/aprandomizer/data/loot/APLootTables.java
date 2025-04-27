package gg.archipelago.aprandomizer.data.loot;

import gg.archipelago.aprandomizer.APRandomizer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;

public class APLootTables {
    public static final ResourceKey<LootTable> ENTITIES_DROWNED_ADD_TRIDENT = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "entities/drowned/add_trident"));
    public static final ResourceKey<LootTable> ENTITIES_WITHER_SKELETON_ADD_WITHER_SKELETON_SKULL = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "entities/wither_skeleton/add_wither_skeleton_skull"));
}
