package gg.archipelago.aprandomizer.locations;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public record AdvancementLocation(ResourceLocation advancement) implements APLocation {
    public static final MapCodec<AdvancementLocation> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(
                    ResourceLocation.CODEC.fieldOf("advancement").forGetter(AdvancementLocation::advancement))
            .apply(instance, AdvancementLocation::new));

    @Override
    public MapCodec<? extends APLocation> codec() {
        return CODEC;
    }

    @Override
    public void give(ServerPlayer player) {
        AdvancementHolder advancementHolder = player.getServer().getAdvancements().get(advancement);
        if (advancementHolder == null)
            return;
        AdvancementProgress ap = player.getAdvancements().getOrStartProgress(advancementHolder);
        if (ap.isDone())
            return;
        for (String remainingCriterion : ap.getRemainingCriteria()) {
            player.getAdvancements().award(advancementHolder, remainingCriterion);
        }

    }

    @Override
    public boolean hasFound(ServerPlayer player) {
        AdvancementHolder advancementHolder = player.getServer().getAdvancements().get(advancement);
        if (advancementHolder == null)
            return false;
        AdvancementProgress ap = player.getAdvancements().getOrStartProgress(advancementHolder);
        return ap.isDone();
    }
}
