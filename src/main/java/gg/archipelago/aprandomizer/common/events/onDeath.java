package gg.archipelago.aprandomizer.common.events;

import dev.koifysh.archipelago.helper.DeathLink;
import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.SlotData;
import gg.archipelago.aprandomizer.common.DeathLinkDamage;
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
        if (event.getSource() instanceof DeathLinkDamage)
            return;

        DeathLink.SendDeathLink(event.getSource().getLocalizedDeathMessage(player).getString(), Objects.requireNonNullElseGet(player.getDisplayName(), player::getName).getString());

        MinecraftServer server = APRandomizer.getServer();
        if (server == null) return;

        GameRules.BooleanValue deathMessages = server.getGameRules().getRule(GameRules.RULE_SHOWDEATHMESSAGES);
        boolean death = deathMessages.get();
        deathMessages.set(false, server);
        for (ServerPlayer serverPlayer : APRandomizer.getServer().getPlayerList().getPlayers()) {
            if (serverPlayer != player) {
                serverPlayer.hurtServer(serverPlayer.serverLevel(), new DeathLinkDamage(), Float.MAX_VALUE);
            }
        }
        deathMessages.set(death, server);
    }
}
