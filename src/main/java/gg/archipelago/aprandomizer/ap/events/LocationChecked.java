package gg.archipelago.aprandomizer.ap.events;

import gg.archipelago.aprandomizer.APRandomizer;
import dev.koifysh.archipelago.events.ArchipelagoEventListener;
import dev.koifysh.archipelago.events.CheckedLocationsEvent;

public class LocationChecked {

    @ArchipelagoEventListener
    public void onLocationChecked(CheckedLocationsEvent event) {
        APRandomizer.advancementManager().ifPresent(advancementManager ->
                event.checkedLocations.forEach(advancementManager::addAdvancement));
    }
}
