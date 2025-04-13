package gg.archipelago.aprandomizer.common.events;

import gg.archipelago.aprandomizer.managers.itemmanager.ItemManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class onDimensionChange {

    @SubscribeEvent
    public static void onChange1(PlayerEvent.PlayerChangedDimensionEvent event) {
        if(!(event.getEntity() instanceof ServerPlayer player)) return;
        ItemManager.refreshCompasses(player);
    }

    @SubscribeEvent
    public static void onChange1(PlayerEvent.PlayerRespawnEvent event) {
        if(!(event.getEntity() instanceof ServerPlayer player)) return;
        ItemManager.refreshCompasses(player);
    }
}
