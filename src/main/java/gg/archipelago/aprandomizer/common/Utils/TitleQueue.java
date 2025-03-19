package gg.archipelago.aprandomizer.common.Utils;

import gg.archipelago.aprandomizer.APRandomizer;
import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.LinkedList;
import java.util.List;

@EventBusSubscriber
public class TitleQueue {

    static List<QueuedTitle> titleQueue = new LinkedList<>();

    static int titleTime;

    final private static MinecraftServer server = APRandomizer.getServer();

    @SubscribeEvent
    static public void serverTick(ServerTickEvent.Post tick) {
        if (titleQueue.size() > 0) {
            if (titleTime <= 0) {
                QueuedTitle title = titleQueue.get(0);
                titleQueue.remove(0);
                titleTime = title.getTicks();
                title.sendTitle();
            }
        }
        if (titleTime > 0) {
            titleTime -= 1;
        }
    }

    public static void queueTitle(QueuedTitle queuedTitle) {
        titleQueue.add(queuedTitle);
    }

    public static void clearTitleQueue() {
        titleQueue.clear();
    }
}
