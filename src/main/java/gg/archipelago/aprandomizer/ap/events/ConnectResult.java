package gg.archipelago.aprandomizer.ap.events;

import dev.koifysh.archipelago.events.ArchipelagoEventListener;
import dev.koifysh.archipelago.events.ConnectionResultEvent;
import dev.koifysh.archipelago.helper.DeathLink;
import dev.koifysh.archipelago.network.ConnectionResult;
import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.SlotData;
import gg.archipelago.aprandomizer.ap.APClient;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConnectResult {

    private static final Logger LOGGER = LogManager.getLogger();
    final APClient APClient;
    private final HolderLookup.Provider registries;

    public ConnectResult(APClient APClient, HolderLookup.Provider registries) {
        this.APClient = APClient;
        this.registries = registries;
    }

    @ArchipelagoEventListener
    public void onConnectResult(ConnectionResultEvent event) {
        if (event.getResult() == ConnectionResult.Success) {
            Utils.sendMessageToAll("Successfully connected to " + APClient.getConnectedAddress());
            try {
                APClient.slotData = event.getSlotData(SlotData.class);
                APClient.slotData.parseStartingItems(registries);
            } catch (Exception e) {
                if (APClient.slotData != null) {
                    Utils.sendMessageToAll("Invalid starting item section, check logs for more details.");
                    LOGGER.warn("Invalid staring items json string: {}", APClient.slotData.startingItems, e);
                } else {
                    Utils.sendMessageToAll("Invalid slot data, check logs for more details.");
                    LOGGER.warn("Invalid slot data", e);
                }
            }

            if (APClient.slotData.MC35) {
                Utils.sendMessageToAll("Welcome to Minecraft 35.");
                APClient.addTag("MC35");
            }
            if (APClient.slotData.deathlink) {
                Utils.sendMessageToAll("Welcome to Death Link.");
                DeathLink.setDeathLinkEnabled(true);
            }

            APRandomizer.advancementManager().ifPresent(value -> value.setCheckedAdvancements(new LongOpenHashSet(APClient.getLocationManager().getCheckedLocations())));

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
