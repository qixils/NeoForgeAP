package gg.archipelago.aprandomizer.items;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.Recipe;

import java.util.List;

public record RecipeReward(ResourceKey<Recipe<?>> recipe) implements APReward {

    public static final MapCodec<RecipeReward> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(
                    ResourceKey.codec(Registries.RECIPE).fieldOf("recipe").forGetter(RecipeReward::recipe))
            .apply(instance, RecipeReward::new));

    public RecipeReward(ResourceLocation recipe) {
        this(ResourceKey.create(Registries.RECIPE, recipe));
    }

    @Override
    public MapCodec<? extends APReward> codec() {
        return CODEC;
    }

    @Override
    public void give(ServerPlayer player) {
        player.awardRecipesByKey(List.of(recipe));
    }

}
