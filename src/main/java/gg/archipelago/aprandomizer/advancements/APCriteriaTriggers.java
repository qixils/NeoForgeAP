package gg.archipelago.aprandomizer.advancements;

import gg.archipelago.aprandomizer.APRandomizer;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class APCriteriaTriggers {
    public static final DeferredRegister<CriterionTrigger<?>> CRITERION_TRIGGERS = DeferredRegister.create(Registries.TRIGGER_TYPE, APRandomizer.MODID);

    public static final DeferredHolder<CriterionTrigger<?>, ReceivedItemCriteria> RECEIVED_ITEM = CRITERION_TRIGGERS.register("received_item", ReceivedItemCriteria::new);
}
