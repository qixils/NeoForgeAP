package gg.archipelago.aprandomizer.apevents;

import dev.koifysh.archipelago.events.ArchipelagoEventListener;
import dev.koifysh.archipelago.events.CheckedLocationsEvent;

public class LocationChecked {

    @ArchipelagoEventListener
    public void onLocationChecked(CheckedLocationsEvent event) {
//        event.checkedLocations.forEach(location -> APRandomizer.getAdvancementManager().addAdvancement(location));
    }
}
