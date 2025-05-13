package gg.archipelago.aprandomizer.common.events;

import gg.archipelago.aprandomizer.APRandomizer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber
public class OnBlockBreak {
    @SubscribeEvent
    static void onPlayerBlockInteract(BlockEvent.BreakEvent event) {
        if (!APRandomizer.isJailPlayers())
            return;
        event.setCanceled(true);
        if (event.getPlayer() instanceof ServerPlayer player)
            player.sendSystemMessage(Component.literal("No!"));
    }
}
