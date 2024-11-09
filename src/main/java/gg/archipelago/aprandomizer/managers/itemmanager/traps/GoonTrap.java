package gg.archipelago.aprandomizer.managers.itemmanager.traps;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.ArrayList;
import java.util.List;

public class GoonTrap implements Trap {

    private final int numberOfGoons;
    final List<Zombie> zombies = new ArrayList<>();

    int timer = 20 * 30;

    public GoonTrap() {
        this(3);
    }

    public GoonTrap(int numberOfGoons) {
        NeoForge.EVENT_BUS.register(this);
        this.numberOfGoons = numberOfGoons;
    }

    @Override
    public void trigger(ServerPlayer player) {
        ItemStack fish = new ItemStack(Items.SALMON);
        MinecraftServer server = APRandomizer.getServer();
        if (server == null) return;

        var enchantmentRegistry = server.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        fish.enchant(enchantmentRegistry.getOrThrow(Enchantments.KNOCKBACK), 3);

        server.execute(() -> {
            ServerLevel world = (ServerLevel) player.level();
            Vec3 pos = player.position();
            for (int i = 0; i < numberOfGoons; i++) {
                Zombie goon = EntityType.ZOMBIE.create(world, EntitySpawnReason.COMMAND);
                if (goon == null) continue;
                goon.setItemInHand(InteractionHand.MAIN_HAND, fish.copy());
                goon.setTarget(player);
                Vec3 offset = Utils.getRandomPosition(pos, 5);
                goon.moveTo(offset);
                zombies.add(goon);
                world.addFreshEntity(goon);
            }
        });
    }

    @SubscribeEvent
    public void onTick(ServerTickEvent event) {
        if (--timer > 0)
            return;

        for (Zombie zombie : zombies) {
            if (zombie.level() instanceof ServerLevel level)
                zombie.kill(level);
        }

        NeoForge.EVENT_BUS.unregister(this);
    }
}