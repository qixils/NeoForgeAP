package gg.archipelago.aprandomizer.modifiers;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.APStructures;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.neoforged.neoforge.common.world.StructureModifier;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Map;

public class APStructureModifiers {
    public static final ResourceKey<StructureModifier> VILLAGE = ResourceKey.create(NeoForgeRegistries.Keys.STRUCTURE_MODIFIERS, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "village"));
    public static final ResourceKey<StructureModifier> PILLAGER_OUTPOST = ResourceKey.create(NeoForgeRegistries.Keys.STRUCTURE_MODIFIERS, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "pillager_outpost"));
    public static final ResourceKey<StructureModifier> FORTRESS = ResourceKey.create(NeoForgeRegistries.Keys.STRUCTURE_MODIFIERS, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "fortress"));
    public static final ResourceKey<StructureModifier> BASTION_REMNANT = ResourceKey.create(NeoForgeRegistries.Keys.STRUCTURE_MODIFIERS, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "bastion_remnant"));
    public static final ResourceKey<StructureModifier> END_CITY = ResourceKey.create(NeoForgeRegistries.Keys.STRUCTURE_MODIFIERS, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "end_city"));

    public static void bootstrap(BootstrapContext<StructureModifier> context) {
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
        context.register(VILLAGE, new APStructureModifier(
                Map.of(
                        Level.OVERWORLD, new APStructureModifier.LevelReplacements(
                                Map.of(
                                        BuiltinStructures.VILLAGE_PLAINS, biomes.getOrThrow(BiomeTags.HAS_VILLAGE_PLAINS),
                                        BuiltinStructures.VILLAGE_DESERT, biomes.getOrThrow(BiomeTags.HAS_VILLAGE_DESERT),
                                        BuiltinStructures.VILLAGE_SAVANNA, biomes.getOrThrow(BiomeTags.HAS_VILLAGE_SAVANNA),
                                        BuiltinStructures.VILLAGE_SNOWY, biomes.getOrThrow(BiomeTags.HAS_VILLAGE_SNOWY),
                                        BuiltinStructures.VILLAGE_TAIGA, biomes.getOrThrow(BiomeTags.HAS_VILLAGE_TAIGA)))),
                APStructures.VILLAGE_NETHER_STRUCTURE,
                ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "village")));
        
        context.register(PILLAGER_OUTPOST, new APStructureModifier(
                Map.of(
                        Level.OVERWORLD, new APStructureModifier.LevelReplacements(
                                Map.of(
                                        BuiltinStructures.PILLAGER_OUTPOST, biomes.getOrThrow(BiomeTags.HAS_PILLAGER_OUTPOST)))),
                APStructures.PILLAGER_OUTPOST_NETHER_STRUCTURE,
                ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "pillager_outpost")));
        
        context.register(FORTRESS, new APStructureModifier(
                Map.of(),
                BuiltinStructures.FORTRESS,
                ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "fortress")));

        context.register(BASTION_REMNANT, new APStructureModifier(
                Map.of(),
                BuiltinStructures.BASTION_REMNANT,
                ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "bastion_remnant")));
        
        context.register(END_CITY, new APStructureModifier(
                Map.of(
                        Level.END, new APStructureModifier.LevelReplacements(
                                Map.of(
                                        BuiltinStructures.END_CITY, biomes.getOrThrow(BiomeTags.HAS_END_CITY)))),
                APStructures.END_CITY_NETHER_STRUCTURE,
                ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "end_city")));
    }
}
