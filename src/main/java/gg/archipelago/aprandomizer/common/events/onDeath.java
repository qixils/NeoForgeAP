package gg.archipelago.aprandomizer.common.events;

import dev.koifysh.archipelago.helper.DeathLink;
import gg.archipelago.aprandomizer.APRandomizer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber
public class onDeath {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public static boolean sendDeathLink = true;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    static void onDeathEvent(LivingDeathEvent event) {
        if(!APRandomizer.isConnected())
            return;
        //only trigger on player death
        if (!(event.getEntity() instanceof ServerPlayer player))
            return;
        if(!APRandomizer.getAP().getSlotData().deathlink)
            return;
        //dont send deathlink if the cause of this death was a deathlink
        if (!sendDeathLink)
            return;

        // TODO: these args are backwards on kono master
        DeathLink.SendDeathLink(event.getSource().getLocalizedDeathMessage(player).getString(), player.getDisplayName().getString());

        MinecraftServer server = APRandomizer.getServer();
        GameRules.BooleanValue deathMessages = server.getGameRules().getRule(GameRules.RULE_SHOWDEATHMESSAGES);
        boolean death = deathMessages.get();
        deathMessages.set(false, server);
        sendDeathLink = false;
        for (ServerPlayer serverPlayer : APRandomizer.getServer().getPlayerList().getPlayers()) {
            if (serverPlayer != player) {
                serverPlayer.kill();
            }
        }
        deathMessages.set(death, server);
        sendDeathLink = true;
    }
}
