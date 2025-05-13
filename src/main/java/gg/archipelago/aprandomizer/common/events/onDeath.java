package gg.archipelago.aprandomizer.common.events;

import dev.koifysh.archipelago.helper.DeathLink;
import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.SlotData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

import java.util.Objects;

@EventBusSubscriber
public class onDeath {

    public static boolean sendDeathLink = true;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    static void onDeathEvent(LivingDeathEvent event) {
        if (!APRandomizer.isConnected())
            return;
        assert APRandomizer.getAP() != null;

        //only trigger on player death
        if (!(event.getEntity() instanceof ServerPlayer player))
            return;
        SlotData slotData = APRandomizer.getAP().getSlotData();
        if (slotData == null || !slotData.deathlink)
            return;
        //dont send deathlink if the cause of this death was a deathlink
        if (!sendDeathLink)
            return;

        // TODO: these args are backwards on kono master
        DeathLink.SendDeathLink(event.getSource().getLocalizedDeathMessage(player).getString(), Objects.requireNonNullElseGet(player.getDisplayName(), player::getName).getString());

        MinecraftServer server = APRandomizer.getServer();
        if (server == null) return;

        GameRules.BooleanValue deathMessages = server.getGameRules().getRule(GameRules.RULE_SHOWDEATHMESSAGES);
        boolean death = deathMessages.get();
        deathMessages.set(false, server);
        sendDeathLink = false;
        for (ServerPlayer serverPlayer : APRandomizer.getServer().getPlayerList().getPlayers()) {
            if (serverPlayer != player) {
                serverPlayer.kill(serverPlayer.serverLevel());
            }
        }
        deathMessages.set(death, server);
        sendDeathLink = true;
    }
}
