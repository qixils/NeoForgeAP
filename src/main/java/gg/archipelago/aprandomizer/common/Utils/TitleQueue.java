package gg.archipelago.aprandomizer.common.Utils;

import gg.archipelago.aprandomizer.APRandomizer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.LinkedList;
import java.util.List;

@Mod.EventBusSubscriber
public class TitleQueue {

    static List<QueuedTitle> titleQueue = new LinkedList<>();

    static int titleTime;

    final private static MinecraftServer server = APRandomizer.getServer();

    @SubscribeEvent
    static public void ServerTick(TickEvent.ServerTickEvent tick) {
        if (tick.phase == TickEvent.Phase.END) {
            if (!titleQueue.isEmpty()) {
                if (titleTime <= 0) {
                    QueuedTitle title = titleQueue.getFirst();
                    titleQueue.removeFirst();
                    titleTime = title.getTicks();
                    title.sendTitle();
                }
            }
            if (titleTime > 0) {
                titleTime -= 1;
            }
        }
    }

    public static void queueTitle(QueuedTitle queuedTitle) {
        titleQueue.add(queuedTitle);
    }

    public static void clearTitleQueue() {
        titleQueue.clear();
    }
}
