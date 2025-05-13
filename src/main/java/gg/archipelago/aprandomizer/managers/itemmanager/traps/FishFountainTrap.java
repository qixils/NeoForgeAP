package gg.archipelago.aprandomizer.managers.itemmanager.traps;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.phys.Vec3;

public class FishFountainTrap implements Trap {

    @Override
    public void trigger(ServerPlayer player) {
        APRandomizer.server().ifPresent(server -> server.execute(() -> {
            ServerLevel world = (ServerLevel) player.level();
            Vec3 pos = player.position();
            for (int i = 0; i < 10; i++) {
                Silverfish fish = EntityType.SILVERFISH.create(world, EntitySpawnReason.MOB_SUMMONED);
                if (fish == null) continue;
                Vec3 offset = Utils.getRandomPosition(pos, 5);
                fish.snapTo(offset);
                world.addFreshEntity(fish);
            }
        }));
    }
}