package gg.archipelago.aprandomizer.common.Utils;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.ap.APClient;
import io.github.archipelagomw.Print.APPrint;
import io.github.archipelagomw.Print.APPrintColor;
import io.github.archipelagomw.Print.APPrintPart;
import io.github.archipelagomw.Print.APPrintType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.*;

import static io.github.archipelagomw.flags.NetworkItem.*;

public class Utils {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    @NotNull
    private static MinecraftServer server() {
        return Objects.requireNonNull(APRandomizer.getServer(), "Server not started");
    }

    public static void sendMessageToAll(String message) {
        sendMessageToAll(Component.literal(message));
    }

    public static void sendMessageToAll(Component message) {
        //tell the server to send the message in a thread safe way.
        server().execute(() -> server().getPlayerList().broadcastSystemMessage(message, false));
    }

    public static void sendFancyMessageToAll(APPrint apPrint) {
        Component message = Utils.apPrintToTextComponent(apPrint);

        //tell the server to send the message in a thread safe way.
        server().execute(() -> server().getPlayerList().broadcastSystemMessage(message, false));

    }

    public static boolean bitmaskMatchAll(int value, int mask) {
        return (value & mask) == mask;
    }

    public static int colorOfFlags(int flags) {
        if (bitmaskMatchAll(flags, ADVANCEMENT + USEFUL + TRAP)) {
            return 0x80FF80;
        }
        if (bitmaskMatchAll(flags, ADVANCEMENT + USEFUL)) {
            return 0xFFDF00;
        }
        if (bitmaskMatchAll(flags, ADVANCEMENT + TRAP)) {
            return 0xFFAC1D;
        }
        if (bitmaskMatchAll(flags, USEFUL + TRAP)) {
            return 0x9B59B7;
        }
        if (bitmaskMatchAll(flags, ADVANCEMENT)) {
            return 0xAF99EF;
        }
        if (bitmaskMatchAll(flags, USEFUL)) {
            return 0x6D8BE8;
        }
        if (bitmaskMatchAll(flags, TRAP)) {
            return 0xFA8072;
        }
        return 0x00EEEE;
    }

    public static Component apPrintToTextComponent(APPrint apPrint) {
        APClient apClient = APRandomizer.getAP();
        boolean isMe = apClient != null && apPrint.receiving == apClient.getSlot();

        MutableComponent message = Component.literal("");
        for (int i = 0; apPrint.parts.length > i; ++i) {
            APPrintPart part = apPrint.parts[i];
            LOGGER.trace("part[{}]: {}, {}, {}", i, part.text, part.color, part.type);
            //no default color was sent so use our own coloring.
            int color = isMe ? Color.PINK.getRGB() : Color.WHITE.getRGB();
            boolean bold = part.color == APPrintColor.bold;
            boolean underline = part.color == APPrintColor.underline;

            if (part.color == APPrintColor.none) {
                if (APRandomizer.getAP().getMyName().equals(part.text)) {
                    color = 0xEE00EE;
                    underline = true;
                } else if (part.type == APPrintType.playerID) {
                    color = 0xFAFAD2;
                } else if (part.type == APPrintType.locationID) {
                    color = 0x00FF7F;
                } else if (part.type == APPrintType.itemID) {
                    color = colorOfFlags(part.flags);
                }
            }
            else
                color = part.color.color.getRGB();

            if (part.color == APPrintColor.underline)
                underline = true;

            //blank out the first two bits because minecraft doesn't deal with alpha values
            int iColor = color & ~(0xFF << 24);
            Style style = Style.EMPTY.withColor(iColor).withBold(bold).withUnderlined(underline);

            message.append(Component.literal(part.text).withStyle(style));
        }
        return message;
    }

    public static void sendTitleToAll(Component title, Component subTitle, int fadeIn, int stay, int fadeOut) {
        server().execute(() -> TitleQueue.queueTitle(new QueuedTitle(server(), server().getPlayerList().getPlayers(), fadeIn, stay, fadeOut, subTitle, title)));
    }

    public static void sendTitleToAll(Component title, Component subTitle, Component chatMessage, int fadeIn, int stay, int fadeOut) {
        server().execute(() -> TitleQueue.queueTitle(new QueuedTitle(server(), server().getPlayerList().getPlayers(), fadeIn, stay, fadeOut, subTitle, title, chatMessage)));
    }

