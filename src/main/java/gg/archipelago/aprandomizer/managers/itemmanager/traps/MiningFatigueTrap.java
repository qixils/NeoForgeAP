package gg.archipelago.aprandomizer.managers.itemmanager.traps;

import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class MiningFatigueTrap implements Trap {

    @Override
    public void trigger(MinecraftServer server, ServerPlayer player) {
        server.execute(() -> {
            player.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.GUARDIAN_ELDER_EFFECT, 1F));
            player.addEffect(new MobEffectInstance(MobEffects.MINING_FATIGUE, 20 * 10));
        });
    }
}