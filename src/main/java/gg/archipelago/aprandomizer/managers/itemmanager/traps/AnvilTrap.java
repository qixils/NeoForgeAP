package gg.archipelago.aprandomizer.managers.itemmanager.traps;

import gg.archipelago.aprandomizer.APRandomizer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public class AnvilTrap implements Trap {

    @Override
    public void trigger(ServerPlayer player) {
        APRandomizer.getServer().execute(() -> {
            ServerLevel world = player.serverLevel();
            Vec3 pos = player.position();
            BlockPos blockPos = new BlockPos(player.getBlockX(), (int) pos.y + 6, player.getBlockZ());
            if (world.isEmptyBlock(blockPos))
                world.setBlock(blockPos, Blocks.ANVIL.defaultBlockState(), 3);
        });
    }
}