package gg.archipelago.aprandomizer.common.events;

import gg.archipelago.aprandomizer.APRandomizer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.CommandEvent;

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
}
