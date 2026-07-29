package gg.archipelago.aprandomizer.ap.events;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.ap.APClient;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import io.github.archipelagomw.Print.APPrintJsonType;
import io.github.archipelagomw.events.ArchipelagoEventListener;
import io.github.archipelagomw.events.PrintJSONEvent;

public class PrintJsonListener {

    @ArchipelagoEventListener
    public void onPrintJson(PrintJSONEvent event) {
        // Don't print chat messages originating from ourselves.
        APClient apClient = APRandomizer.getAP();
        if (apClient != null && APPrintJsonType.Chat == event.type && event.apPrint.slot == apClient.getSlot())
            return;

        Utils.sendFancyMessageToAll(event.apPrint);
    }
}