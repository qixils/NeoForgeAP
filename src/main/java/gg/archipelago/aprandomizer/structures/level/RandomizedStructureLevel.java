package gg.archipelago.aprandomizer.structures.level;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

public record RandomizedStructureLevel(Identifier name) implements StructureLevelReference {

    public static final MapCodec<RandomizedStructureLevel> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(
                    Identifier.CODEC.fieldOf("name").forGetter(RandomizedStructureLevel::name))
            .apply(instance, RandomizedStructureLevel::new));

    @Override
    public MapCodec<? extends StructureLevelReference> codec() {
        return CODEC;
    }

    @Override
    public ResourceKey<Level> level() {
        return Utils.getStructureWorld(name);
    }

}
