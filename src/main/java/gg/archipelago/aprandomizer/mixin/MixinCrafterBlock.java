package gg.archipelago.aprandomizer.mixin;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.managers.itemmanager.ItemManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.CrafterBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(CrafterBlock.class)
public abstract class MixinCrafterBlock {
    @Inject(method = "getPotentialResults(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/crafting/CraftingInput;)Ljava/util/Optional;", at = @At("RETURN"), cancellable = true)
    private static void filterCrafterRecipes(ServerLevel level, CraftingInput input, CallbackInfoReturnable<Optional<RecipeHolder<CraftingRecipe>>> info) {
        ItemManager itemManager = APRandomizer.getItemManager();
        if (itemManager != null) {
            info.setReturnValue(info.getReturnValue().filter(recipe -> !itemManager.getLockedRecipes(level.registryAccess()).contains(recipe.id())));
        }
    }

}
