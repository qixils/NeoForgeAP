package gg.archipelago.aprandomizer.data.loot;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.data.loot.modifiers.OverrideItemLootModifier;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.AddTableLootModifier;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

import java.util.concurrent.CompletableFuture;

public class APGlobalLootModifierProvider extends GlobalLootModifierProvider {

    public APGlobalLootModifierProvider(PackOutput output, CompletableFuture<Provider> registries) {
        super(output, registries, APRandomizer.MODID);
    }

    @Override
    protected void start() {
        HolderGetter<Item> items = this.registries.lookupOrThrow(Registries.ITEM);
        this.add("entities/drowned/add_trident", new AddTableLootModifier(
                new LootItemCondition[] {
                    LootTableIdCondition.builder(EntityType.DROWNED.getDefaultLootTable().get().location()).build()
                }, APLootTables.ENTITIES_DROWNED_ADD_TRIDENT));

        this.add("entities/wither_skeleton/override_wither_skeleton_skull", new OverrideItemLootModifier(
                new LootItemCondition[] {
                    LootTableIdCondition.builder(EntityType.WITHER_SKELETON.getDefaultLootTable().get().location()).build()
                },
                ItemPredicate.Builder.item()
                        .of(items, Items.WITHER_SKELETON_SKULL)
                        .build(),
                APLootTables.ENTITIES_WITHER_SKELETON_ADD_WITHER_SKELETON_SKULL));
    }

}
