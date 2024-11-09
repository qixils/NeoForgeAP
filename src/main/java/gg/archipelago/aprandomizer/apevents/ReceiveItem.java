package gg.archipelago.aprandomizer.apevents;

import dev.koifysh.archipelago.Print.APPrintColor;
import dev.koifysh.archipelago.events.ArchipelagoEventListener;
import dev.koifysh.archipelago.events.ReceiveItemEvent;
import dev.koifysh.archipelago.parts.NetworkItem;
import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import gg.archipelago.aprandomizer.data.WorldData;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

public class ReceiveItem {

    @ArchipelagoEventListener
    public void onReceiveItem(ReceiveItemEvent event) {
        NetworkItem item = event.getItem();
        APRandomizer.recipeManager().ifPresent(value -> value.grantRecipe(item.itemID));
        APRandomizer.itemManager().ifPresent(value -> value.giveItemToAll(item.itemID, (int) event.getIndex()));

        // Dont fire if we have all ready recevied this location
        WorldData worldData = APRandomizer.getWorldData();
        if (worldData == null) return;
        if (event.getIndex() <= worldData.getItemIndex()) return;

        APRandomizer.getWorldData().setItemIndex((int) event.getIndex());

        Component textItem = Component.literal(item.itemName).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(APPrintColor.gold.color.getRGB())));
        Component title = Component.literal("Received").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(APPrintColor.red.color.getRGB())));
        Utils.sendTitleToAll(title, textItem, 10, 60, 10);


    }
}
