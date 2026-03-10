package gg.archipelago.aprandomizer.items;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStackTemplate;

public record ItemReward(ItemStackTemplate item) implements APReward {

    public static final MapCodec<ItemReward> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(
                    ItemStackTemplate.MAP_CODEC.fieldOf("item").forGetter(ItemReward::item))
            .apply(instance, ItemReward::new));
    @Override
    public MapCodec<? extends APReward> codec() {
        return CODEC;
    }

    @Override
    public void give(ServerPlayer player) {
        Utils.giveItemToPlayer(player, item.create());
    }
}
