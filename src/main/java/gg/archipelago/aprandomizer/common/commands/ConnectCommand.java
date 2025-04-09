package gg.archipelago.aprandomizer.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import gg.archipelago.aprandomizer.APClient;
import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.APStorage.APMCData;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.net.URISyntaxException;

@EventBusSubscriber
public class ConnectCommand {

    //build our command structure and submit it
    public static void Register(CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(
                Commands.literal("connect") //base slash command is "connect"
                        //take the first argument as a string and name it "Address"
                        .then(Commands.argument("Address", StringArgumentType.string())
                                .executes(context -> connectToAPServer(
                                        context,
                                        StringArgumentType.getString(context, "Address"),
                                        -1,
                                        null
                                ))
                                .then(Commands.argument("Port", IntegerArgumentType.integer())
                                        .executes(context -> connectToAPServer(
                                                context,
                                                StringArgumentType.getString(context, "Address"),
                                                IntegerArgumentType.getInteger(context, "Port"),
                                                null
                                        ))
                                        .then(Commands.argument("Password", StringArgumentType.string())
                                                .executes(context -> connectToAPServer(
                                                        context,
                                                        StringArgumentType.getString(context, "Address"),
                                                        IntegerArgumentType.getInteger(context, "Port"),
                                                        StringArgumentType.getString(context, "Password")
                                                ))
                                        )
                                )
                        )
        );

    }

    private static int connectToAPServer(CommandContext<CommandSourceStack> commandContext, String hostname, int port, String password) {
        APMCData data = APRandomizer.getApmcData();
        if (data.state == APMCData.State.VALID) {

            APClient apClient = APRandomizer.getAP();
            if (apClient == null) return 0;

            apClient.setName(data.player_name);
            apClient.setPassword(password);
            String address = (port == -1) ? hostname : hostname.concat(":" + port);
            Utils.sendMessageToAll("Connecting to Archipelago server at " + address);
            try {
                apClient.connect(address);
            } catch (URISyntaxException e) {
                Utils.sendMessageToAll("Malformed address " + address);
            }
        } else if (data.state == APMCData.State.MISSING)
            Utils.sendMessageToAll("no .apmc file found. please stop the server,  place .apmc file in './APData/', delete the world folder, then relaunch the server.");
        else if (data.state == APMCData.State.INVALID_VERSION)
            Utils.sendMessageToAll("APMC data file wrong version.");
        else if (data.state == APMCData.State.INVALID_SEED)
            Utils.sendMessageToAll("Current Minecraft world has been used for a previous game. please stop server, delete the world and relaunch the server.");

        return 1;
    }

    //wait for register commands event then register ourself as a command.
    @SubscribeEvent
    static void onRegisterCommandsEvent(RegisterCommandsEvent event) {
        ConnectCommand.Register(event.getDispatcher());
    }
}
