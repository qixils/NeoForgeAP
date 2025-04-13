package gg.archipelago.aprandomizer.common.events;

import dev.koifysh.archipelago.network.client.BouncePacket;
import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.SlotData;
import gg.archipelago.aprandomizer.ap.APClient;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

@EventBusSubscriber
public class onLivingHurt {
    private static final Logger log = LoggerFactory.getLogger(onLivingHurt.class);

    @SubscribeEvent
    static void onLivingDeathEvent(LivingDeathEvent event) {
        Entity damageSource = event.getSource().getEntity();
        if (damageSource == null || damageSource.getType() != EntityType.PLAYER) return;

        APClient apClient = APRandomizer.getAP();
        if (apClient == null || !apClient.isConnected()) return;

        SlotData slotData = apClient.getSlotData();
        if (slotData == null || !slotData.MC35) return;

        String name = event.getEntity().getEncodeId();

        BouncePacket packet = new BouncePacket();
        packet.tags = new String[]{"MC35"};
        packet.setData(new HashMap<>() {{
            put("enemy", name);
            put("source", APRandomizer.getAP().getSlot());
            CompoundTag nbt = event.getEntity().saveWithoutId(new CompoundTag());
            nbt.remove("UUID");
            nbt.remove("Motion");
            nbt.remove("Health");
            //LOGGER.info("nbt: {}",nbt.toString());
            put("nbt", nbt.toString());
        }});
        APRandomizer.sendBounce(packet);
    }

    @SubscribeEvent
    static void onLivingHurtEvent(LivingDamageEvent.Post event) {
        LivingEntity entity = event.getEntity();
        MinecraftServer server = entity.getServer();
        if (server == null) return;

        if (entity instanceof Pig && entity.getPassengers().isEmpty() && entity.getPassengers().getFirst() instanceof ServerPlayer player && event.getSource().getMsgId().equals("fall")) {
            AdvancementHolder advancement = server.getAdvancements().get(ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "archipelago/ride_pig"));
            if (advancement == null) {
                log.warn("Missing pigs fly achievement");
            } else {
                AdvancementProgress ap = player.getAdvancements().getOrStartProgress(advancement);
                if (!ap.isDone()) {
                    for (String s : ap.getRemainingCriteria()) {
                        player.getAdvancements().award(advancement, s);
                    }
                }
            }
        }

        Entity e = event.getSource().getEntity();
        if (e instanceof ServerPlayer player && event.getNewDamage() >= 18 && !event.getSource().is(DamageTypes.EXPLOSION) && !event.getSource().getMsgId().equals("fireball")) {
            //Utils.sendMessageToAll("damage type: "+ event.getSource().getMsgId());
            AdvancementHolder a = server.getAdvancements().get(ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "archipelago/overkill"));
            if (a == null) {
                log.warn("Missing overkill achievement");
            } else {
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
