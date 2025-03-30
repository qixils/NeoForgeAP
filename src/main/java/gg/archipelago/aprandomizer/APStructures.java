package gg.archipelago.aprandomizer;

import gg.archipelago.aprandomizer.structures.*;
import gg.archipelago.aprandomizer.tags.APBiomeTags;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.Structure.StructureSettings;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Map;
import java.util.Optional;

public class APStructures {

    public static final DeferredRegister<StructureType<?>> DEFERRED_REGISTRY_STRUCTURE = DeferredRegister.create(Registries.STRUCTURE_TYPE, APRandomizer.MODID);

    public static final DeferredHolder<StructureType<?>, StructureType<NetherVillageStructure>> VILLAGE_NETHER = DEFERRED_REGISTRY_STRUCTURE.register("village_nether", () -> () -> NetherVillageStructure.CODEC);
    public static final DeferredHolder<StructureType<?>, StructureType<NetherEndCityStructure>> END_CITY_NETHER = DEFERRED_REGISTRY_STRUCTURE.register("end_city_nether", () -> () -> NetherEndCityStructure.CODEC);
    public static final DeferredHolder<StructureType<?>, StructureType<NetherPillagerOutpostStructure>> PILLAGER_OUTPOST_NETHER = DEFERRED_REGISTRY_STRUCTURE.register("pillager_outpost_nether", () -> () -> NetherPillagerOutpostStructure.CODEC);
    public static final DeferredHolder<StructureType<?>, StructureType<BeeGroveStructure>> BEE_GROVE = DEFERRED_REGISTRY_STRUCTURE.register("bee_grove", () -> () -> BeeGroveStructure.CODEC);

    public static final ResourceKey<Structure> VILLAGE_NETHER_STRUCTURE = ResourceKey.create(Registries.STRUCTURE, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "village_nether"));
    public static final ResourceKey<Structure> END_CITY_NETHER_STRUCTURE = ResourceKey.create(Registries.STRUCTURE, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "end_city_nether"));
    public static final ResourceKey<Structure> PILLAGER_OUTPOST_NETHER_STRUCTURE = ResourceKey.create(Registries.STRUCTURE, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "pillager_outpost_nether"));
    public static final ResourceKey<Structure> BEEGROVE_STRUCTURE = ResourceKey.create(Registries.STRUCTURE, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "bee_grove"));

    public static void bootstrap(BootstrapContext<Structure> context) {
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
        HolderGetter<StructureTemplatePool> pools = context.lookup(Registries.TEMPLATE_POOL);
        context.register(BEEGROVE_STRUCTURE,
                new BeeGroveStructure(
                        new StructureSettings.Builder(biomes.getOrThrow(APBiomeTags.BEE_GROVE_BIOMES))
                                .generationStep(GenerationStep.Decoration.SURFACE_STRUCTURES)
                                .build(),
                        pools.getOrThrow(APTemplatePools.BEE_GROVES),
                        Optional.empty(),
                        6,
                        ConstantHeight.of(VerticalAnchor.absolute(0)),
                        Optional.of(Heightmap.Types.WORLD_SURFACE_WG),
                        80));

        context.register(END_CITY_NETHER_STRUCTURE,
                new NetherEndCityStructure(
                        new StructureSettings.Builder(HolderSet.empty())
                                .generationStep(GenerationStep.Decoration.SURFACE_STRUCTURES)
                                .build()));

        context.register(PILLAGER_OUTPOST_NETHER_STRUCTURE,
                new NetherPillagerOutpostStructure(
                        new StructureSettings.Builder(HolderSet.empty())
                                .generationStep(GenerationStep.Decoration.SURFACE_STRUCTURES)
                                .terrainAdapation(TerrainAdjustment.BEARD_THIN)
                                .spawnOverrides(
                                        Map.of(
                                                MobCategory.MONSTER, new StructureSpawnOverride(
                                                        StructureSpawnOverride.BoundingBoxType.STRUCTURE,
                                                        WeightedList.<MobSpawnSettings.SpawnerData>builder()
                                                                .add(new Weighted<>(new MobSpawnSettings.SpawnerData(EntityType.PILLAGER, 5, 1), 2))
                                                                .build())))
                                .build(),
                        pools.getOrThrow(APTemplatePools.PILLAGER_OUTPOST_BASE_PLATES),
                        Optional.empty(),
                        6,
                        UniformHeight.of(
                                VerticalAnchor.aboveBottom(16),
                                VerticalAnchor.belowTop(10)),
                        Optional.empty(),
                        80));

        context.register(VILLAGE_NETHER_STRUCTURE,
                new NetherVillageStructure(
                        new StructureSettings.Builder(HolderSet.empty())
                                .generationStep(GenerationStep.Decoration.SURFACE_STRUCTURES)
                                .terrainAdapation(TerrainAdjustment.BEARD_THIN)
                                .build(),
                        pools.getOrThrow(APTemplatePools.VILLAGE_NETHER_TOWN_CENTERS),
                        Optional.empty(),
                        6,
                        UniformHeight.of(
                                VerticalAnchor.aboveBottom(16),
                                VerticalAnchor.belowTop(10)),
                        Optional.empty(),
                        80));
    }
}
