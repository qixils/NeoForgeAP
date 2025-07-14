package gg.archipelago.aprandomizer.datamaps;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.gifting.*;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.registries.datamaps.AdvancedDataMapType;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

public class APDataMaps {
    public static final DataMapType<Level, HolderSet<Biome>> DEFAULT_STRUCTURE_BIOMES = DataMapType.builder(
            ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "default_structure_biomes"), Registries.DIMENSION, RegistryCodecs.homogeneousList(Registries.BIOME))
            .build();

    public static final DataMapType<Item, TraitData> ITEMS_GIFTS_BASE_TRAITS = AdvancedDataMapType.builder(
                    ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "gifts/traits/base"),
                    Registries.ITEM,
                    TraitData.CODEC
            )
            .merger(new DataGiftTraitsMerger<>())
            .remover(DataGiftTraitsRemover.codec())
            .build();

    public static final DataMapType<Item, FinalTraitData> ITEMS_GIFTS_FINAL_TRAITS = AdvancedDataMapType.builder(
                    ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "gifts/traits/final"),
                    Registries.ITEM,
                    FinalTraitData.CODEC
            )
            .merger(new FinalDataGiftTraitsMerger<>())
            .remover(FinalDataGiftTraitsRemover.codec())
            .build();
    public static final DataMapType<Fluid, TraitData> FLUIDS_GIFTS_TRAITS = AdvancedDataMapType.builder(
                    ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "gifts/traits"),
                    Registries.FLUID,
                    TraitData.CODEC
            )
            .merger(new DataGiftTraitsMerger<>())
            .remover(DataGiftTraitsRemover.codec())
            .build();
}
