package gg.archipelago.aprandomizer.data.loot.modifiers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

import java.util.function.Predicate;

public class OverrideItemLootModifier extends LootModifier {

    public static final MapCodec<OverrideItemLootModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> LootModifier.codecStart(instance)
            .and(instance.group(
                    ItemPredicate.CODEC.fieldOf("predicate").forGetter(OverrideItemLootModifier::getPredicate),
                    ResourceKey.codec(Registries.LOOT_TABLE).fieldOf("table").forGetter(OverrideItemLootModifier::getTable)))
            .apply(instance, OverrideItemLootModifier::new));

    private final ItemPredicate predicate;
    private final ResourceKey<LootTable> table;

    public OverrideItemLootModifier(LootItemCondition[] conditionsIn, ItemPredicate predicate, ResourceKey<LootTable> table) {
        super(conditionsIn);
        this.predicate = predicate;
        this.table = table;
    }

    public ItemPredicate getPredicate() {
        return predicate;
    }

    public ResourceKey<LootTable> getTable() {
        return table;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        ObjectArrayList<ItemStack> newLoot = generatedLoot.stream().filter(Predicate.not(predicate)).collect(ObjectArrayList.toList());
        context.getResolver().lookupOrThrow(Registries.LOOT_TABLE).get(table).ifPresent(newTable -> newTable.value().getRandomItemsRaw(context, LootTable.createStackSplitter(context.getLevel(), newLoot::add)));
        return newLoot;
    }

}
