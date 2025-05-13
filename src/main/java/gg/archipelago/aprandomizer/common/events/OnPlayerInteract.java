package gg.archipelago.aprandomizer.common.events;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.attachments.APAttachmentTypes;
import gg.archipelago.aprandomizer.datacomponents.APDataComponents;
import gg.archipelago.aprandomizer.datacomponents.TrackedStructure;
import gg.archipelago.aprandomizer.items.CompassReward;
import gg.archipelago.aprandomizer.managers.itemmanager.ItemManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.List;

@EventBusSubscriber
public class OnPlayerInteract {

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

        if (!event.getItemStack().has(APDataComponents.TRACKED_STRUCTURE))
            return;

        BlockState block = event.getLevel().getBlockState(event.getHitVec().getBlockPos());
        if (block.is(Blocks.LODESTONE))
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
        TrackedStructure structure = event.getItemStack().get(APDataComponents.TRACKED_STRUCTURE);
        if (structure == null)
            return;

        //fetch our current compass list.
        List<CompassReward> compasses = event.getEntity().getData(APAttachmentTypes.AP_PLAYER).getUnlockedCompassRewards();

        int newCompassIndex = structure.index() + 1;
        if (compasses.size() <= newCompassIndex) {
            newCompassIndex = 0;
        }
        TrackedStructure newStructure = new TrackedStructure(
                compasses.get(newCompassIndex),
                newCompassIndex
        );

        ItemManager.updateCompassLocation(newStructure, player, compass);
    }
}
