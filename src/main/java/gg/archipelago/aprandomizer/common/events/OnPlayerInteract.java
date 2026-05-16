package gg.archipelago.aprandomizer.common.events;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.attachments.APAttachmentTypes;
import gg.archipelago.aprandomizer.items.CompassReward;
import gg.archipelago.aprandomizer.managers.itemmanager.ItemManager;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.dialog.*;
import net.minecraft.server.dialog.action.StaticAction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.CustomClickActionEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.*;

@EventBusSubscriber
public class OnPlayerInteract {

    private static final Identifier SET_COMPASS = Identifier.fromNamespaceAndPath(APRandomizer.MODID, "set_compass");

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

        if (!event.getItemStack().has(DataComponents.CUSTOM_DATA) || !event.getItemStack().getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().contains("target"))
            return;

        BlockState block = event.getLevel().getBlockState(event.getHitVec().getBlockPos());
        if (block.is(Blocks.LODESTONE))
            event.setCanceled(true);

        event.getEntity().getInventory().setChanged();
        event.getEntity().inventoryMenu.broadcastChanges();
    }

    @SubscribeEvent
    static void onPlayerInteractEvent(PlayerInteractEvent.RightClickItem event) {
        onPlayerInteract(event);

        if (event.getSide().isClient())
            return;
        if (!(event.getEntity() instanceof ServerPlayer player))
            return;

        if (!isCompass(event.getItemStack()))
            return;

        //fetch our current compass list.
        Collection<CompassReward> compasses = event.getEntity().getData(APAttachmentTypes.AP_PLAYER).getUnlockedCompassRewards().values();
        Map<String, Set<CompassReward>> categories = new HashMap<>();
        for (CompassReward reward : compasses) {
            Set<CompassReward> category = categories.computeIfAbsent(reward.category(), name -> new HashSet<>());
            category.add(reward);
        }

        Dialog dialog;
        if (categories.size() > 1) {
            dialog = new DialogListDialog(
                    new CommonDialogData(
                            Component.literal("Compass"),
                            Optional.empty(),
                            true,
                            true,
                            DialogAction.CLOSE,
                            List.of(),
                            List.of()),
                    HolderSet.direct(Holder::direct, categories.entrySet().stream().sorted(Comparator.comparing(entry -> entry.getKey())).map(entry -> createDialog(player, entry.getValue(), entry.getKey())).toList()),
                    Optional.empty(),
                    1,
                    CommonButtonData.DEFAULT_WIDTH);
        } else if (categories.size() == 1) {
            dialog = createDialog(player, categories.values().iterator().next(), categories.keySet().iterator().next());
        } else {
            return;
        }

        event.getEntity().openDialog(Holder.direct(dialog));
    }

    @SubscribeEvent
    static void onCustomClickAction(CustomClickActionEvent event) {
        if (!event.getIdentifier().equals(SET_COMPASS) || event.getPayload() == null)
            return;

        Optional<Identifier> idResult = Identifier.CODEC.parse(NbtOps.INSTANCE, event.getPayload()).result();
        if (idResult.isEmpty())
            return;

        event.setCanceled(true);

        CompassReward compassReward = event.getPlayer().getData(APAttachmentTypes.AP_PLAYER).getUnlockedCompassRewards().get(idResult.get());
        if (compassReward == null)
            return;

        ItemStack compass = event.getPlayer().getMainHandItem();
        if (!isCompass(compass)) {
            for (ItemStack stack : event.getPlayer().getInventory()) {
                if (isCompass(stack)) {
                    compass = stack;
                    break;
                }
            }
        }

        if (!isCompass(compass))
            return;

        ItemManager.updateCompassLocation(compassReward, event.getPlayer(), compass);
    }

    private static Dialog createDialog(ServerPlayer player, Set<CompassReward> compasses, String category) {
        RegistryOps<Tag> ops = player.registryAccess().createSerializationContext(NbtOps.INSTANCE);
        return new MultiActionDialog(
                new CommonDialogData(
                        Component.literal(category),
                        Optional.empty(),
                        true,
                        true,
                        DialogAction.CLOSE,
                        List.of(),
                        List.of()),
                compasses.stream()
                        .sorted(Comparator.comparing(compass -> compass.name().getString()))
                        .map(compassReward -> new ActionButton(
                                new CommonButtonData(
                                        compassReward.name(),
                                        CommonButtonData.DEFAULT_WIDTH),
                                Optional.of(
                                        new StaticAction(
                                                new ClickEvent.Custom(
                                                        SET_COMPASS,
                                                        Identifier.CODEC.encodeStart(ops, compassReward.id()).result())))))
                        .toList(),
                Optional.empty(),
                1);
    }

    private static boolean isCompass(ItemStack compass) {
        if (!compass.is(Items.COMPASS))
            return false;

        CustomData customData = compass.get(DataComponents.CUSTOM_DATA);
        if (customData == null)
            return false;

        CompoundTag nbt = customData.copyTag();
        if (!nbt.contains("target"))
            return false;

        return true;
    }
}
