package gg.archipelago.aprandomizer.tags;

import gg.archipelago.aprandomizer.APRandomizer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public class APBiomeTags {
    public static final TagKey<Biome> OVERWORLD_STRUCTURE = TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath(APRandomizer.MODID, "overworld_structure"));
    public static final TagKey<Biome> NETHER_STRUCTURE = TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath(APRandomizer.MODID, "nether_structure"));
    public static final TagKey<Biome> END_STRUCTURE = TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath(APRandomizer.MODID, "end_structure"));
    public static final TagKey<Biome> NONE = TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath(APRandomizer.MODID, "none"));
    public static final TagKey<Biome> BEE_GROVE_BIOMES = TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath(APRandomizer.MODID, "bee_grove_biomes"));
}
