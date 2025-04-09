package gg.archipelago.aprandomizer.common.events;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.managers.itemmanager.ItemManager;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.ArrayList;

@EventBusSubscriber
public class onPlayerInteract {

    static void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getSide().isClient())
            return;
        //stop all right click interactions if game has not started.
        if (APRandomizer.isJailPlayers() && event instanceof ICancellableEvent cancellable)
            cancellable.setCanceled(true);
    }

    @SubscribeEvent
    static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        onPlayerInteract(event);
    }

    @SubscribeEvent
    static void onPlayerBlockInteract(PlayerInteractEvent.RightClickBlock event) {
        onPlayerInteract(event);

        if (event.getSide().isClient())
            return;
        if (!event.getItemStack().getItem().equals(Items.COMPASS) || !event.getItemStack().has(DataComponents.CUSTOM_DATA)) {
            return;
        }

        BlockState block = event.getLevel().getBlockState(event.getHitVec().getBlockPos());
        if (event.getItemStack().has(DataComponents.CUSTOM_DATA) && block.is(Blocks.LODESTONE))
            event.setCanceled(true);

        MinecraftServer server = event.getEntity().getServer();
        if (server == null) return;
        server.execute(() -> {
            event.getEntity().getInventory().setChanged();
            event.getEntity().inventoryMenu.broadcastChanges();
        });
    }

    @SubscribeEvent
    static void onPlayerInteractEvent(PlayerInteractEvent.RightClickItem event) {
        onPlayerInteract(event);

        if (event.getSide().isClient())
            return;
        if (!(event.getEntity() instanceof ServerPlayer player))
            return;

        if (!event.getItemStack().getItem().equals(Items.COMPASS))
            return;
        ItemStack compass = event.getItemStack();
        CustomData customData = compass.get(DataComponents.CUSTOM_DATA);
        if (customData == null) return;
        String trackedStructure = customData.getUnsafe().getString(APRandomizer.MODID + ":tracked_structure");
        if (trackedStructure.isBlank()) return;
        ResourceLocation location = ResourceLocation.parse(trackedStructure);

        //fetch our current compass list.
        ItemManager itemManager = APRandomizer.getItemManager();
        if (itemManager == null) return;

        ArrayList<TagKey<Structure>> compasses = itemManager.getCompasses();

        TagKey<Structure> tagKey = TagKey.create(Registries.STRUCTURE, location);
        //get our current structures index in that list, increase it by one, wrapping it to 0 if needed.
        int index = compasses.indexOf(tagKey) + 1;
        if (index >= compasses.size())
            index = 0;

        if (compasses.isEmpty()) return;
        TagKey<Structure> structure = compasses.get(index);
        ItemManager.updateCompassLocation(structure, player, compass);

    }
}
