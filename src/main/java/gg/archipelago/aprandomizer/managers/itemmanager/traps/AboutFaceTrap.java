package gg.archipelago.aprandomizer.managers.itemmanager.traps;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Relative;

public class AboutFaceTrap implements Trap {
    @Override
    public void trigger(ServerPlayer player) {
        player.teleportTo(player.serverLevel(), 0, 0, 0, Relative.union(Relative.DELTA, Relative.ROTATION), 180f, 0, false);
    }
}