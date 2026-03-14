package gg.archipelago.aprandomizer.ap.events;

import gg.archipelago.aprandomizer.common.Utils.Utils;
import gg.archipelago.aprandomizer.managers.itemmanager.ItemManager;
import io.github.archipelagomw.Print.APPrintColor;
import io.github.archipelagomw.events.ArchipelagoEventListener;
import io.github.archipelagomw.events.ReceiveItemEvent;
import io.github.archipelagomw.parts.NetworkItem;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
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
            if (!newItem) return;

            int color = Utils.colorOfFlags(item.flags);
            Component textItem = Component.literal(item.itemName).withStyle(Style.EMPTY.withColor(color));
            Component title = Component.literal("Received").withStyle(Style.EMPTY.withColor(APPrintColor.white.color.getRGB()));
            Utils.sendTitleToAll(title, textItem, 5, 30, 5);
        });
    }
}