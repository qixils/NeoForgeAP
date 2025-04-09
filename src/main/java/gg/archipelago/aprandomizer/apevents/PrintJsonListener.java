package gg.archipelago.aprandomizer.apevents;

import dev.koifysh.archipelago.events.ArchipelagoEventListener;
import dev.koifysh.archipelago.events.PrintJSONEvent;
import gg.archipelago.aprandomizer.APClient;
import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.common.Utils.Utils;

public class PrintJsonListener {

    @ArchipelagoEventListener
    public void onPrintJson(PrintJSONEvent event) {
        // Don't print chat messages originating from ourselves.
        APClient apClient = APRandomizer.getAP();
        if (apClient != null && event.type.equals("Chat") && event.player != apClient.getSlot())
            return;

        Utils.sendFancyMessageToAll(event.apPrint);
    }
}
