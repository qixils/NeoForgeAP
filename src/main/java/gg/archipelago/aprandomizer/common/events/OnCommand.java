package gg.archipelago.aprandomizer.common.events;

import gg.archipelago.aprandomizer.APRandomizer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.CommandEvent;

import java.util.List;

@EventBusSubscriber
public class OnCommand {

    private static final List<String> ALLOWED_COMMANDS = List.of(
            "connect",
            "sync",
            "start",
            "stop",
            "kick",
            "ban",
            "ban-ip",
            "pardon",
            "pardon-ip",
            "whitelist",
            "me",
            "say"
    );

    @SubscribeEvent
    static void onPlayerLoginEvent(CommandEvent event) {
        if (!APRandomizer.isRace()) {
            return;
        }
        CommandSourceStack source = event.getParseResults().getContext().getSource();
        String command = event.getParseResults().getReader().getRead();
        for (String allowedCommand : ALLOWED_COMMANDS)
            if (command.startsWith(allowedCommand) || command.startsWith("/" + allowedCommand))
                return;

        event.setCanceled(true);
        source.sendFailure(Component.literal("Non-essential commands are disabled in race mode."));
    }

//    @EventBusSubscriber
//    public static class onDimensionChange {
//
//        @SubscribeEvent
//        public static void onChange1(PlayerEvent.PlayerChangedDimensionEvent event) {
//            if(!(event.getEntity() instanceof ServerPlayer player)) return;
//            ItemManager.refreshCompasses(player);
//        }
//
//        @SubscribeEvent
//        public static void onChange1(PlayerEvent.PlayerRespawnEvent event) {
//            if(!(event.getEntity() instanceof ServerPlayer player)) return;
//            ItemManager.refreshCompasses(player);
//        }
//    }
}
