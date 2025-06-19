package gg.archipelago.aprandomizer.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import gg.archipelago.aprandomizer.ap.APClient;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber
public class DisconnectCommand {
    //build our command structure and submit it
    public static void Register(CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(
                Commands.literal("disconnect") //base slash command is "connect"
                        //take the first argument as a string and name it "Address"
                        .executes(DisconnectCommand::disconnect)
        );

    }

    private static int disconnect(CommandContext<CommandSourceStack> commandContext) {
        APClient.client.disconnect();
        //Utils.sendMessageToAll("Disconnected.");
        return 1;
    }

    //wait for register commands event then register us as a command.
    @SubscribeEvent
    static void onRegisterCommandsEvent(RegisterCommandsEvent event) {
        DisconnectCommand.Register(event.getDispatcher());
    }
}
