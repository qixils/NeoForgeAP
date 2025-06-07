package gg.archipelago.aprandomizer.modifiers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.ap.storage.APMCData;
import gg.archipelago.aprandomizer.datamaps.APDataMaps;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.neoforged.neoforge.common.world.ModifiableStructureInfo;
import net.neoforged.neoforge.common.world.StructureModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO: null safety

public record APStructureModifier(Map<ResourceKey<Level>, LevelReplacements> levels, ResourceKey<Structure> defaultStructure, ResourceLocation name) implements StructureModifier {

    public static final MapCodec<APStructureModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(
                    Codec.unboundedMap(ResourceKey.codec(Registries.DIMENSION), LevelReplacements.CODEC).optionalFieldOf("levels", Map.of()).forGetter(APStructureModifier::levels),
                    ResourceKey.codec(Registries.STRUCTURE).fieldOf("default_structure").forGetter(APStructureModifier::defaultStructure),
                    ResourceLocation.CODEC.fieldOf("name").forGetter(APStructureModifier::name))
            .apply(instance, APStructureModifier::new));

    public static final DeferredRegister<MapCodec<? extends StructureModifier>> STRUCTURE_MODIFIERS = DeferredRegister.create(NeoForgeRegistries.STRUCTURE_MODIFIER_SERIALIZERS, APRandomizer.MODID);
    private static final DeferredHolder<MapCodec<? extends StructureModifier>, MapCodec<APStructureModifier>> AP_STRUCTURE_MODIFIER = STRUCTURE_MODIFIERS.register("ap_structure_modifier", () -> CODEC);

    public static Map<ResourceLocation, ResourceKey<Level>> structures = new HashMap<>();

    public static void loadTags() {
        if (!structures.isEmpty()) return;
        if (APRandomizer.getApmcData().state == APMCData.State.MISSING) {
            APRandomizer.LOGGER.error("APMCData is missing, cannot load tags.");
            return;
        }
        APRandomizer.LOGGER.info("Loading Biome info.");

        APMCData data = APRandomizer.getApmcData();
        for (Map.Entry<String, String> entry : data.structures.entrySet()) {
            switch (entry.getKey()) {
                case "Overworld Structure 1", "Overworld Structure 2" ->
                    structures.put(getResourceLocation(entry.getValue()), Level.OVERWORLD);
                case "Nether Structure 1", "Nether Structure 2" ->
                    structures.put(getResourceLocation(entry.getValue()), Level.NETHER);
                case "The End Structure" ->
                    structures.put(getResourceLocation(entry.getValue()), Level.END);
            }
        }
    }

    @Override
    public void modify(@NotNull Holder<Structure> structure, @NotNull Phase phase, ModifiableStructureInfo.StructureInfo.@NotNull Builder builder) {
        MinecraftServer server = APRandomizer.getServer();
        if (server == null) return;
        if (!phase.equals(Phase.MODIFY) || structure.unwrapKey().isEmpty()) return;
        if (APRandomizer.getApmcData().state == APMCData.State.MISSING) {
            APRandomizer.LOGGER.error("APMCData is missing, cannot modify structures.");
            return;
        }
        if (structures.isEmpty()) loadTags();
        Set<ResourceKey<Structure>> changedStructures = Stream.concat(levels.values().stream().flatMap(levels -> levels.replacements().keySet().stream()), Stream.of(defaultStructure)).collect(Collectors.toSet());
        ResourceKey<Structure> currentStructure = structure.unwrapKey().get();
        if (!structures.containsKey(name) || !changedStructures.contains(currentStructure))
            return;
        APRandomizer.LOGGER.debug("Altering biome list for {}", currentStructure.location());

        ResourceKey<Level> level = structures.get(name);
        HolderSet<Biome> biomes = structure.value().biomes();
        HolderSet<Biome> defaultBiomes = ServerLifecycleHooks.getCurrentServer().registryAccess().lookupOrThrow(Registries.DIMENSION).getData(APDataMaps.DEFAULT_STRUCTURE_BIOMES, level);
        if (defaultBiomes == null) {
            defaultBiomes = HolderSet.empty();
        }
        Map<ResourceKey<Structure>, HolderSet<Biome>> currentReplacements = levels.containsKey(level) ? levels.get(level).replacements() : Map.of(defaultStructure, defaultBiomes);

        biomes = currentReplacements.getOrDefault(currentStructure, HolderSet.empty());

        builder.getStructureSettings().setBiomes(biomes);
    }

    private static ResourceLocation getResourceLocation(String name) {
        return switch (name) {
            case "Village" -> ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "village");
            case "Pillager Outpost" -> ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "pillager_outpost");
            case "Nether Fortress" -> ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "fortress");
            case "Bastion Remnant" -> ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "bastion_remnant");
            case "End City" -> ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "end_city");
            default -> ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "unknown");
        };
    }

    @Override
    public @NotNull MapCodec<APStructureModifier> codec() {
        return CODEC;
    }

    public static record LevelReplacements(Map<ResourceKey<Structure>, HolderSet<Biome>> replacements) {
        public static final Codec<LevelReplacements> CODEC = RecordCodecBuilder.create(instance -> instance
                .group(
                        Codec.unboundedMap(ResourceKey.codec(Registries.STRUCTURE), RegistryCodecs.homogeneousList(Registries.BIOME)).fieldOf("replacements").forGetter(LevelReplacements::replacements))
                .apply(instance, LevelReplacements::new));
    }
}

