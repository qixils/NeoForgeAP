package gg.archipelago.aprandomizer.locations;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public record AdvancementLocation(Identifier advancement) implements APLocation {
    public static final MapCodec<AdvancementLocation> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(
                    Identifier.CODEC.fieldOf("advancement").forGetter(AdvancementLocation::advancement))
            .apply(instance, AdvancementLocation::new));

    @Override
    public MapCodec<? extends APLocation> codec() {
        return CODEC;
    }

    @Override
    public void give(ServerPlayer player) {
        if (player.level().getServer() == null)
            return;
        AdvancementHolder advancementHolder = player.level().getServer().getAdvancements().get(advancement);
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
        if (player.level().getServer() == null)
            return false;
        AdvancementHolder advancementHolder = player.level().getServer().getAdvancements().get(advancement);
        if (advancementHolder == null)
            return false;
        AdvancementProgress ap = player.getAdvancements().getOrStartProgress(advancementHolder);
        return ap.isDone();
    }
}
