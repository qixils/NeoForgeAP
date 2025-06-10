package gg.archipelago.aprandomizer.ap;

import dev.koifysh.archipelago.Client;
import dev.koifysh.archipelago.flags.ItemsHandling;
import gg.archipelago.aprandomizer.SlotData;
import gg.archipelago.aprandomizer.ap.events.*;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import gg.archipelago.aprandomizer.managers.GoalManager;
import gg.archipelago.aprandomizer.managers.advancementmanager.AdvancementManager;
import gg.archipelago.aprandomizer.managers.itemmanager.ItemManager;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

public class APClient extends Client {

    private final GoalManager goalManager;
    @Nullable
    public SlotData slotData;

    public APClient(MinecraftServer server, AdvancementManager advancementManager, ItemManager itemManager, GoalManager goalManager) {
        super();
        this.goalManager = goalManager;

        this.setGame("Minecraft");
        this.setItemsHandlingFlags(ItemsHandling.SEND_ITEMS | ItemsHandling.SEND_OWN_ITEMS | ItemsHandling.SEND_STARTING_INVENTORY);
        //give the advancement manager the list of checked achievements
        advancementManager.setCheckedAdvancements(new LongOpenHashSet(getLocationManager().getCheckedLocations()));
        //give our item manager the list of received items to give to players as they log in.
        itemManager.setReceivedItems(new LongArrayList(getItemManager().getReceivedItemIDs()));
        this.getEventManager().registerListener(new onDeathLink());
        this.getEventManager().registerListener(new onMC35());
        this.getEventManager().registerListener(new ConnectResult(this, server.registryAccess(), server, advancementManager, itemManager, goalManager));
        this.getEventManager().registerListener(new AttemptedConnection());
        this.getEventManager().registerListener(new ReceiveItem(itemManager));
        this.getEventManager().registerListener(new LocationChecked(advancementManager));
        this.getEventManager().registerListener(new PrintJsonListener());
    }

    @Nullable
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
        goalManager.updateInfoBar();
    }
}
