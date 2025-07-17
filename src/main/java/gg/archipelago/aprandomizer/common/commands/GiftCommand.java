package gg.archipelago.aprandomizer.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.koifysh.archipelago.network.server.RoomInfoPacket;
import dev.koifysh.archipelago.parts.NetworkPlayer;
import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.gifting.GiftHandler;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber
public final class GiftCommand {

    private GiftCommand() {
    }

    // Register the command
    public static void Register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("gift")
                        .then(
                                Commands.argument("recipient", StringArgumentType.string())
                                        .suggests(new APPlayersSuggestionProvider())
                                        .executes(GiftCommand::gift)
                        )
        );
    }

    public static int gift(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        if (!APRandomizer.isConnected()) {
            context.getSource().sendFailure(Component.literal("Please connect to the Archipelago server before gifting."));
            return 1;
        }
        if (APRandomizer.isJailPlayers()) {
            context.getSource().sendFailure(Component.literal("You cannot gift items before the game starts."));
            return 1;
        }

        var server = APRandomizer.getServer();
        if (server == null) return 0;
        var overworld = server.getLevel(Level.OVERWORLD);
        if (overworld == null) return 0;
        var apClient = APRandomizer.getAP();
        if (apClient == null) {
            context.getSource().sendFailure(Component.literal("Archipelago client is not initialized."));
            return 0;
        }
        var player = context.getSource().getPlayerOrException();
        var heldItem = player.getMainHandItem();
        if (heldItem.isEmpty()) {
            context.getSource().sendFailure(Component.literal("You must hold an item in your hand to gift it."));
            return 0;
        }

        String recipientName = StringArgumentType.getString(context, "recipient");
        Optional<NetworkPlayer> recipientOpt = apClient.getRoomInfo().networkPlayers.stream()
                .filter(p -> p != null && p.name.equals(recipientName))
                .findFirst();
        if (recipientOpt.isEmpty()) {
            context.getSource().sendFailure(Component.literal("No player found with the name: " + recipientName));
            return 0;
        }
        NetworkPlayer recipient = recipientOpt.get();

        GiftHandler giftHandler = APRandomizer.getGiftHandler();
        if (giftHandler == null) {
            context.getSource().sendFailure(Component.literal("Gifting is not enabled"));
            return 0;
        }

        RecipeManager recipeManager = context.getSource().getServer().getRecipeManager();
        context.getSource().sendSystemMessage(
                Component.literal("Item traits : " + giftHandler.getGiftItem(
                        context.getSource()
                                .registryAccess(),heldItem,
                        recipeManager))
        );
        giftHandler.giftItem(context.getSource().registryAccess(),heldItem,recipient, recipeManager).whenComplete((giftingError, v) -> {
            if (giftingError != null) {
                context.getSource().sendFailure(Component.literal("Error while gifting item: " + giftingError));
            } else {
                player.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            }
        });

        return 1;
    }

    @SubscribeEvent
    public static void onRegisterCommandsEvent(RegisterCommandsEvent event) {
        Register(event.getDispatcher());
    }

    public static class APPlayersSuggestionProvider implements SuggestionProvider<CommandSourceStack> {
        @Override
        public CompletableFuture<Suggestions> getSuggestions(
                CommandContext<CommandSourceStack> context,
                SuggestionsBuilder builder) throws CommandSyntaxException {
            String recipientName = "";
            try {
                recipientName = context.getArgument("recipient", String.class);
            } catch (IllegalArgumentException ignored) {
            }

            if (APRandomizer.getAP() == null) {
                return Suggestions.empty();
            }
            String finalRecipientName = recipientName;
            RoomInfoPacket roomInfo = APRandomizer.getAP().getRoomInfo();
            if (roomInfo == null || roomInfo.networkPlayers == null) {
                return Suggestions.empty();
            }
            roomInfo.networkPlayers.stream()
                    .filter(player -> player.slot != 0)
                    .forEach(player -> {
                        if (finalRecipientName.isBlank() || player.name.startsWith(finalRecipientName)) {
                            builder.suggest(player.name);
                        }
                    });
            return builder.buildFuture();
        }
    }
}
