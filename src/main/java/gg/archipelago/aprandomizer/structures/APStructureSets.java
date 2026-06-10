package gg.archipelago.aprandomizer.structures;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.APStructures;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;

import java.util.List;
import java.util.Optional;

public class APStructureSets {
    public static final ResourceKey<StructureSet> BEE_GROVE = ResourceKey.create(Registries.STRUCTURE_SET, Identifier.fromNamespaceAndPath(APRandomizer.MODID, "bee_grove"));
    public static final ResourceKey<StructureSet> END_CITY_NETHER = ResourceKey.create(Registries.STRUCTURE_SET, Identifier.fromNamespaceAndPath(APRandomizer.MODID, "end_city_nether"));
    public static final ResourceKey<StructureSet> END_CITY_OVERWORLD = ResourceKey.create(Registries.STRUCTURE_SET, Identifier.fromNamespaceAndPath(APRandomizer.MODID, "end_city_overworld"));
    public static final ResourceKey<StructureSet> PILLAGER_OUTPOST_NETHER = ResourceKey.create(Registries.STRUCTURE_SET, Identifier.fromNamespaceAndPath(APRandomizer.MODID, "pillager_outpost_nether"));
    public static final ResourceKey<StructureSet> VILLAGE_NETHER = ResourceKey.create(Registries.STRUCTURE_SET, Identifier.fromNamespaceAndPath(APRandomizer.MODID, "village_nether"));
    public static final ResourceKey<StructureSet> VILLAGE_END = ResourceKey.create(Registries.STRUCTURE_SET, Identifier.fromNamespaceAndPath(APRandomizer.MODID, "village_end"));
    public static final ResourceKey<StructureSet> PILLAGER_OUTPOST_END = ResourceKey.create(Registries.STRUCTURE_SET, Identifier.fromNamespaceAndPath(APRandomizer.MODID, "pillager_outpost_end"));

    public static void bootstrap(BootstrapContext<StructureSet> context) {
        HolderGetter<Structure> structures = context.lookup(Registries.STRUCTURE);
        context.register(BEE_GROVE,
                new StructureSet(
                        List.of(
                                StructureSet.entry(structures.getOrThrow(APStructures.BEEGROVE_STRUCTURE))),
                        new RandomSpreadStructurePlacement(40, 15, RandomSpreadType.LINEAR, 87633157)));

        context.register(END_CITY_NETHER,
                new StructureSet(
                        List.of(
                                StructureSet.entry(structures.getOrThrow(APStructures.END_CITY_NETHER_STRUCTURE))),
                        new RandomSpreadStructurePlacement(27, 4, RandomSpreadType.LINEAR, 30084232)));

        context.register(END_CITY_OVERWORLD,
                new StructureSet(
                        List.of(
                                StructureSet.entry(structures.getOrThrow(APStructures.END_CITY_OVERWORLD_STRUCTURE))),
                        new RandomSpreadStructurePlacement(34, 8, RandomSpreadType.LINEAR, 10387312)));

        Holder.Reference<StructureSet> village_nether =
                context.register(VILLAGE_NETHER,
                new StructureSet(
                        List.of(
                                StructureSet.entry(structures.getOrThrow(APStructures.VILLAGE_NETHER_STRUCTURE))),
                        new RandomSpreadStructurePlacement(27, 4, RandomSpreadType.LINEAR, 10387312)));

        context.register(PILLAGER_OUTPOST_NETHER,
                new StructureSet(
                        List.of(
                                StructureSet.entry(structures.getOrThrow(APStructures.PILLAGER_OUTPOST_NETHER_STRUCTURE))),
                        new RandomSpreadStructurePlacement(
                                Vec3i.ZERO,
                                StructurePlacement.FrequencyReductionMethod.LEGACY_TYPE_1,
                                0.2F,
                                165745296,
                                Optional.of(new StructurePlacement.ExclusionZone(village_nether, 10)),
                                27, 4, RandomSpreadType.LINEAR)));


        Holder.Reference<StructureSet> village_end =
        context.register(VILLAGE_END,
                        new StructureSet(
                                List.of(
                                        StructureSet.entry(structures.getOrThrow(APStructures.VILLAGE_END_STRUCTURE))),
                                new RandomSpreadStructurePlacement(20, 11, RandomSpreadType.TRIANGULAR, 10387313)));

        context.register(PILLAGER_OUTPOST_END,
                new StructureSet(
                        List.of(
                                StructureSet.entry(structures.getOrThrow(APStructures.PILLAGER_OUTPOST_END_STRUCTURE))),
                        new RandomSpreadStructurePlacement(
                                Vec3i.ZERO,
                                StructurePlacement.FrequencyReductionMethod.LEGACY_TYPE_1,
                                1F,
                                165745296,
                                Optional.of(new StructurePlacement.ExclusionZone(village_end, 10)),
                                20, 11, RandomSpreadType.TRIANGULAR)));
    }
}
