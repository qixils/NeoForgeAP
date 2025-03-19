package gg.archipelago.aprandomizer.common.events;

import dev.koifysh.archipelago.network.client.BouncePacket;
import gg.archipelago.aprandomizer.APRandomizer;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Pig;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber
public class onLivingHurt {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    static void onLivingDeathEvent(LivingDeathEvent event) {
        if(!APRandomizer.isConnected())
            return;

        String name = event.getEntity().getEncodeId();
        if(!APRandomizer.getAP().getSlotData().MC35)
            return;

        Entity damageSource = event.getSource().getEntity();
        if(damageSource != null && damageSource.getType() == EntityType.PLAYER) {
            BouncePacket packet = new BouncePacket();
            packet.tags = new String[]{"MC35"};
            CompoundTag nbt = event.getEntity().saveWithoutId(new CompoundTag());
            nbt.remove("UUID");
            nbt.remove("Motion");
            nbt.remove("Health");
            packet.setData(new HashMap<>(Map.of(
                "enemy", name,
                "source", APRandomizer.getAP().getSlot(),
                //LOGGER.info("nbt: {}",nbt.toString());
                "nbt", nbt.toString()
            )));
            APRandomizer.sendBounce(packet);
        }
    }

    @SubscribeEvent
    static void onLivingHurtEvent(LivingDamageEvent.Post event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof Pig) {
            if (entity.getPassengers().size() > 0) {
                if (entity.getPassengers().get(0) instanceof ServerPlayer) {
                    if (event.getSource().is(DamageTypes.FALL)) {
                        ServerPlayer player = (ServerPlayer) entity.getPassengers().get(0);
                        AdvancementHolder advancement = event.getEntity().getServer().getAdvancements().get(ResourceLocation.fromNamespaceAndPath("aprandomizer", "archipelago/ride_pig"));
                        AdvancementProgress ap = player.getAdvancements().getOrStartProgress(advancement);
                        if (!ap.isDone()) {
                            for (String s : ap.getRemainingCriteria()) {
                                player.getAdvancements().award(advancement, s);
                            }
                        }
                    }
                }
            }
        }

        Entity e = event.getSource().getEntity();
        if (e instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer) e;
            //Utils.sendMessageToAll("damage type: "+ event.getSource().getMsgId());
            if (event.getNewDamage() >= 18 && !event.getSource().is(DamageTypes.EXPLOSION) && !event.getSource().is(DamageTypes.FIREBALL)) {
                AdvancementHolder a = event.getEntity().getServer().getAdvancements().get(ResourceLocation.fromNamespaceAndPath("aprandomizer", "archipelago/overkill"));
                AdvancementProgress ap = player.getAdvancements().getOrStartProgress(a);
                if (!ap.isDone()) {
                    for (String s : ap.getRemainingCriteria()) {
                        player.getAdvancements().award(a, s);
                    }
                }
            }
        }
    }
}
