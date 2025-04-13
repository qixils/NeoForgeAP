package gg.archipelago.aprandomizer.common.events;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.managers.itemmanager.ItemManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.CommandEvent;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.ArrayList;

@EventBusSubscriber
public class onCommand {

    private static final ArrayList<String> allowedCommands = new ArrayList<>() {{
        add("connect");
        add("sync");
        add("start");
        add("stop");
        add("kick");
        add("ban");
        add("ban-ip");
        add("pardon");
        add("pardon-ip");
        add("whitelist");
        add("me");
        add("say");
    }};

    @SubscribeEvent
    static void onPlayerLoginEvent(CommandEvent event) {
        if (!APRandomizer.isRace()) {
            return;
        }
        CommandSourceStack source = event.getParseResults().getContext().getSource();
        String command = event.getParseResults().getReader().getRead();
        for (String allowedCommand : allowedCommands)
            if (command.startsWith(allowedCommand) || command.startsWith("/" + allowedCommand))
                return;

        event.setCanceled(true);
        source.sendFailure(Component.literal("Non-essential commands are disabled in race mode."));
    }

    @EventBusSubscriber
    public static class onDimensionChange {

        @SubscribeEvent
        public static void onChange1(PlayerEvent.PlayerChangedDimensionEvent event) {
            if(!(event.getEntity() instanceof ServerPlayer player)) return;
            ItemManager.refreshCompasses(player);
        }

        @SubscribeEvent
        public static void onChange1(PlayerEvent.PlayerRespawnEvent event) {
            if(!(event.getEntity() instanceof ServerPlayer player)) return;
            ItemManager.refreshCompasses(player);
        }
    }
}
