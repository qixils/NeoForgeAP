package gg.archipelago.aprandomizer.ap.events;

import dev.koifysh.archipelago.Print.APPrintColor;
import dev.koifysh.archipelago.events.ArchipelagoEventListener;
import dev.koifysh.archipelago.events.ReceiveItemEvent;
import dev.koifysh.archipelago.parts.NetworkItem;
import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

public class ReceiveItem {

    @ArchipelagoEventListener
    public void onReceiveItem(ReceiveItemEvent event) {
        APRandomizer.server().ifPresent(server -> server.execute(() -> {
            NetworkItem item = event.getItem();

            boolean newItem = APRandomizer.itemManager().map(value -> value.giveItemToAll(item.itemID, (int) event.getIndex())).orElse(false);

            if (newItem) {
                Component textItem = Component.literal(item.itemName).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(APPrintColor.gold.color.getRGB())));
                Component title = Component.literal("Received").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(APPrintColor.red.color.getRGB())));
                Utils.sendTitleToAll(title, textItem, 10, 60, 10);
            }
        }));


    }
}