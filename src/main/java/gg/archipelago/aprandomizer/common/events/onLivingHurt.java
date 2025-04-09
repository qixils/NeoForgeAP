package gg.archipelago.aprandomizer.common.events;

import dev.koifysh.archipelago.network.client.BouncePacket;
import gg.archipelago.aprandomizer.APClient;
import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.SlotData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

import java.util.HashMap;

@EventBusSubscriber
public class onLivingHurt {
    @SubscribeEvent
    static void onLivingDeathEvent(LivingDeathEvent event) {
        Entity damageSource = event.getSource().getEntity();
        if (damageSource == null || damageSource.getType() != EntityType.PLAYER) return;

        APClient apClient = APRandomizer.getAP();
        if (apClient == null || !apClient.isConnected()) return;

        SlotData slotData = apClient.getSlotData();
        if (slotData == null || !slotData.MC35) return;

        //TODO: this may be broken.
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
}
