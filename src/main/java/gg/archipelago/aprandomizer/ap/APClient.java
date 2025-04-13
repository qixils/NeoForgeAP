package gg.archipelago.aprandomizer.ap;

import dev.koifysh.archipelago.Client;
import dev.koifysh.archipelago.ItemFlags;
import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.SlotData;
import gg.archipelago.aprandomizer.ap.events.*;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import gg.archipelago.aprandomizer.managers.GoalManager;
import org.jetbrains.annotations.Nullable;

public class APClient extends Client {

    @Nullable
    public SlotData slotData;

    public APClient() {
        super();

        this.setGame("Minecraft");
        this.setItemsHandlingFlags(ItemFlags.SEND_ITEMS | ItemFlags.SEND_OWN_ITEMS | ItemFlags.SEND_STARTING_INVENTORY);
        APRandomizer.advancementManager().ifPresent(value -> value.setCheckedAdvancements(getLocationManager().getCheckedLocations()));

        //give our item manager the list of received items to give to players as they log in.
        APRandomizer.itemManager().ifPresent(value -> value.setReceivedItems(getItemManager().getReceivedItemIDs()));

        //reset and catch up our global recipe list to be consistent with what we loaded from our save file.
        APRandomizer.recipeManager().ifPresent(value -> {
            value.resetRecipes();
            value.grantRecipeList(getItemManager().getReceivedItemIDs());
        });

        this.getEventManager().registerListener(new onDeathLink());
        this.getEventManager().registerListener(new onMC35());
        this.getEventManager().registerListener(new ConnectResult(this));
        this.getEventManager().registerListener(new AttemptedConnection());
        this.getEventManager().registerListener(new ReceiveItem());
        this.getEventManager().registerListener(new LocationChecked());
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
        APRandomizer.goalManager().ifPresent(GoalManager::updateInfoBar);
    }

}
