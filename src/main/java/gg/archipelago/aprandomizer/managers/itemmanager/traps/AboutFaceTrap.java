package gg.archipelago.aprandomizer.managers.itemmanager.traps;

import net.minecraft.server.level.ServerPlayer;

import java.util.Collections;

public class AboutFaceTrap implements Trap {
    @Override
    public void trigger(ServerPlayer player) {
        player.teleportTo(player.serverLevel(), player.getX(), player.getY(), player.getZ(), Collections.emptySet(), player.getYHeadRot() + 180f, player.getXRot(), false);
    }
}