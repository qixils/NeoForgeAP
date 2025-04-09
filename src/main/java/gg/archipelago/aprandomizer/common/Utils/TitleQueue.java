package gg.archipelago.aprandomizer.common.Utils;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.LinkedList;
import java.util.List;

@EventBusSubscriber
public class TitleQueue {

    static final List<QueuedTitle> titleQueue = new LinkedList<>();

    static int titleTime;

    @SubscribeEvent
    static public void ServerTick(ServerTickEvent.Post tick) {
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

    public static void queueTitle(QueuedTitle queuedTitle) {
        titleQueue.add(queuedTitle);
    }

    public static void ClearQueue() {
        titleQueue.clear();
    }
}
