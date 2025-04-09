package gg.archipelago.aprandomizer.common.Utils;

import gg.archipelago.aprandomizer.APRandomizer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class QueuedTitle {

    private final int ticks;
    private final List<ServerPlayer> players;
    private final int fadeIn;
    private final int stay;
    private final int fadeOut;
    private final Component subTitle;
    private final Component title;
    private Component chatMessage = null;

    public QueuedTitle(List<ServerPlayer> players, int fadeIn, int stay, int fadeOut, Component subTitle, Component title) {
        this.players = players;
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
        this.subTitle = subTitle;
        this.title = title;
        this.ticks = fadeIn + stay + fadeOut + 20;
    }

    public QueuedTitle(List<ServerPlayer> players, int fadeIn, int stay, int fadeOut, Component subTitle, Component title, Component chatMessage) {
        this(players, fadeIn, stay, fadeOut, subTitle, title);
        this.chatMessage = chatMessage;

    }


    public void sendTitle() {
        APRandomizer.server().ifPresent(server -> server.execute(() -> {
            TitleUtils.setTimes(players, fadeIn, stay, fadeOut);
            TitleUtils.showTitle(players, title, subTitle);
            if (chatMessage != null) {
                Utils.sendMessageToAll(chatMessage);
            }
        }));
    }

    public int getTicks() {
        return ticks;
    }
}
