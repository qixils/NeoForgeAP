package gg.archipelago.aprandomizer.managers.itemmanager.traps;

import gg.archipelago.aprandomizer.APRandomizer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.bossevents.CustomBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public class FakeWither implements Trap {

    private final CustomBossEvent witherBar;

    public FakeWither(MinecraftServer server) {
        NeoForge.EVENT_BUS.register(this);

        witherBar = server.getCustomBossEvents().create(Identifier.fromNamespaceAndPath(APRandomizer.MODID, "fake-wither"), Component.translatable(EntityType.WITHER.getDescriptionId()));
        witherBar.setColor(BossEvent.BossBarColor.PURPLE);
        witherBar.setDarkenScreen(true);
        witherBar.setMax(300);
        witherBar.setValue(0);
        witherBar.setOverlay(BossEvent.BossBarOverlay.PROGRESS);
        witherBar.setVisible(false);
    }

    @Override
    public void trigger(MinecraftServer server, ServerPlayer player) {
        server.execute(() -> {
            witherBar.addPlayer(player);
            witherBar.setVisible(true);
            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20 * 6, 0));
            player.playSound(SoundEvents.WITHER_SPAWN, 1, 1);
        });
    }

    @SubscribeEvent
    public void onTick(ServerTickEvent event) {
        if (!witherBar.isVisible())
            return;
        int value = witherBar.getValue();
        if (value >= witherBar.getMax()) {
            witherBar.setValue(0);
            witherBar.setVisible(false);
            NeoForge.EVENT_BUS.unregister(this);
            return;
        }
        witherBar.setValue(++value);
    }
}