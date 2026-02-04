package gg.archipelago.aprandomizer.tags;

import gg.archipelago.aprandomizer.APRandomizer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;

public class APStructureTags {
    public static final TagKey<Structure> BASTION_REMNANT = TagKey.create(Registries.STRUCTURE, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "bastion_remnant"));
    public static final TagKey<Structure> END_CITY = TagKey.create(Registries.STRUCTURE, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "end_city"));
    public static final TagKey<Structure> FORTRESS = TagKey.create(Registries.STRUCTURE, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "fortress"));
    public static final TagKey<Structure> PILLAGER_OUTPOST = TagKey.create(Registries.STRUCTURE, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "pillager_outpost"));
    public static final TagKey<Structure> VILLAGE = TagKey.create(Registries.STRUCTURE, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "village"));
    public static final TagKey<Structure> WOODLAND_MANSION = TagKey.create(Registries.STRUCTURE, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "woodland_mansion"));
    public static final TagKey<Structure> OCEAN_MONUMENT = TagKey.create(Registries.STRUCTURE, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "ocean_monument"));
    public static final TagKey<Structure> ANCIENT_CITY = TagKey.create(Registries.STRUCTURE, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "ancient_city"));
    public static final TagKey<Structure> TRAIL_RUINS = TagKey.create(Registries.STRUCTURE, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "trail_ruins"));
    public static final TagKey<Structure> TRIAL_CHAMBERS = TagKey.create(Registries.STRUCTURE, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "trial_chambers"));
}
