package gg.archipelago.aprandomizer.ap.events;

import io.github.archipelagomw.Print.APPrintColor;
import io.github.archipelagomw.events.ArchipelagoEventListener;
import io.github.archipelagomw.events.ReceiveItemEvent;
import io.github.archipelagomw.parts.NetworkItem;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import gg.archipelago.aprandomizer.managers.itemmanager.ItemManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.MinecraftServer;

public class ReceiveItem {

    private final MinecraftServer server;
    private final ItemManager itemManager;

    public ReceiveItem(MinecraftServer server, ItemManager itemManager){
        this.server = server;
        this.itemManager = itemManager;
    }

    @ArchipelagoEventListener
    public void onReceiveItem(ReceiveItemEvent event) {
        server.execute(() -> {
            NetworkItem item = event.getItem();

            boolean newItem = itemManager.giveItemToAll(item.itemID, (int) event.getIndex());

            if (newItem) {
                Component textItem = Component.literal(item.itemName).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(APPrintColor.gold.color.getRGB())));
                Component title = Component.literal("Received").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(APPrintColor.red.color.getRGB())));
                Utils.sendTitleToAll(title, textItem, 10, 60, 10);
            }
        });
    }
}