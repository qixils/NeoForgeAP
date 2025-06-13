package gg.archipelago.aprandomizer.items.traps;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import gg.archipelago.aprandomizer.items.APReward;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public record MobTrap(EntityType<?> type, int count, int radius, boolean target, Optional<Integer> angerTime) implements APReward {

    public static final MapCodec<MobTrap> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(
                    EntityType.CODEC.fieldOf("entity_type").forGetter(MobTrap::type),
                    Codec.INT.optionalFieldOf("count", 1).forGetter(MobTrap::count),
                    Codec.INT.fieldOf("radius").forGetter(MobTrap::radius),
                    Codec.BOOL.optionalFieldOf("target", false).forGetter(MobTrap::target),
                    Codec.INT.optionalFieldOf("anger_time").forGetter(MobTrap::angerTime))
            .apply(instance, MobTrap::new));

    @Override
    public MapCodec<? extends APReward> codec() {
        return MAP_CODEC;
    }

    @Override
    public void give(ServerPlayer player) {
        ServerLevel world = player.serverLevel();
        Vec3 pos = player.position();
        for (int i = 0; i < count; i++) {
            Entity entity = type.create(world, EntitySpawnReason.MOB_SUMMONED);
            if (entity == null) continue;
            Vec3 offset = Utils.getRandomPosition(pos, radius);
            entity.snapTo(offset);

            if (target && entity instanceof Mob mob) {
                mob.setTarget(player);
            }

            if (angerTime.isPresent() && entity instanceof NeutralMob neutralMob) {
                neutralMob.setRemainingPersistentAngerTime(angerTime.get());
                neutralMob.setPersistentAngerTarget(null);
            }

            world.addFreshEntity(entity);
        }
    }

}
