package gg.archipelago.aprandomizer.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import dev.koifysh.archipelago.network.client.BouncePacket;
import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.ap.APClient;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber
public class BounceCommand {

    //build our command structure and submit it
    public static void Register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext pContext) {

        dispatcher.register(Commands.literal("bounce") //base slash command is "connect"
                // first make sure its NOT a dedicated server (aka single player or hosted via in game client, OR user has an op level of 1)
                .requires((CommandSource) -> (!CommandSource.getServer().isDedicatedServer() || CommandSource.hasPermission(1)))
                //take the first argument as a string and name it "Address"
                .then(Commands.argument("entity", ResourceArgument.resource(pContext, Registries.ENTITY_TYPE))
                        .suggests(SuggestionProviders.cast(SuggestionProviders.SUMMONABLE_ENTITIES))
                        .executes(context ->
                                bounceEntity(context.getSource(),
                                        ResourceArgument.getSummonableEntityType(context, "entity"),
                                        new CompoundTag()
                                )
                        )
                        .then(Commands.argument("nbt", CompoundTagArgument.compoundTag())
                                .executes(context ->
                                        bounceEntity(
                                                context.getSource(),
                                                ResourceArgument.getSummonableEntityType(context, "entity"),
                                                CompoundTagArgument.getCompoundTag(context, "nbt")
                                        )
                                )
                        )
                )


        );

    }

    private static int bounceEntity(CommandSourceStack commandSource, Holder.Reference<EntityType<?>> entity, CompoundTag nbt) {
        APClient apClient = APRandomizer.getAP();
        if (apClient == null) return 0;

        BouncePacket packet = new BouncePacket();
        packet.tags = new String[]{"MC35"};
        packet.setData(new HashMap<>(Map.of(
                "enemy", entity.toString(),
                "source", apClient.getSlot(),
                "nbt", nbt.toString())));
        apClient.sendBounce(packet);
        return 1;
    }

    //wait for register commands event then register us as a command.
    @SubscribeEvent
    static void onRegisterCommandsEvent(RegisterCommandsEvent event) {
        BounceCommand.Register(event.getDispatcher(), event.getBuildContext());
    }
}
