package gg.archipelago.aprandomizer.mixin;

import gg.archipelago.aprandomizer.ap.storage.APMCData;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import gg.archipelago.aprandomizer.managers.GoalManager;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;


@Mixin(ItemStack.class)
public abstract class MixinItemStack {

//    @Inject(method = "<init>(Lnet/minecraft/world/level/ItemLike;ILnet/minecraft/core/component/PatchedDataComponentMap;)V", at = @At("RETURN"))
//    private void onInit(ItemLike itemLike, int count, PatchedDataComponentMap tag, CallbackInfo ci) {
//        if (itemLike == Items.WITHER_SKELETON_SKULL) {
//            ArrayList<String> lore = new ArrayList<>();
//            if (GoalManager.isBossRequired(APMCData.Bosses.WITHER)) {
//                lore.add("Vibrates with power...");
//            } else {
//                lore.add("lies dormant...");
//            }
//
//            Utils.setItemLore((ItemStack) (Object) this, lore);
//        } else if (itemLike == Items.ENDER_PEARL) {
//            ArrayList<String> lore = new ArrayList<>();
//            if (GoalManager.isBossRequired(APMCData.Bosses.ENDER_DRAGON)) {
//                lore.add("Vibrates with power...");
//            } else {
//                lore.add("lies dormant...");
//            }
//            Utils.setItemLore((ItemStack) (Object) this, lore);
//        }
//    }
}
