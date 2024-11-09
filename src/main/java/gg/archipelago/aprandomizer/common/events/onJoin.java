package gg.archipelago.aprandomizer.common.events;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.APStorage.APMCData;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import gg.archipelago.aprandomizer.managers.GoalManager;
import gg.archipelago.aprandomizer.managers.advancementmanager.AdvancementManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.GameType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.Set;

@EventBusSubscriber
public class onJoin {
    @SubscribeEvent
    static void onPlayerLoginEvent(PlayerEvent.PlayerLoggedInEvent event) {

        ServerPlayer player = (ServerPlayer) event.getEntity();
        if (APRandomizer.isRace()) {
            if (player.gameMode.getGameModeForPlayer() != GameType.SURVIVAL) {
                player.setGameMode(GameType.SURVIVAL);
            }
        }

        APMCData data = APRandomizer.getApmcData();
        if (data.state == APMCData.State.MISSING)
            Utils.sendMessageToAll("No APMC file found, please only start the server via the APMC file.");
        else if (data.state == APMCData.State.INVALID_VERSION)
            Utils.sendMessageToAll("This Seed was generated using an incompatible randomizer version.");
        else if (data.state == APMCData.State.INVALID_SEED)
            Utils.sendMessageToAll("Invalid Minecraft World please only start the Minecraft server via the correct APMC file");

        APRandomizer.advancementManager().ifPresent(AdvancementManager::syncAllAdvancements);
        APRandomizer.recipeManager().ifPresent(value -> {
            Set<RecipeHolder<?>> restricted = value.getRestrictedRecipes();
            Set<RecipeHolder<?>> granted = value.getGrantedRecipes();
            player.resetRecipes(restricted);
            player.awardRecipes(granted);
        });

        APRandomizer.goalManager().ifPresent(GoalManager::updateInfoBar);
        APRandomizer.itemManager().ifPresent(value -> value.catchUpPlayer(player));

        if (APRandomizer.isJailPlayers()) {
            BlockPos jail = APRandomizer.getJailPosition();
            player.teleportTo(jail.getX(), jail.getY(), jail.getZ());
            if (player.gameMode.getGameModeForPlayer() != GameType.SURVIVAL) {
                player.setGameMode(GameType.SURVIVAL);
            }
        }
    }
}
