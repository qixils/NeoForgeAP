package gg.archipelago.aprandomizer.common.events;

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
public class OnDeath {

    public static boolean sendDeathLink = true;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    static void onDeathEvent(LivingDeathEvent event) {
        if (!APRandomizer.isConnected())
            return;
        assert APRandomizer.getAP() != null; // safe because isConnected verifies this

        //only trigger on player death
        if (!(event.getEntity() instanceof ServerPlayer player))
            return;
        SlotData slotData = APRandomizer.getAP().getSlotData();
        if (slotData == null || !slotData.deathlink)
            return;
        //don't send deathlink if the cause of this death was a deathlink
        if (!sendDeathLink)
            return;

        APRandomizer.getAP().sendDeathlink(Objects.requireNonNullElseGet(player.getDisplayName(), player::getName).getString(), event.getSource().getLocalizedDeathMessage(player).getString());

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
