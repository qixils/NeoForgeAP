package gg.archipelago.aprandomizer.ap.events;

import io.github.archipelagomw.events.ArchipelagoEventListener;
import io.github.archipelagomw.events.ConnectionResultEvent;
import io.github.archipelagomw.network.ConnectionResult;
import gg.archipelago.aprandomizer.SlotData;
import gg.archipelago.aprandomizer.ap.APClient;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import gg.archipelago.aprandomizer.managers.GoalManager;
import gg.archipelago.aprandomizer.managers.advancementmanager.AdvancementManager;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.HolderLookup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConnectResult {

    private static final Logger LOGGER = LogManager.getLogger();
    private final APClient APClient;
    private final HolderLookup.Provider registries;
    private final AdvancementManager advancementManager;
    private final GoalManager goalManager;

    public ConnectResult(APClient APClient, HolderLookup.Provider registries, AdvancementManager advancementManager, GoalManager goalManager) {
        this.APClient = APClient;
        this.registries = registries;
        this.advancementManager = advancementManager;
        this.goalManager = goalManager;
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
                APClient.setDeathLinkEnabled(true);
            }

            advancementManager.setCheckedAdvancements(new LongOpenHashSet(APClient.getLocationManager().getCheckedLocations()));

            // ensure server is synced
            goalManager.updateGoal(true);
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
