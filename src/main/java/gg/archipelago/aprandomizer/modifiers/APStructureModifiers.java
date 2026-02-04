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
    public static final ResourceLocation VILLAGE_NAME = ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "village");
    public static final ResourceLocation PILLAGER_OUTPOST_NAME = ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "pillager_outpost");
    public static final ResourceLocation FORTRESS_NAME = ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "fortress");
    public static final ResourceLocation BASTION_REMNANT_NAME = ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "bastion_remnant");
    public static final ResourceLocation END_CITY_NAME = ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "end_city");
    public static final ResourceLocation WOODLAND_MANSION_NAME = ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "woodland_mansion");
    public static final ResourceLocation OCEAN_MONUMENT_NAME = ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "ocean_monument");
    public static final ResourceLocation ANCIENT_CITY_NAME = ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "ancient_city");
    public static final ResourceLocation TRAIL_RUINS_NAME = ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "trail_ruins");
    public static final ResourceLocation TRIAL_CHAMBERS_NAME = ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "trial_chambers");

    public static final ResourceKey<StructureModifier> VILLAGE = ResourceKey.create(NeoForgeRegistries.Keys.STRUCTURE_MODIFIERS, VILLAGE_NAME);
    public static final ResourceKey<StructureModifier> PILLAGER_OUTPOST = ResourceKey.create(NeoForgeRegistries.Keys.STRUCTURE_MODIFIERS, PILLAGER_OUTPOST_NAME);
    public static final ResourceKey<StructureModifier> FORTRESS = ResourceKey.create(NeoForgeRegistries.Keys.STRUCTURE_MODIFIERS, FORTRESS_NAME);
    public static final ResourceKey<StructureModifier> BASTION_REMNANT = ResourceKey.create(NeoForgeRegistries.Keys.STRUCTURE_MODIFIERS, BASTION_REMNANT_NAME);
    public static final ResourceKey<StructureModifier> END_CITY = ResourceKey.create(NeoForgeRegistries.Keys.STRUCTURE_MODIFIERS, END_CITY_NAME);

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
                VILLAGE_NAME));
        
        context.register(PILLAGER_OUTPOST, new APStructureModifier(
                Map.of(
                        Level.OVERWORLD, new APStructureModifier.LevelReplacements(
                                Map.of(
                                        BuiltinStructures.PILLAGER_OUTPOST, biomes.getOrThrow(BiomeTags.HAS_PILLAGER_OUTPOST)))),
                APStructures.PILLAGER_OUTPOST_NETHER_STRUCTURE,
                PILLAGER_OUTPOST_NAME));
        
        context.register(FORTRESS, new APStructureModifier(
                Map.of(),
                BuiltinStructures.FORTRESS,
                FORTRESS_NAME));

        context.register(BASTION_REMNANT, new APStructureModifier(
                Map.of(),
                BuiltinStructures.BASTION_REMNANT,
                BASTION_REMNANT_NAME));
        
        context.register(END_CITY, new APStructureModifier(
                Map.of(
                        Level.END, new APStructureModifier.LevelReplacements(
                                Map.of(
                                        BuiltinStructures.END_CITY, biomes.getOrThrow(BiomeTags.HAS_END_CITY)))),
                APStructures.END_CITY_NETHER_STRUCTURE,
                END_CITY_NAME));
    }
}
