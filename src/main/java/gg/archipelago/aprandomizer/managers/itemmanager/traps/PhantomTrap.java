package gg.archipelago.aprandomizer.managers.itemmanager.traps;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.LinkedList;
import java.util.List;

public class PhantomTrap implements Trap {

    final List<Phantom> phantoms = new LinkedList<>();

    int timer = 20 * 45;

    public PhantomTrap() {
        NeoForge.EVENT_BUS.register(this);
    }

    @Override
    public void trigger(MinecraftServer server, ServerPlayer player) {
        server.execute(() -> {
            ServerLevel world = (ServerLevel) player.level();
            Vec3 pos = player.position();
            for (int i = 0; i < 3; i++) {
                Phantom phantom = EntityType.PHANTOM.create(world, EntitySpawnReason.MOB_SUMMONED);
                if (phantom == null) continue;
                phantom.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, MobEffectInstance.INFINITE_DURATION, 0, false, false));
                Vec3 offset = Utils.getRandomPosition(pos, 5);
                phantom.snapTo(offset);
                if (world.addFreshEntity(phantom))
                    phantoms.add(phantom);

            }
        });
    }

    @SubscribeEvent
    public void onTick(ServerTickEvent.Post event) {
        if (--timer > 0)
            return;

        for (Phantom phantom : phantoms) {
            phantom.kill((ServerLevel) phantom.level());
        }
        NeoForge.EVENT_BUS.unregister(this);
    }
}