    public static void sendActionBarToAll(String actionBarMessage, int fadeIn, int stay, int fadeOut) {
        server().execute(() -> {
            TitleUtils.setTimes(server().getPlayerList().getPlayers(), fadeIn, stay, fadeOut);
            TitleUtils.showActionBar(server().getPlayerList().getPlayers(), Component.literal(actionBarMessage));
        });
    }

    public static void sendActionBarToPlayer(ServerPlayer player, String actionBarMessage, int fadeIn, int stay, int fadeOut) {
        server().execute(() -> {
            TitleUtils.setTimes(Collections.singletonList(player), fadeIn, stay, fadeOut);
            TitleUtils.showActionBar(Collections.singletonList(player), Component.literal(actionBarMessage));
        });
    }

    public static void PlaySoundToAll(SoundEvent sound) {
        server().execute(() -> {
            for (ServerPlayer player : server().getPlayerList().getPlayers()) {
                player.playSound(sound, 1, 1);
            }
        });
    }

    public static ResourceKey<Level> getStructureWorld(Identifier id) {

        String structureName = getAPStructureName(id);
        //fetch what structures are where from our APMC data.
        Map<String, String> structures = APRandomizer.getApmcData().structures;
        for (Map.Entry<String, String> entry : structures.entrySet()) {
            if (!entry.getValue().equals(structureName)) continue;
            if (entry.getKey().contains("Overworld"))
                return Level.OVERWORLD;
            if (entry.getKey().contains("Nether"))
                return Level.NETHER;
            if (entry.getKey().contains("The End"))
                return Level.END;
        }

        return Level.OVERWORLD;
    }

    public static String getAPStructureName(Identifier id) {
        return switch (id.toString()) {
            case "aprandomizer:village" -> "Village";
            case "aprandomizer:end_city" -> "End City";
            case "aprandomizer:pillager_outpost" -> "Pillager Outpost";
            case "aprandomizer:fortress" -> "Nether Fortress";
            case "aprandomizer:bastion_remnant" -> "Bastion Remnant";
            default -> id.getPath().toLowerCase();
        };
    }

    public static Vec3 getRandomPosition(Vec3 pos, int radius) {
        double a = Math.random() * Math.PI * 2;
        double b = Math.random() * Math.PI / 2;
        double x = radius * Math.cos(a) * Math.sin(b) + pos.x;
        double z = radius * Math.sin(a) * Math.sin(b) + pos.z;
        double y = radius * Math.cos(b) + pos.y;
        return new Vec3(x, y, z);
    }

    public static void addLodestoneTags(ResourceKey<Level> worldRegistryKey, BlockPos blockPos, ItemStack item) {
        item.set(DataComponents.LODESTONE_TRACKER, new LodestoneTracker(Optional.of(new GlobalPos(worldRegistryKey, blockPos)),false));
    }

    public static void giveItemToPlayer(ServerPlayer player, ItemStack itemstack) {
        boolean flag = player.getInventory().add(itemstack);
        if (flag && itemstack.isEmpty()) {
            itemstack.setCount(1);
            ItemEntity itementity1 = player.drop(itemstack, false);
            if (itementity1 != null) {
                itementity1.makeFakeItem();
            }
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
            player.inventoryMenu.broadcastChanges();
        } else {
            ItemEntity itementity = player.drop(itemstack, false);
            if (itementity != null) {
                itementity.setNoPickUpDelay();
                itementity.setTarget(player.getUUID());
            }
        }
    }

    public static void setNameAndLore(ItemStack itemstack, String itemName, Collection<String> itemLore) {
        setItemName(itemstack, itemName);
        setItemLore(itemstack, itemLore);
    }

    public static void setNameAndLore(ItemStack itemstack, Component itemName, Collection<String> itemLore) {
        setItemName(itemstack, itemName);
        setItemLore(itemstack, itemLore);
    }

    public static void setItemName(ItemStack itemstack, String itemName) {
        setItemName(itemstack, Component.literal(itemName));
    }

    public static void setItemName(ItemStack itemstack, Component itemName) {
        itemstack.set(DataComponents.CUSTOM_NAME, itemName);
    }

    public static void setItemLore(ItemStack iStack, Collection<String> itemLore) {
        iStack.set(DataComponents.LORE, new ItemLore(itemLore.stream().<Component>map(Component::literal).toList()));
    }
}