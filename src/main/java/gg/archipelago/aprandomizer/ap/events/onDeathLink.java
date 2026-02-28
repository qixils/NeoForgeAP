package gg.archipelago.aprandomizer.ap.events;

import io.github.archipelagomw.events.ArchipelagoEventListener;
import io.github.archipelagomw.events.DeathLinkEvent;
import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.SlotData;
import gg.archipelago.aprandomizer.ap.APClient;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import gg.archipelago.aprandomizer.common.events.OnDeath;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRules;

public class onDeathLink {

    @ArchipelagoEventListener
    public void onDeath(DeathLinkEvent event) {
        APClient apClient = APRandomizer.getAP();
        if (apClient == null) return;

        SlotData slotData = apClient.getSlotData();
        if (slotData == null || !slotData.deathlink) return;

        MinecraftServer server = APRandomizer.getServer();
        if (server == null) return;

        Boolean showDeathMessages = server.getLevel(Level.OVERWORLD).getGameRules().get(GameRules.SHOW_DEATH_MESSAGES);
        boolean showDeaths = showDeathMessages;
        if (showDeaths) {
            String cause = event.cause;
            if (cause != null && !cause.isBlank())
                Utils.sendMessageToAll(event.cause);
            else
                Utils.sendMessageToAll("This Death brought to you by " + event.source);
        }
        server.getLevel(Level.OVERWORLD).getGameRules().set(GameRules.SHOW_DEATH_MESSAGES, false, server);
        OnDeath.sendDeathLink = false;
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            player.kill(player.level());
        }
        OnDeath.sendDeathLink = true;
        server.getLevel(Level.OVERWORLD).getGameRules().set(GameRules.SHOW_DEATH_MESSAGES, showDeaths, server);
    }
}
