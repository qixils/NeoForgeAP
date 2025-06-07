package gg.archipelago.aprandomizer.ap.events;

import dev.koifysh.archipelago.events.ArchipelagoEventListener;
import dev.koifysh.archipelago.events.CheckedLocationsEvent;
import gg.archipelago.aprandomizer.managers.advancementmanager.AdvancementManager;

public class LocationChecked {
    private final AdvancementManager advancementManager;

    public LocationChecked(AdvancementManager advancementManager) {
        this.advancementManager = advancementManager;
    }

    @ArchipelagoEventListener
    public void onLocationChecked(CheckedLocationsEvent event) {
        event.checkedLocations.forEach(advancementManager::addAdvancement);

    }
}
