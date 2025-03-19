package gg.archipelago.aprandomizer.structures;

import com.mojang.datafixers.util.Pair;
import gg.archipelago.aprandomizer.APRandomizer;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.data.worldgen.placement.VillagePlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

import java.util.List;

public class APTemplatePools {

    // Bee Grove
    public static final ResourceKey<StructureTemplatePool> BEE_GROVES = ResourceKey.create(Registries.TEMPLATE_POOL, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "bee_groves"));

    // Nether Pillager Outpost
    public static final ResourceKey<StructureTemplatePool> PILLAGER_OUTPOST_BASE_PLATES = ResourceKey.create(Registries.TEMPLATE_POOL, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "pillager_outpost/base_plates"));
    public static final ResourceKey<StructureTemplatePool> PILLAGER_OUTPOST_FEATURE_PLATES = ResourceKey.create(Registries.TEMPLATE_POOL, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "pillager_outpost/feature_plates"));
    public static final ResourceKey<StructureTemplatePool> PILLAGER_OUTPOST_FEATURES = ResourceKey.create(Registries.TEMPLATE_POOL, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "pillager_outpost/features"));
    public static final ResourceKey<StructureTemplatePool> PILLAGER_OUTPOST_TOWERS = ResourceKey.create(Registries.TEMPLATE_POOL, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "pillager_outpost/towers"));

    // Nether village
    public static final ResourceKey<StructureTemplatePool> VILLAGE_NETHER_DECOR = ResourceKey.create(Registries.TEMPLATE_POOL, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "village/nether/decor"));
    public static final ResourceKey<StructureTemplatePool> VILLAGE_NETHER_HOUSES = ResourceKey.create(Registries.TEMPLATE_POOL, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "village/nether/houses"));
    public static final ResourceKey<StructureTemplatePool> VILLAGE_NETHER_STREETS = ResourceKey.create(Registries.TEMPLATE_POOL, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "village/nether/streets"));
    public static final ResourceKey<StructureTemplatePool> VILLAGE_NETHER_TERMINATORS = ResourceKey.create(Registries.TEMPLATE_POOL, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "village/nether/terminators"));
    public static final ResourceKey<StructureTemplatePool> VILLAGE_NETHER_TOWN_CENTERS = ResourceKey.create(Registries.TEMPLATE_POOL, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "village/nether/town_centers"));
    public static final ResourceKey<StructureTemplatePool> VILLAGE_NETHER_TREES = ResourceKey.create(Registries.TEMPLATE_POOL, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "village/nether/trees"));
    public static final ResourceKey<StructureTemplatePool> VILLAGE_NETHER_VILLAGERS = ResourceKey.create(Registries.TEMPLATE_POOL, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "village/nether/villagers"));

    public static void bootstrap(BootstrapContext<StructureTemplatePool> context) {
        HolderGetter<StructureTemplatePool> pools = context.lookup(Registries.TEMPLATE_POOL);
        HolderGetter<StructureProcessorList> processors = context.lookup(Registries.PROCESSOR_LIST);
        HolderGetter<PlacedFeature> features = context.lookup(Registries.PLACED_FEATURE);
        context.register(BEE_GROVES,
                new StructureTemplatePool(
                        pools.getOrThrow(Pools.EMPTY),
                        List.of(
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":bee_grove"),
                                        50)),
                        StructureTemplatePool.Projection.TERRAIN_MATCHING));
        
        context.register(PILLAGER_OUTPOST_BASE_PLATES,
                new StructureTemplatePool(
                        pools.getOrThrow(Pools.EMPTY),
                        List.of(
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":pillager_outpost/base_plate"),
                                        1)),
                        StructureTemplatePool.Projection.RIGID));

        context.register(PILLAGER_OUTPOST_FEATURE_PLATES,
                new StructureTemplatePool(
                        pools.getOrThrow(Pools.EMPTY),
                        List.of(
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":pillager_outpost/feature_plate"),
                                        1)),
                        StructureTemplatePool.Projection.RIGID));

        context.register(PILLAGER_OUTPOST_FEATURES,
                new StructureTemplatePool(
                        pools.getOrThrow(Pools.EMPTY),
                        List.of(
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":pillager_outpost/feature_cage1"),
                                        1),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":pillager_outpost/feature_cage2"),
                                        1),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":pillager_outpost/feature_logs"),
                                        1),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":pillager_outpost/feature_tent1"),
                                        1),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":pillager_outpost/feature_tent2"),
                                        1),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":pillager_outpost/feature_targets"),
                                        1),
                                Pair.of(
                                        StructurePoolElement.empty(),
                                        6)),
                        StructureTemplatePool.Projection.RIGID));

        context.register(PILLAGER_OUTPOST_TOWERS,
                new StructureTemplatePool(
                        pools.getOrThrow(Pools.EMPTY),
                        List.of(
                                Pair.of(
                                        StructurePoolElement.list(
                                                List.of(
                                                        StructurePoolElement.single(APRandomizer.MODID + ":pillager_outpost/watchtower"),
                                                        StructurePoolElement.single(APRandomizer.MODID + ":pillager_outpost/watchtower_overgrown", processors.getOrThrow(ProcessorLists.OUTPOST_ROT)))),
                                        1)),
                        StructureTemplatePool.Projection.RIGID));

        Holder<StructureTemplatePool> netherVillageTerminators = context.register(VILLAGE_NETHER_TERMINATORS,
                new StructureTemplatePool(
                        pools.getOrThrow(Pools.EMPTY),
                        List.of(
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/terminators/terminator_01"),
                                        1),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/terminators/terminator_02"),
                                        1),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/terminators/terminator_03"),
                                        1),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/terminators/terminator_04"),
                                        1)),
                        StructureTemplatePool.Projection.RIGID));

        context.register(VILLAGE_NETHER_DECOR,
                new StructureTemplatePool(
                        pools.getOrThrow(Pools.EMPTY),
                        List.of(
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/nether_lamp_1"),
                                        2),
                                Pair.of(
                                        StructurePoolElement.feature(features.getOrThrow(VillagePlacements.PILE_HAY_VILLAGE)),
                                        7)),
                        StructureTemplatePool.Projection.RIGID));

        context.register(VILLAGE_NETHER_HOUSES,
                new StructureTemplatePool(
                        netherVillageTerminators,
                        List.of(
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/houses/nether_small_house_1"),
                                        2),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/houses/nether_small_house_2"),
                                        2),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/houses/nether_small_house_3"),
                                        2),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/houses/nether_small_house_4"),
                                        2),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/houses/nether_small_house_5"),
                                        2),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/houses/nether_small_house_6"),
                                        1),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/houses/nether_small_house_7"),
                                        2),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/houses/nether_small_house_8"),
                                        3),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/houses/nether_medium_house_1"),
                                        2),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/houses/nether_medium_house_2"),
                                        2),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/houses/nether_big_house_1"),
                                        2),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/houses/nether_butcher_shop_1"),
                                        2),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/houses/nether_butcher_shop_2"),
                                        2),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/houses/nether_tool_smith_1"),
                                        2),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/houses/nether_fletcher_house_1"),
                                        2),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/houses/nether_shepherds_house_1"),
                                        2),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/houses/nether_armorer_house_1"),
                                        2),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/houses/nether_fisher_cottage_1"),
                                        2),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/houses/nether_tannery_1"),
                                        2),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/houses/nether_cartographer_1"),
                                        1),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/houses/nether_library_1"),
                                        5),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/houses/nether_library_2"),
                                        1),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/houses/nether_masons_house_1"),
                                        2),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/houses/nether_weaponsmith_1"),
                                        2),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/houses/nether_temple_3"),
                                        2),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/houses/nether_temple_4"),
                                        2),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/houses/nether_stable_1"),
                                        2),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/houses/nether_stable_2"),
                                        2),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/houses/nether_large_farm_1"),
                                        4),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/houses/nether_small_farm_1"),
                                        4),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/houses/nether_animal_pen_1"),
                                        1),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/houses/nether_animal_pen_2"),
                                        1),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/houses/nether_animal_pen_3"),
                                        5),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/houses/nether_accessory_1"),
                                        1),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/houses/nether_meeting_point_4"),
                                        3),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/houses/nether_meeting_point_5"),
                                        1),
                                Pair.of(
                                        StructurePoolElement.empty(),
                                        10)),
                        StructureTemplatePool.Projection.RIGID));

        context.register(VILLAGE_NETHER_STREETS,
                new StructureTemplatePool(
                        netherVillageTerminators,
                        List.of(
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/streets/corner_01"),
                                        2),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/streets/corner_02"),
                                        2),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/streets/corner_03"),
                                        2),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/streets/straight_01"),
                                        4),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/streets/straight_02"),
                                        4),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/streets/straight_03"),
                                        7),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/streets/straight_04"),
                                        7),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/streets/straight_05"),
                                        3),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/streets/straight_06"),
                                        4),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/streets/crossroad_01"),
                                        2),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/streets/crossroad_02"),
                                        1),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/streets/crossroad_03"),
                                        2),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/streets/crossroad_04"),
                                        2),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/streets/crossroad_05"),
                                        2),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/streets/crossroad_06"),
                                        2),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/streets/turn_01"),
                                        3)),
                        StructureTemplatePool.Projection.RIGID));

        context.register(VILLAGE_NETHER_TOWN_CENTERS,
                new StructureTemplatePool(
                        pools.getOrThrow(Pools.EMPTY),
                        List.of(
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/town_centers/nether_fountain_01"),
                                        50),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/town_centers/nether_meeting_point_1"),
                                        50),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/town_centers/nether_meeting_point_2"),
                                        50),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/town_centers/nether_meeting_point_3"),
                                        50)),
                        StructureTemplatePool.Projection.RIGID));

        context.register(VILLAGE_NETHER_TREES,
                new StructureTemplatePool(
                        pools.getOrThrow(Pools.EMPTY),
                        List.of(),
                        StructureTemplatePool.Projection.RIGID));

        context.register(VILLAGE_NETHER_VILLAGERS,
                new StructureTemplatePool(
                        pools.getOrThrow(Pools.EMPTY),
                        List.of(
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/villagers/nitwit"),
                                        1),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/villagers/baby"),
                                        1),
                                Pair.of(
                                        StructurePoolElement.single(APRandomizer.MODID + ":village/nether/villagers/unemployed"),
                                        10)),
                        StructureTemplatePool.Projection.RIGID));
    }
}
