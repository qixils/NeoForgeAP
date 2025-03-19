package gg.archipelago.aprandomizer.common.events;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.managers.itemmanager.ItemManager;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

@EventBusSubscriber
public class onPlayerInteract {

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    static void onPlayerBlockInteract(PlayerInteractEvent.RightClickBlock event) {
        if (event.getSide().isClient()) return;
        // stop all right click interactions if game has not started.
        if (APRandomizer.isJailPlayers())
            event.setCanceled(true);
        if (!event.getItemStack().getItem().equals(Items.COMPASS) || !event.getItemStack().has(DataComponents.CUSTOM_DATA)) {
            return;
        }

        BlockState block = event.getLevel().getBlockState(event.getHitVec().getBlockPos());
        if (event.getItemStack().get(DataComponents.CUSTOM_DATA).copyTag().get("structure") != null && block.is(Blocks.LODESTONE))
            event.setCanceled(true);

        event.getEntity().getServer().execute(() -> {
            event.getEntity().getInventory().setChanged();
            event.getEntity().inventoryMenu.broadcastChanges();
        });
    }

    @SubscribeEvent
    static void onPlayerInteractEvent(PlayerInteractEvent.RightClickItem event) {
        if(event.getSide().isClient())
            return;
        // stop all right click interactions if game has not started.
        if (APRandomizer.isJailPlayers())
            event.setCanceled(true);
        if (event.getItemStack().getItem().equals(Items.COMPASS) && event.getItemStack().has(DataComponents.CUSTOM_DATA)) {
            ItemStack compass = event.getItemStack();
            CompoundTag nbt = compass.get(DataComponents.CUSTOM_DATA).copyTag();
            if(nbt.get("structure") == null)
                return;

            //fetch our current compass list.
            ArrayList<TagKey<Structure>> compasses = APRandomizer.getItemManager().getCompasses();

            TagKey<Structure> tagKey = TagKey.create(Registries.STRUCTURE, ResourceLocation.parse(nbt.getString("structure")));
            //get our current structures index in that list, increase it by one, wrapping it to 0 if needed.
            int index = compasses.indexOf(tagKey) + 1;
            if(index >= compasses.size())
                index = 0;

            TagKey<Structure> structure = compasses.get(index);

            ItemManager.updateCompassLocation(structure,event.getEntity(),compass);

        }
    }
}
