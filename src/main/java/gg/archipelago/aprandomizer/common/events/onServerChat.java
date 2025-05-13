package gg.archipelago.aprandomizer.common.events;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.ap.APClient;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ServerChatEvent;

import java.util.Objects;

@EventBusSubscriber
public class onServerChat {
    @SubscribeEvent
    static void onServerChatEvent(ServerChatEvent event) {
        APClient apClient = APRandomizer.getAP();
        if (apClient == null || !apClient.isConnected()) return;

        ServerPlayer player = event.getPlayer();

        String message = event.getMessage().getString();

        if (message.startsWith("!"))
            apClient.sendChat(message);
        else
            apClient.sendChat("(" + Objects.requireNonNullElseGet(player.getDisplayName(), player::getName).getString() + ") " + message);
    }
}
