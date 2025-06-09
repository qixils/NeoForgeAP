package gg.archipelago.aprandomizer.mixin;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.managers.itemmanager.ItemManager;
import net.minecraft.resources.ResourceKey;
import net.minecraft.stats.ServerRecipeBook;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(ServerRecipeBook.class)
public class MixinServerRecipeBook {
    @Inject(method = "add", at = @At("HEAD"), cancellable = true)
    public void onAdd(ResourceKey<Recipe<?>> p_379734_, CallbackInfo ci) {
        if (APRandomizer.server == null) return;
        //if (ItemManager.getLockedRecipes(APRandomizer.server().get().registryAccess()).contains(p_379734_))
        //    ci.cancel();
        if(ItemManager.getLockedRecipes(APRandomizer.server.registryAccess()).contains(p_379734_))
            ci.cancel();
    }

    @ModifyVariable(method = "addRecipes", at = @At(value = "HEAD"), ordinal = 0, argsOnly = true)
    public Collection<RecipeHolder<?>> onAddRecipes(Collection<RecipeHolder<?>> p_12792_) {
        if (APRandomizer.server == null || p_12792_.isEmpty()) return p_12792_;
        //var lockedRecipes = ItemManager.getLockedRecipes(APRandomizer.server().get().registryAccess());
        var lockedRecipes = ItemManager.getLockedRecipes(APRandomizer.server.registryAccess());
        if (lockedRecipes.isEmpty()) return p_12792_;
        return p_12792_.stream()
                .filter(value -> !lockedRecipes.contains(value.id()))
                .toList();
    }
}
