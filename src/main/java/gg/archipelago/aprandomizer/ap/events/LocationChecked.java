package gg.archipelago.aprandomizer.ap.events;

import dev.koifysh.archipelago.events.ArchipelagoEventListener;
import dev.koifysh.archipelago.events.CheckedLocationsEvent;
import gg.archipelago.aprandomizer.APRandomizer;

public class LocationChecked {

    @ArchipelagoEventListener
    public void onLocationChecked(CheckedLocationsEvent event) {
        APRandomizer.advancementManager().ifPresent(advancementManager ->
                event.checkedLocations.forEach(advancementManager::addAdvancement));
    }
}
