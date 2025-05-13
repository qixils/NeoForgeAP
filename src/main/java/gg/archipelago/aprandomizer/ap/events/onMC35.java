package gg.archipelago.aprandomizer.ap.events;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.koifysh.archipelago.events.ArchipelagoEventListener;
import dev.koifysh.archipelago.events.BouncedEvent;
import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.SlotData;
import gg.archipelago.aprandomizer.ap.APClient;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class onMC35 {

    @ArchipelagoEventListener
    public void onBounced(BouncedEvent event) {
        MinecraftServer server = APRandomizer.getServer();
        if (server == null) return;

        APClient apClient = APRandomizer.getAP();
        if (apClient == null) return;

        if (!event.tags.contains("MC35") && !(Optional.ofNullable(apClient.getSlotData()).map(SlotData::getMC35).orElse(false)))
            return;

        int sourceSlot = event.getInt("source");
        if (sourceSlot != apClient.getSlot()) {
            int randPlayer = ThreadLocalRandom.current().nextInt(server.getPlayerCount());
            ServerPlayer player = server.getPlayerList().getPlayers().get(randPlayer);
            CompoundTag eNBT = new CompoundTag();
            try {
                if (event.containsKey("nbt"))
                    eNBT = TagParser.parseCompoundFully(event.getString("nbt"));
            } catch (CommandSyntaxException ignored) {
            }
            eNBT.putString("id", event.getString("enemy"));
            Entity entity = EntityType.loadEntityRecursive(eNBT, player.level(), EntitySpawnReason.MOB_SUMMONED, (spawnEntity) -> {
                Vec3 pos = player.position();
                Vec3 offset = Utils.getRandomPosition(pos, 10);
                spawnEntity.snapTo(offset.x, offset.y, offset.z, spawnEntity.yRotO, spawnEntity.xRotO);
                return spawnEntity;
            });
            if (entity != null) {
                if (entity instanceof LivingEntity livingEntity) {
                    livingEntity.heal(livingEntity.getMaxHealth());
                    livingEntity.setLastHurtByPlayer(player, 100);
                }
                player.level().addFreshEntity(entity);
            }

        }
    }
}
