package gg.archipelago.aprandomizer.common.events;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.attachments.APAttachmentTypes;
import gg.archipelago.aprandomizer.items.CompassReward;
import gg.archipelago.aprandomizer.managers.itemmanager.ItemManager;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.List;
import java.util.Optional;

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
        if (event.getItemStack().has(DataComponents.CUSTOM_DATA) && event.getItemStack().get(DataComponents.CUSTOM_DATA).copyTag().get("structure") != null && block.is(Blocks.LODESTONE))
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

        CompoundTag nbt = customData.copyTag();

        //fetch our current compass list.
        List<CompassReward> compasses = event.getEntity().getData(APAttachmentTypes.AP_PLAYER).getUnlockedCompassRewards();

        HolderLookup.Provider registries = event.getLevel().registryAccess();
        Optional<CompassReward> currentCompassReward = nbt.read("structure", CompassReward.CODEC, registries.createSerializationContext(NbtOps.INSTANCE));
        Optional<Integer> currentCompassIndex = nbt.getInt("index");

        if (currentCompassReward.isEmpty() || currentCompassIndex.isEmpty())
            return;

        int newCompassIndex = currentCompassIndex.get() + 1;
        if (compasses.size() <= newCompassIndex) {
            newCompassIndex = 0;
        }
        nbt.putInt("index", newCompassIndex);
        compass.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
        CompassReward newCompassReward = compasses.get(newCompassIndex);

        ItemManager.updateCompassLocation(newCompassReward, player, compass);
    }
}
