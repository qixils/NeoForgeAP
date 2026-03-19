package gg.archipelago.aprandomizer.mixin;

import gg.archipelago.aprandomizer.ap.storage.APMCData;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import gg.archipelago.aprandomizer.managers.GoalManager;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;


@Mixin(ItemStack.class)
public abstract class MixinItemStack {

    @Unique
    public void aprandomizer$setBossLore(APMCData.Bosses bosses) {
        String lore = GoalManager.isBossRequired(bosses)
                ? "It vibrates with power...\nYou must fight this foe when all else is complete."
                : "It lies dormant...\nThis foe is unneeded for your goals.";
        Utils.setItemLore((ItemStack) (Object) this, Collections.singletonList(lore));
    }

    @Inject(method = "<init>(Lnet/minecraft/world/level/ItemLike;ILnet/minecraft/core/component/PatchedDataComponentMap;)V", at = @At("RETURN"))
    private void onInit(ItemLike itemLike, int count, PatchedDataComponentMap tag, CallbackInfo ci) {
        if (itemLike == Items.WITHER_SKELETON_SKULL)
            aprandomizer$setBossLore(APMCData.Bosses.WITHER);
        else if (itemLike == Items.ENDER_PEARL)
            aprandomizer$setBossLore(APMCData.Bosses.ENDER_DRAGON);
    }
}
