package gg.archipelago.aprandomizer.managers.itemmanager.traps;

import gg.archipelago.aprandomizer.APRandomizer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class BlindnessTrap implements Trap {
    @Override
    public void trigger(MinecraftServer server, ServerPlayer player) {
        server.execute(() -> {
            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20 * 10));
            player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 20 * 10));
        });
    }
}