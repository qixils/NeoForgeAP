package gg.archipelago.aprandomizer.managers.itemmanager.traps;

import gg.archipelago.aprandomizer.APRandomizer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class LevitateTrap implements Trap {
    @Override
    public void trigger(MinecraftServer server, ServerPlayer player) {
        server.execute(() -> player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 20 * 10)));
    }
}