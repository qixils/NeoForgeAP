package gg.archipelago.aprandomizer.ap.events;

import dev.koifysh.archipelago.events.ArchipelagoEventListener;
import dev.koifysh.archipelago.events.CheckedLocationsEvent;
import gg.archipelago.aprandomizer.managers.advancementmanager.AdvancementManager;

public class LocationChecked {
    static AdvancementManager advancementManager;
    @ArchipelagoEventListener
    public void onLocationChecked(CheckedLocationsEvent event) {
        event.checkedLocations.forEach(advancementManager::addAdvancement);

    }
}
