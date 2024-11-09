package gg.archipelago.aprandomizer.common.events;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.managers.GoalManager;
import net.minecraft.core.BlockPos;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber
public class onPlayerClone {
    @SubscribeEvent
    public static void onPlayerCloneEvent(PlayerEvent.PlayerRespawnEvent event) {
        if (APRandomizer.isJailPlayers()) {
            BlockPos jail = APRandomizer.getJailPosition();
            event.getEntity().teleportTo(jail.getX(), jail.getY(), jail.getZ());
        }

        //if we are leaving because the dragon is dead check if our goals are all done!
        GoalManager goalManager = APRandomizer.getGoalManager();
        if (event.isEndConquered() && goalManager != null && goalManager.isDragonDead()) {
            goalManager.checkGoalCompletion();
        }
    }
}
