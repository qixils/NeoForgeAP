package gg.archipelago.aprandomizer.common.events;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.ap.storage.APMCData;
import gg.archipelago.aprandomizer.managers.advancementmanager.AdvancementManager;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

@EventBusSubscriber
public class onAdvancement {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    static void onAdvancementEvent(AdvancementEvent.AdvancementProgressEvent event) {
        MinecraftServer server = APRandomizer.getServer();
        if (server == null) return;

        server.execute(() -> {
            for (String progress : event.getAdvancementProgress().getCompletedCriteria()) {
                for (ServerPlayer p : server.getPlayerList().getPlayers()) {
                    p.getAdvancements().award(event.getAdvancement(), progress);
                }
            }
        });
    }

    @SubscribeEvent
    static void onAdvancementEvent(AdvancementEvent.AdvancementEarnEvent event) {
        MinecraftServer server = APRandomizer.getServer();
        if (server == null) return; // !?

        //dont do any checking if the apmcdata file is not valid.
        if (APRandomizer.getApmcData().state != APMCData.State.VALID) return;

        ServerPlayer player = (ServerPlayer) event.getEntity();
        AdvancementHolder advancementHolder = event.getAdvancement();
        String id = advancementHolder.id().toString();

        AdvancementManager am = APRandomizer.getAdvancementManager();
        //don't do anything if this advancement has already been had, or is not on our list of tracked advancements.
        if (am == null || am.hasAdvancement(id) || am.getAdvancementID(id) == 0) return;

        LOGGER.debug("{} has gotten the advancement {}", Objects.requireNonNullElseGet(player.getDisplayName(), player::getName).getString(), id);
        am.addAdvancement(am.getAdvancementID(id));
        am.syncAdvancement(advancementHolder);

        advancementHolder.value().display().ifPresent(it -> server.getPlayerList().broadcastSystemMessage(it.getType().createAnnouncement(advancementHolder, player), false));
    }
}
