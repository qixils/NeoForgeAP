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

import java.awt.*;

import static io.github.archipelagomw.flags.NetworkItem.*;
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
                Component textItem;
                if ((item.flags & ADVANCEMENT) == ADVANCEMENT) {
                    textItem = Component.literal(item.itemName).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11508207)));
                }

                else if ((item.flags & USEFUL) == USEFUL){
                    textItem = Component.literal(item.itemName).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(7179240)));
                }
                else if ((item.flags & TRAP) == TRAP){
                    textItem = Component.literal(item.itemName).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(16416882)));
                }
                else{
                    textItem = Component.literal(item.itemName).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(61166)));
                }
                Component title = Component.literal("Received").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(APPrintColor.white.color.getRGB())));
                Utils.sendTitleToAll(title, textItem, 5, 30, 5);
            }
        });
    }
}