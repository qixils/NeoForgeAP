package gg.archipelago.aprandomizer.common.events;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.APRegistries;
import gg.archipelago.aprandomizer.ap.storage.APMCData;
import gg.archipelago.aprandomizer.locations.APLocation;
import gg.archipelago.aprandomizer.locations.AdvancementLocation;
import gg.archipelago.aprandomizer.managers.advancementmanager.AdvancementManager;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

@EventBusSubscriber
public class OnAdvancement {
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
        Advancement advancement = event.getAdvancement().value();
        Identifier id = event.getAdvancement().id();

        AdvancementManager am = APRandomizer.getAdvancementManager();
        if (am == null) return;
        Registry<APLocation> locations = server.registryAccess().lookupOrThrow(APRegistries.ARCHIPELAGO_LOCATION);
        //don't do anything if this advancement has already been had, or is not on our list of tracked advancements.
        for (Map.Entry<ResourceKey<APLocation>, APLocation> entry : locations.entrySet()) {
            if (!(entry.getValue() instanceof AdvancementLocation(Identifier advKey) && advKey.equals(id) && !am.hasAdvancement(entry.getKey()) && am.getAdvancementID(entry.getKey()) != 0))
                continue;
            LOGGER.debug("{} has gotten the advancement {}", player.getDisplayName().getString(), id);
            am.addAdvancement(entry.getKey());
            am.syncAdvancement(entry.getKey(), entry.getValue());
            advancement.display().ifPresent(it -> server.getPlayerList().broadcastSystemMessage(
                    advancement.display().get().getType().createAnnouncement(event.getAdvancement(), player),
                    false
            ));
        }
    }
}
