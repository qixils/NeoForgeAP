package gg.archipelago.aprandomizer.ap;

import dev.koifysh.archipelago.Client;
import dev.koifysh.archipelago.flags.ItemsHandling;
import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.SlotData;
import gg.archipelago.aprandomizer.ap.events.*;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class APClient extends Client {

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public SlotData slotData;

    private final MinecraftServer server;

    public APClient(MinecraftServer server) {
        super();

        this.setGame("Minecraft");
        this.setItemsHandlingFlags(ItemsHandling.SEND_ITEMS | ItemsHandling.SEND_OWN_ITEMS | ItemsHandling.SEND_STARTING_INVENTORY);
        this.server = server;
        APRandomizer.getAdvancementManager().setCheckedAdvancements(new LongOpenHashSet(getLocationManager().getCheckedLocations()));

        //give our item manager the list of received items to give to players as they log in.
        APRandomizer.getItemManager().setReceivedItems(new LongArrayList(getItemManager().getReceivedItemIDs()));

        this.getEventManager().registerListener(new onDeathLink());
        this.getEventManager().registerListener(new onMC35());
        this.getEventManager().registerListener(new ConnectResult(this, server.registryAccess()));
        this.getEventManager().registerListener(new AttemptedConnection());
        this.getEventManager().registerListener(new ReceiveItem());
        this.getEventManager().registerListener(new LocationChecked());
        this.getEventManager().registerListener(new PrintJsonListener());
    }

    public SlotData getSlotData() {
        return slotData;
    }


    @Override
    public void onError(Exception ex) {
        String error = String.format("Connection error: %s", ex.getLocalizedMessage());
        Utils.sendMessageToAll(error);
    }

    @Override
    public void onClose(String reason, int attemptingReconnect) {
        if (attemptingReconnect > 0) {
            Utils.sendMessageToAll(String.format("%s \n... reconnecting in %ds", reason, attemptingReconnect));
        } else {
            Utils.sendMessageToAll(reason);
        }
        APRandomizer.getGoalManager().updateInfoBar();
    }
}
