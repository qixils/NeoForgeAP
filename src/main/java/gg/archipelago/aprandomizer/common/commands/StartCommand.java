package gg.archipelago.aprandomizer.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.archipelagomw.ClientStatus;
import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.SlotData;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import gg.archipelago.aprandomizer.managers.itemmanager.ItemManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber
public class StartCommand {
    //build our command structure and submit it
    public static void Register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("start") //base slash command is "start"
                        .executes(context -> Start(context, false))
        );

        dispatcher.register(
                Commands.literal("forcestart") //base slash command is "start"
                        .executes(context -> Start(context, true))
        );
    }

    private static int Start(CommandContext<CommandSourceStack> commandSourceCommandContext, boolean force) {
        if (!APRandomizer.isConnected() && !force) {
            commandSourceCommandContext.getSource().sendFailure(Component.literal("Please connect to the Archipelago server before starting."));
            return 1;
        }
        if (!APRandomizer.isJailPlayers()) {
            commandSourceCommandContext.getSource().sendFailure(Component.literal("The game has already started! what are you doing? START PLAYING!"));
            return 1;
        }
        Utils.sendMessageToAll("GO!");
        if (APRandomizer.isConnected()) {
            assert APRandomizer.getAP() != null; // safe because isConnected verifies this
            APRandomizer.getAP().setGameState(ClientStatus.CLIENT_PLAYING);
        }
        APRandomizer.setJailPlayers(false);
        MinecraftServer server = APRandomizer.getServer();
        if (server == null) return 0;
        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
        if (overworld == null) return 0;
        ItemManager itemManager = APRandomizer.getItemManager();
        if (itemManager == null) return 0;
        BlockPos spawn = overworld.getRespawnData().pos();
        StructureTemplate jailStruct = overworld.getStructureManager().get(Identifier.fromNamespaceAndPath(APRandomizer.MODID, "spawnjail")).orElseThrow();
        BlockPos jailPos = new BlockPos(spawn.getX() + 5, 300, spawn.getZ() + 5);
        for (BlockPos blockPos : BlockPos.betweenClosed(jailPos, jailPos.offset(jailStruct.getSize()))) {
            overworld.setBlock(blockPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
        }
        overworld.getGameRules().set(GameRules.ADVANCE_TIME, true, server);
        overworld.getGameRules().set(GameRules.ADVANCE_WEATHER, true, server);
        overworld.getGameRules().set(GameRules.FIRE_SPREAD_RADIUS_AROUND_PLAYER, 128, server);
        overworld.getGameRules().set(GameRules.RANDOM_TICK_SPEED, 3, server);
        overworld.getGameRules().set(GameRules.SPAWN_PATROLS, true, server);
        overworld.getGameRules().set(GameRules.SPAWN_WANDERING_TRADERS, true, server);
        overworld.getGameRules().set(GameRules.MOB_GRIEFING, true, server);
        overworld.getGameRules().set(GameRules.SPAWN_MOBS, true, server);
        overworld.getGameRules().set(GameRules.IMMEDIATE_RESPAWN, true, server);
        overworld.getGameRules().set(GameRules.MOB_DROPS, true, server);
        overworld.getGameRules().set(GameRules.ENTITY_DROPS, true, server);
        server.execute(() -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                player.getFoodData().eat(20, 20);
                player.setHealth(20);
                player.getInventory().clearContent();
                player.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));
                player.teleportTo(spawn.getX(), spawn.getY(), spawn.getZ());

                if (APRandomizer.isConnected()) {
                    assert APRandomizer.getAP() != null; // safe because isConnected verifies this
                    SlotData slotData = APRandomizer.getAP().getSlotData();
                    if (slotData != null) {
                        for (ItemStack iStack : slotData.startingItemStacks) {
                            Utils.giveItemToPlayer(player, iStack.copy());
                        }
                    }
                }
            }
            itemManager.catchUp(server);
        });
        return 1;
    }
    //wait for register commands event then register us as a command.
    @SubscribeEvent
    static void onRegisterCommandsEvent(RegisterCommandsEvent event) {
        // I'm assuming this fires after the server loads lol
        StartCommand.Register(event.getDispatcher());
    }
}
