package gg.archipelago.aprandomizer.structures;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gg.archipelago.aprandomizer.APStructures;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.structures.EndCityPieces;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class NetherEndCityStructure extends Structure {
    public static final MapCodec<NetherEndCityStructure> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(settingsCodec(instance)
            ).apply(instance, NetherEndCityStructure::new));

    public NetherEndCityStructure(Structure.StructureSettings config) {
        super(config);
    }


    /*
     * This is where extra checks can be done to determine if the structure can spawn here.
     * This only needs to be overridden if you're adding additional spawn conditions.
     *
     * Fun fact, if you set your structure separation/spacing to be 0/1, you can use
     * isFeatureChunk to return true only if certain chunk coordinates are passed in
     * which allows you to spawn structures only at certain coordinates in the world.
     *
     * Basically, this method is used for determining if the land is at a suitable height,
     * if certain other structures are too close or not, or some other restrictive condition.
     *
     * For example, Pillager Outposts added a check to make sure it cannot spawn within 10 chunk of a Village.
     * (Bedrock Edition seems to not have the same check)
     *
     * If you are doing Nether structures, you'll probably want to spawn your structure on top of ledges.
     * Best way to do that is to use getBaseColumn to grab a column of blocks at the structure's x/z position.
     * Then loop through it and look for land with air above it and set blockpos's Y value to it.
     * Make sure to set the final boolean in JigsawPlacement.addPieces to false so
     * that the structure spawns at blockpos's y value instead of placing the structure on the Bedrock roof!
     *
     * Also, please for the love of god, do not do dimension checking here.
     * If you do and another mod's dimension is trying to spawn your structure,
     * the locate command will make minecraft hang forever and break the game.
     * Use the biome tags for where to spawn the structure and users can datapack
     * it to spawn in specific biomes that aren't in the dimension they don't like if they wish.
     */
    private static boolean extraSpawningChecks(Structure.GenerationContext context) {
        // Grabs the chunk position we are at
        ChunkPos chunkpos = context.chunkPos();

        // Checks to make sure our structure does not spawn above land that's higher than y = 150
        // to demonstrate how this method is good for checking extra conditions for spawning
        return context.chunkGenerator().getFirstFreeHeight(
                chunkpos.getMinBlockX(),
                chunkpos.getMinBlockZ(),
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                context.heightAccessor(),
                context.randomState()) < 150;
    }

    @Override
    public @NotNull Optional<Structure.GenerationStub> findGenerationPoint(Structure.@NotNull GenerationContext context) {

        // Check if the spot is valid for our structure. This is just as another method for cleanness.
        // Returning an empty optional tells the game to skip this spot as it will not generate the structure.
        if (!NetherEndCityStructure.extraSpawningChecks(context)) {
            return Optional.empty();
        }
        // get sea Level, or in our case lava level.
        int seaLevel = context.chunkGenerator().getSeaLevel();

        /*
         * yay for custom hard coded structure gen. let's just call this and hope it works...
         */
        BlockPos blockpos = context.chunkPos().getMiddleBlockPosition(seaLevel);

        return Optional.of(new Structure.GenerationStub(blockpos, structure -> {
            Rotation rotation = Rotation.getRandom(context.random());
            List<StructurePiece> list = Lists.newArrayList();
            EndCityPieces.startHouseTower(context.structureTemplateManager(), blockpos, rotation, list, context.random());
            list.forEach(structure::addPiece);
        }));
    }

    @Override
    public @NotNull StructureType<?> type() {
        return APStructures.END_CITY_NETHER.get();
    }

}