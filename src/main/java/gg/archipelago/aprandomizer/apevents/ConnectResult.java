package gg.archipelago.aprandomizer.apevents;

import dev.koifysh.archipelago.events.ArchipelagoEventListener;
import dev.koifysh.archipelago.events.ConnectionResultEvent;
import dev.koifysh.archipelago.helper.DeathLink;
import dev.koifysh.archipelago.network.ConnectionResult;
import gg.archipelago.aprandomizer.APClient;
import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.SlotData;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConnectResult {

    private static final Logger LOGGER = LogManager.getLogger();
    final APClient client;

    public ConnectResult(APClient apClient) {
        client = apClient;
    }

    @ArchipelagoEventListener
    public void onConnectResult(ConnectionResultEvent event) {
        if (event.getResult() == ConnectionResult.Success) {
            Utils.sendMessageToAll("Successfully connected to " + client.getConnectedAddress());
            try {
                client.slotData = event.getSlotData(SlotData.class);
                client.slotData.parseStartingItems();
            } catch (Exception e) {
                if (client.slotData != null) {
                    Utils.sendMessageToAll("Invalid starting item section, check logs for more details.");
                    LOGGER.warn("Invalid staring items json string: {}", client.slotData.startingItems, e);
                } else {
                    Utils.sendMessageToAll("Invalid slot data, check logs for more details.");
                    LOGGER.warn("Invalid slot data", e);
                }
            }

            if (client.slotData.MC35) {
                Utils.sendMessageToAll("Welcome to Minecraft 35.");
                client.addTag("MC35");
            }
            if (client.slotData.deathlink) {
                Utils.sendMessageToAll("Welcome to Death Link.");
                DeathLink.setDeathLinkEnabled(true);
            }

            APRandomizer.advancementManager().ifPresent(value -> value.setCheckedAdvancements(client.getLocationManager().getCheckedLocations()));

            //give our item manager the list of received items to give to players as they log in.
            APRandomizer.itemManager().ifPresent(value -> value.setReceivedItems(client.getItemManager().getReceivedItemIDs()));

            //reset and catch up our global recipe list to be consistent with what we just got from the AP server
            APRandomizer.recipeManager().ifPresent(value -> {
                value.resetRecipes();
                value.grantRecipeList(client.getItemManager().getReceivedItemIDs());
            });

            //catch up all connected players to the list just received.
            // TODO: broken ish
            APRandomizer.server().ifPresent(server -> server.execute(() -> {
                APRandomizer.itemManager().ifPresent(value -> {
                    for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                        value.catchUpPlayer(player);
                    }
                });
            }));

            // ensure server is synced
            APRandomizer.goalManager().ifPresent(goalManager -> goalManager.updateGoal(true));

        } else if (event.getResult() == ConnectionResult.InvalidPassword) {
            Utils.sendMessageToAll("Connection Failed: Invalid Password.");
        } else if (event.getResult() == ConnectionResult.IncompatibleVersion) {
            Utils.sendMessageToAll("Connection Failed: Server Sent Incompatible Version Error.");
        } else if (event.getResult() == ConnectionResult.InvalidSlot) {
            Utils.sendMessageToAll("Connection Failed: Invalid Slot Name. (this is case sensitive)");
        } else if (event.getResult() == ConnectionResult.SlotAlreadyTaken) {
            Utils.sendMessageToAll("Connection Failed: Room Slot has all ready been taken.");
        }
    }
}
