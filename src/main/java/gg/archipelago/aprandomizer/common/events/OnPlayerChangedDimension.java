package gg.archipelago.aprandomizer.common.events;

import gg.archipelago.aprandomizer.managers.itemmanager.ItemManager;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber
public class OnPlayerChangedDimension {

    @SubscribeEvent
    public static void onChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if(!(event.getEntity() instanceof ServerPlayer player)) return;
        ItemManager.cleanCompasses(player);
        ItemManager.refreshCompasses(player);
    }
}
