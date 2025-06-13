package gg.archipelago.aprandomizer.common.events;

import net.minecraft.world.entity.npc.CatSpawner;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ModifyCustomSpawnersEvent;

@EventBusSubscriber
public class OnModifyCustomSpawners {
    @SubscribeEvent
    public static void onModifyCustomSpawners(ModifyCustomSpawnersEvent event) {
        if (event.getLevel().dimension() != Level.OVERWORLD && !event.getCustomSpawners().stream().anyMatch(spawner -> spawner instanceof CatSpawner)) {
            event.addCustomSpawner(new CatSpawner());
        }
    }
}
