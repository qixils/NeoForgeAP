package gg.archipelago.aprandomizer;

import com.mojang.serialization.MapCodec;
import gg.archipelago.aprandomizer.structures.BeeGroveStructure;
import gg.archipelago.aprandomizer.structures.NetherEndCityStructure;
import gg.archipelago.aprandomizer.structures.NetherPillagerOutpostStructure;
import gg.archipelago.aprandomizer.structures.NetherVillageStructure;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;


public class APStructures {

    public static final DeferredRegister<StructureType<?>> DEFERRED_REGISTRY_STRUCTURE = DeferredRegister.create(Registries.STRUCTURE_TYPE, APRandomizer.MODID);

    public static final DeferredHolder<StructureType<?>, StructureType<NetherVillageStructure>> VILLAGE_NETHER = DEFERRED_REGISTRY_STRUCTURE.register("village_nether", () -> explicitStructureTypeTyping(NetherVillageStructure.CODEC));
    public static final DeferredHolder<StructureType<?>, StructureType<NetherEndCityStructure>> END_CITY_NETHER = DEFERRED_REGISTRY_STRUCTURE.register("end_city_nether", () -> explicitStructureTypeTyping(NetherEndCityStructure.CODEC));
    public static final DeferredHolder<StructureType<?>, StructureType<NetherPillagerOutpostStructure>> PILLAGER_OUTPOST_NETHER = DEFERRED_REGISTRY_STRUCTURE.register("pillager_outpost_nether", () -> explicitStructureTypeTyping(NetherPillagerOutpostStructure.CODEC));
    public static final DeferredHolder<StructureType<?>, StructureType<BeeGroveStructure>> BEE_GROVE = DEFERRED_REGISTRY_STRUCTURE.register("bee_grove", () -> explicitStructureTypeTyping(BeeGroveStructure.CODEC));


    /**
     * Originally, I had a double lambda ()->()-> for the RegistryObject line above, but it turns out that
     * some IDEs cannot resolve the typing correctly. This method explicitly states what the return type
     * is so that the IDE can put it into the DeferredRegistry properly.
     */
    private static <T extends Structure> StructureType<T> explicitStructureTypeTyping(MapCodec<T> structureCodec) {
        return () -> structureCodec;
    }

    public static final TagKey<Structure> VILLAGE_TAG = TagKey.create(Registries.STRUCTURE, ResourceLocation.parse("aprandomizer:village"));
    public static final TagKey<Structure> OUTPOST_TAG = TagKey.create(Registries.STRUCTURE, ResourceLocation.parse("aprandomizer:pillager_outpost"));
    public static final TagKey<Structure> END_CITY_TAG = TagKey.create(Registries.STRUCTURE, ResourceLocation.parse("aprandomizer:end_city"));
    public static final TagKey<Structure> BASTION_REMNANT_TAG = TagKey.create(Registries.STRUCTURE, ResourceLocation.parse("aprandomizer:bastion_remnant"));
    public static final TagKey<Structure> FORTRESS_TAG = TagKey.create(Registries.STRUCTURE, ResourceLocation.parse("aprandomizer:fortress"));


}
