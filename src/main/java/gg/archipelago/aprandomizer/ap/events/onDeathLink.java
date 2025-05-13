package gg.archipelago.aprandomizer.ap.events;

import dev.koifysh.archipelago.events.ArchipelagoEventListener;
import dev.koifysh.archipelago.events.DeathLinkEvent;
import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.SlotData;
import gg.archipelago.aprandomizer.ap.APClient;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import gg.archipelago.aprandomizer.common.events.OnDeath;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;

public class onDeathLink {

    @ArchipelagoEventListener
    public void onDeath(DeathLinkEvent event) {
        APClient apClient = APRandomizer.getAP();
        if (apClient == null) return;

        SlotData slotData = apClient.getSlotData();
        if (slotData == null || !slotData.deathlink) return;

        MinecraftServer server = APRandomizer.getServer();
        if (server == null) return;

        GameRules.BooleanValue showDeathMessages = server.getGameRules().getRule(GameRules.RULE_SHOWDEATHMESSAGES);
        boolean showDeaths = showDeathMessages.get();
        if (showDeaths) {
            String cause = event.cause;
            if (cause != null && !cause.isBlank())
                Utils.sendMessageToAll(event.cause);
            else
                Utils.sendMessageToAll("This Death brought to you by " + event.source);
        }
        showDeathMessages.set(false, server);
        OnDeath.sendDeathLink = false;
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            player.kill(player.serverLevel());
        }
        OnDeath.sendDeathLink = true;
        showDeathMessages.set(showDeaths, server);
    }
}
