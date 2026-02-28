package gg.archipelago.aprandomizer.datamaps;

import gg.archipelago.aprandomizer.APRandomizer;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

public class APDataMaps {
    public static final DataMapType<Level, HolderSet<Biome>> DEFAULT_STRUCTURE_BIOMES = DataMapType.builder(
            Identifier.fromNamespaceAndPath(APRandomizer.MODID, "default_structure_biomes"), Registries.DIMENSION, RegistryCodecs.homogeneousList(Registries.BIOME))
            .build();
}
