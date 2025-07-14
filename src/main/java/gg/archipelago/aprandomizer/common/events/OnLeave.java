package gg.archipelago.aprandomizer.common.events;

import gg.archipelago.aprandomizer.APRandomizer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber
public class OnLeave {

    @SubscribeEvent
    static void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        if (APRandomizer.getServer() != null)
            if (APRandomizer.getServer()
                    .getPlayerList().getPlayers()
                    .stream().allMatch(e -> e.equals(event.getEntity()))) {
                APRandomizer.getGiftHandler().stopReception();
            }
    }
}
