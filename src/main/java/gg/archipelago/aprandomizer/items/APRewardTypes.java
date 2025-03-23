package gg.archipelago.aprandomizer.items;

import com.mojang.serialization.MapCodec;
import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.APRegistries;
import net.minecraft.core.Registry;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class APRewardTypes {
    public static final DeferredRegister<MapCodec<? extends APReward>> REGISTER = DeferredRegister.create(APRegistries.ARCHIPELAGO_REWARD_TYPE, APRandomizer.MODID);

    public static final Registry<MapCodec<? extends APReward>> REGISTRY = REGISTER.makeRegistry(builder -> {});

    public static final DeferredHolder<MapCodec<? extends APReward>, MapCodec<RecipeReward>> RECIPE = REGISTER.register("recipe", () -> RecipeReward.CODEC);
    public static final DeferredHolder<MapCodec<? extends APReward>, MapCodec<ItemReward>> ITEM = REGISTER.register("item", () -> ItemReward.CODEC);
    public static final DeferredHolder<MapCodec<? extends APReward>, MapCodec<ExperienceReward>> EXPERIENCE = REGISTER.register("experience", () -> ExperienceReward.CODEC);
}
