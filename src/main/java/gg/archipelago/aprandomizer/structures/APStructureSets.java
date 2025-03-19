package gg.archipelago.aprandomizer.structures;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.APStructures;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;

import java.util.List;

public class APStructureSets {
    public static final ResourceKey<StructureSet> BEE_GROVE = ResourceKey.create(Registries.STRUCTURE_SET, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "bee_grove"));
    public static final ResourceKey<StructureSet> END_CITY_NETHER = ResourceKey.create(Registries.STRUCTURE_SET, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "end_city_nether"));
    public static final ResourceKey<StructureSet> PILLAGER_OUTPOST_NETHER = ResourceKey.create(Registries.STRUCTURE_SET, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "pillager_outpost_nether"));
    public static final ResourceKey<StructureSet> VILLAGE_NETHER = ResourceKey.create(Registries.STRUCTURE_SET, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "village_nether"));

    public static void bootstrap(BootstrapContext<StructureSet> context) {
        HolderGetter<Structure> structures = context.lookup(Registries.STRUCTURE);
        context.register(BEE_GROVE,
                new StructureSet(
                        List.of(
                                StructureSet.entry(structures.getOrThrow(APStructures.BEEGROVE_STRUCTURE))),
                        new RandomSpreadStructurePlacement(12, 8, RandomSpreadType.LINEAR, 87633157)));

        context.register(END_CITY_NETHER,
                new StructureSet(
                        List.of(
                                StructureSet.entry(structures.getOrThrow(APStructures.END_CITY_NETHER_STRUCTURE))),
                        new RandomSpreadStructurePlacement(20, 10, RandomSpreadType.LINEAR, 92464638)));

        context.register(PILLAGER_OUTPOST_NETHER,
                new StructureSet(
                        List.of(
                                StructureSet.entry(structures.getOrThrow(APStructures.PILLAGER_OUTPOST_NETHER_STRUCTURE))),
                        new RandomSpreadStructurePlacement(16, 6, RandomSpreadType.LINEAR, 44897651)));

        context.register(VILLAGE_NETHER,
                new StructureSet(
                        List.of(
                                StructureSet.entry(structures.getOrThrow(APStructures.VILLAGE_NETHER_STRUCTURE))),
                        new RandomSpreadStructurePlacement(16, 6, RandomSpreadType.LINEAR, 784665)));
    }
}
