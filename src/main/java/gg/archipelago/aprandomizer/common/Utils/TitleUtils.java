package gg.archipelago.aprandomizer.common.Utils;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

public class TitleUtils {

    static void resetTitle(Collection<ServerPlayer> players) {
        ClientboundClearTitlesPacket stitlepacket = new ClientboundClearTitlesPacket(true);

        for (ServerPlayer serverplayerentity : players) {
            serverplayerentity.connection.send(stitlepacket);
        }
    }

    static void showTitle(Collection<ServerPlayer> players, Component title, Component subtitle) {
        ClientboundSetSubtitleTextPacket subtitleTextPacket = new ClientboundSetSubtitleTextPacket(subtitle);
        ClientboundSetTitleTextPacket titleTextPacket = new ClientboundSetTitleTextPacket(title);
        for (ServerPlayer serverplayerentity : players) {
            serverplayerentity.connection.send(subtitleTextPacket);
            serverplayerentity.connection.send(titleTextPacket);
        }
    }

    static void showActionBar(Collection<ServerPlayer> players, Component actionBarText) {
        ClientboundSetActionBarTextPacket actionBarTextPacket = new ClientboundSetActionBarTextPacket(actionBarText);
        for (ServerPlayer serverplayerentity : players) {
            serverplayerentity.connection.send(actionBarTextPacket);
        }
    }

    static void setTimes(Collection<ServerPlayer> players, int fadeIn, int stay, int fadeOut) {
        ClientboundSetTitlesAnimationPacket animationPacket = new ClientboundSetTitlesAnimationPacket(fadeIn, stay, fadeOut);
        for (ServerPlayer serverplayerentity : players) {
            serverplayerentity.connection.send(animationPacket);
        }
    }

}
