package gg.archipelago.aprandomizer.managers.itemmanager.traps;

import gg.archipelago.aprandomizer.APRandomizer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public class SandRain implements Trap {
    @Override
    public void trigger(ServerPlayer player) {
        APRandomizer.server().ifPresent(server -> server.execute(() -> {
            ServerLevel world = (ServerLevel) player.level();
            Vec3 pos = player.position();
            int radius = 5;
            for (int x = (int) pos.x - radius; x <= (int) pos.x + radius; x++) {
                for (int z = (int) pos.z - radius; z <= (int) pos.z + radius; z++) {
                    BlockPos blockPos = new BlockPos(x, (int) pos.y + 15, z);
                    if (world.isEmptyBlock(blockPos))
                        world.setBlock(blockPos, Blocks.SAND.defaultBlockState(), 3);
                }
            }
        }));
    }
}