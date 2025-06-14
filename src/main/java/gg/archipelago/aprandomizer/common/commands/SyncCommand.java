package gg.archipelago.aprandomizer.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.ap.APClient;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber
public class SyncCommand {
    //build our command structure and submit it
    public static void Register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("sync") //base slash command is "sync"
                        .executes(SyncCommand::sync)
        );
    }

    private static int sync(CommandContext<CommandSourceStack> source) {
        APClient apClient = APRandomizer.getAP();
        if (apClient == null || !apClient.isConnected()) return 0;

        Utils.sendMessageToAll("Re-syncing progress with Archipelago server.");
        apClient.sync();
        return 1;
    }

    //wait for register commands event then register us as a command.
    @SubscribeEvent
    static void onRegisterCommandsEvent(RegisterCommandsEvent event) {
        SyncCommand.Register(event.getDispatcher());
    }
}
