package gg.archipelago.aprandomizer.common.events;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.ap.storage.APMCData;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import gg.archipelago.aprandomizer.managers.GoalManager;
import gg.archipelago.aprandomizer.managers.advancementmanager.AdvancementManager;
import gg.archipelago.aprandomizer.managers.itemmanager.ItemManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber
public class OnJoin {
    static GoalManager goalManager;
    static ItemManager itemManager;
    static AdvancementManager advancementManager;
    @SubscribeEvent
    static void onPlayerLoginEvent(PlayerEvent.PlayerLoggedInEvent event) {

        ServerPlayer player = (ServerPlayer) event.getEntity();
        if (APRandomizer.isRace())
            player.setGameMode(GameType.SURVIVAL);

        APMCData data = APRandomizer.getApmcData();
        if (data.state == APMCData.State.MISSING) {
            Utils.sendMessageToAll("No APMC file found, please only start the server via the APMC file.");
            return;
        }
        else if (data.state == APMCData.State.INVALID_VERSION) {
            Utils.sendMessageToAll("This Seed was generated using an incompatible randomizer version.");
            return;
        }
        else if (data.state == APMCData.State.INVALID_SEED) {
            Utils.sendMessageToAll("Supplied APMC file does not match world loaded. something went very wrong here.");
            return;
        }
        advancementManager.syncAllAdvancements();
        goalManager.updateInfoBar();
        itemManager.catchUpPlayer(player);

        if (APRandomizer.isJailPlayers()) {
            BlockPos jail = APRandomizer.getJailPosition();
            player.teleportTo(jail.getX(), jail.getY(), jail.getZ());
            player.setGameMode(GameType.SURVIVAL);
        }
    }
}
