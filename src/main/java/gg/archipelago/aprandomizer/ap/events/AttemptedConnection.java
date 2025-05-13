package gg.archipelago.aprandomizer.ap.events;

import dev.koifysh.archipelago.events.ArchipelagoEventListener;
import dev.koifysh.archipelago.events.ConnectionAttemptEvent;
import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.SlotData;
import gg.archipelago.aprandomizer.ap.storage.APMCData;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttemptedConnection {

    private static final Logger log = LoggerFactory.getLogger(AttemptedConnection.class);

    @ArchipelagoEventListener
    public void onAttemptConnect(ConnectionAttemptEvent event) {
        try {
            SlotData temp = event.getSlotData(SlotData.class);
            APMCData data = APRandomizer.getApmcData();
            if (!event.getSeedName().equals(data.seed_name)) {
                Utils.sendMessageToAll("Failed to Connect to Archipelago Server: Wrong .apmc file found. please stop the server, use the correct .apmc file, delete the world folder, then relaunch the server.");
                event.setCanceled(true);
            }
            if (!APRandomizer.getValidVersions().contains(temp.getClient_version())) {
                event.setCanceled(true);
                Utils.sendMessageToAll("Game was generated with an for an incompatible version of the Minecraft Randomizer.");
            }
        } catch (Exception e) {
            log.warn("Failed to connect", e);
        }
    }
}
