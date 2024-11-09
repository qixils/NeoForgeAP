package gg.archipelago.aprandomizer.managers.itemmanager.traps;

import net.minecraft.server.level.ServerPlayer;

public interface Trap {

    void trigger(ServerPlayer player);
}