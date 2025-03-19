package gg.archipelago.aprandomizer.common.events;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.ap.storage.APMCData;
import gg.archipelago.aprandomizer.managers.advancementmanager.AdvancementManager;
import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@EventBusSubscriber
public class onAdvancement {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    static void onAdvancementEvent(AdvancementEvent.AdvancementProgressEvent event) {
        APRandomizer.server.execute(() -> {
            for (String progress : event.getAdvancementProgress().getCompletedCriteria()) {
                for (ServerPlayer p : APRandomizer.server.getPlayerList().getPlayers()) {
                    p.getAdvancements().award(event.getAdvancement(), progress);
                }
            }
        });

    }

    @SubscribeEvent
    static void onAdvancementEvent(AdvancementEvent.AdvancementEarnEvent event) {
        //dont do any checking if the apmcdata file is not valid.
        if (APRandomizer.getApmcData().state != APMCData.State.VALID)
            return;

        ServerPlayer player = (ServerPlayer) event.getEntity();
        Advancement advancement = event.getAdvancement().value();
        ResourceLocation id = event.getAdvancement().id();

        AdvancementManager APAdvancementManager = APRandomizer.getAdvancementManager();
        //don't do anything if this advancement has already been had, or is not on our list of tracked advancements.
        if (!APAdvancementManager.hasAdvancement(id) && APAdvancementManager.getAdvancementID(id) != 0) {
            LOGGER.debug("{} has gotten the advancement {}", player.getDisplayName().getString(), id);
            APAdvancementManager.addAdvancement(APAdvancementManager.getAdvancementID(id));
            APAdvancementManager.syncAdvancement(event.getAdvancement());
            if(advancement.display().isEmpty())
                return;
            APRandomizer.getServer().getPlayerList().broadcastSystemMessage(
                     advancement.display().get().getType().createAnnouncement(event.getAdvancement(), player),
                    false
            );

        }
    }
}
