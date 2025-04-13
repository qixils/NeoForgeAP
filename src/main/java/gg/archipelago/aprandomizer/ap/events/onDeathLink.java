package gg.archipelago.aprandomizer.ap.events;

import dev.koifysh.archipelago.events.ArchipelagoEventListener;
import dev.koifysh.archipelago.events.DeathLinkEvent;
import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import gg.archipelago.aprandomizer.common.events.onDeath;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;

public class onDeathLink {

    @ArchipelagoEventListener
    public static void onDeath(DeathLinkEvent event) {
        if (!APRandomizer.getAP().getSlotData().deathlink)
            return;

        GameRules.BooleanValue showDeathMessages = APRandomizer.getServer().getGameRules().getRule(GameRules.RULE_SHOWDEATHMESSAGES);
        boolean showDeaths = showDeathMessages.get();
        if(showDeaths) {
            String cause = event.cause;
            if(cause != null && !cause.isBlank())
                Utils.sendMessageToAll(event.cause);
            else
                Utils.sendMessageToAll("This Death brought to you by "+ event.source);
        }
        showDeathMessages.set(false, APRandomizer.getServer());
        onDeath.sendDeathLink = false;
        for (ServerPlayer player : APRandomizer.getServer().getPlayerList().getPlayers()) {
            player.kill();
        }
        onDeath.sendDeathLink = true;
        showDeathMessages.set(showDeaths,APRandomizer.getServer());

    }
}
