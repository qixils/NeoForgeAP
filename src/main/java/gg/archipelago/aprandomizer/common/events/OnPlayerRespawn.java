package gg.archipelago.aprandomizer.common.events;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.managers.GoalManager;
import gg.archipelago.aprandomizer.managers.itemmanager.ItemManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber
public class OnPlayerRespawn {

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if(!(event.getEntity() instanceof ServerPlayer player)) return;
        ItemManager.refreshCompasses(player);

        if (APRandomizer.isJailPlayers()) {
            BlockPos jail = APRandomizer.getJailPosition();
            player.teleportTo(jail.getX(), jail.getY(), jail.getZ());
        }

        //if we are leaving because the dragon is dead check if our goals are all done!
        GoalManager goalManager = APRandomizer.getGoalManager();
        if (goalManager != null) {
            assert APRandomizer.getWorldData() != null;
            if (APRandomizer.getWorldData().isDragonKilled()) goalManager.checkGoalCompletion();
        }
    }

}
