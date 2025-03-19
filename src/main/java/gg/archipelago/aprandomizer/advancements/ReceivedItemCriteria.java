package gg.archipelago.aprandomizer.advancements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gg.archipelago.aprandomizer.APRegistries;
import gg.archipelago.aprandomizer.items.APItem;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;

import java.util.Optional;

public class ReceivedItemCriteria extends SimpleCriterionTrigger<ReceivedItemCriteria.TriggerInstance> {

    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, ResourceKey<APItem> item, int tier) {
        this.trigger(player, instance -> instance.item().equals(item) && tier >= instance.tier());
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, ResourceKey<APItem> item, int tier) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance
                .group(
                        EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                        ResourceKey.codec(APRegistries.ARCHIPELAGO_ITEMS).fieldOf("item").forGetter(TriggerInstance::item),
                        ExtraCodecs.POSITIVE_INT.optionalFieldOf("tier", 1).forGetter(TriggerInstance::tier))
                .apply(instance, TriggerInstance::new));

        public static Criterion<TriggerInstance> receivedItem(ResourceKey<APItem> item) {
            return APCriteriaTriggers.RECEIVED_ITEM.get().createCriterion(new TriggerInstance(Optional.empty(), item, 1));
        }

        public static Criterion<TriggerInstance> receivedItem(ResourceKey<APItem> item, int tier) {
            return APCriteriaTriggers.RECEIVED_ITEM.get().createCriterion(new TriggerInstance(Optional.empty(), item, tier));
        }
    }
}
