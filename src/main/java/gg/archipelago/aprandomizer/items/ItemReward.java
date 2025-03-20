package gg.archipelago.aprandomizer.items;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;

public record ItemReward(ItemStack item) implements APReward {

    public static final MapCodec<ItemReward> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(
                    ItemStack.STRICT_CODEC.fieldOf("item").forGetter(ItemReward::item))
            .apply(instance, ItemReward::new));

    @Override
    public MapCodec<? extends APReward> codec() {
        return CODEC;
    }

}
