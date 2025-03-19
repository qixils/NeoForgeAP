package gg.archipelago.aprandomizer.common.events;

import gg.archipelago.aprandomizer.APRandomizer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ServerChatEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@EventBusSubscriber
public class onServerChat {

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    static void onServerChatEvent(ServerChatEvent event) {
        if(!APRandomizer.getAP().isConnected())
            return;
        ServerPlayer player = event.getPlayer();

        String message = event.getMessage().getString();

        if (message.startsWith("!"))
            APRandomizer.getAP().sendChat(message);
        else
            APRandomizer.getAP().sendChat("(" + player.getDisplayName().getString() + ") " + message);
    }
}
