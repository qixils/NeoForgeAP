package gg.archipelago.aprandomizer.apevents;

import dev.koifysh.archipelago.events.ArchipelagoEventListener;
import dev.koifysh.archipelago.events.DeathLinkEvent;
import gg.archipelago.aprandomizer.APClient;
import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.SlotData;
import gg.archipelago.aprandomizer.common.DeathLinkDamage;
import gg.archipelago.aprandomizer.common.Utils.Utils;
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
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            player.hurtServer(player.serverLevel(), new DeathLinkDamage(), Float.MAX_VALUE);
        }
        showDeathMessages.set(showDeaths, server);
    }
}
