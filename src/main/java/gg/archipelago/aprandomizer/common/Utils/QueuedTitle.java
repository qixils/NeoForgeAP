package gg.archipelago.aprandomizer.common.Utils;

import gg.archipelago.aprandomizer.APRandomizer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class QueuedTitle {
    private final MinecraftServer server;
    private final int ticks;
    private final List<ServerPlayer> players;
    private final int fadeIn;
    private final int stay;
    private final int fadeOut;
    private final Component subTitle;
    private final Component title;
    private Component chatMessage = null;

    public QueuedTitle(MinecraftServer server, List<ServerPlayer> players, int fadeIn, int stay, int fadeOut, Component subTitle, Component title) {
        this.server = server;
        this.players = players;
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
        this.subTitle = subTitle;
        this.title = title;
        this.ticks = fadeIn + stay + fadeOut + 20;
    }

    public QueuedTitle(MinecraftServer server, List<ServerPlayer> players, int fadeIn, int stay, int fadeOut, Component subTitle, Component title, Component chatMessage) {
        this(server, players, fadeIn, stay, fadeOut, subTitle, title);
        this.chatMessage = chatMessage;
    }


    public void sendTitle() {
        server.execute(() -> {
            TitleUtils.setTimes(players, fadeIn, stay, fadeOut);
            TitleUtils.showTitle(players, title, subTitle);
            if (chatMessage != null) {
                Utils.sendMessageToAll(chatMessage);
            }
        });
    }

    public int getTicks() {
        return ticks;
    }
}
