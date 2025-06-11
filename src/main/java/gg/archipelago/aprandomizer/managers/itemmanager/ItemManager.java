package gg.archipelago.aprandomizer.managers.itemmanager;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.APRegistries;
import gg.archipelago.aprandomizer.advancements.APCriteriaTriggers;
import gg.archipelago.aprandomizer.attachments.APAttachmentTypes;
import gg.archipelago.aprandomizer.attachments.APPlayerAttachment;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import gg.archipelago.aprandomizer.datacomponents.APDataComponents;
import gg.archipelago.aprandomizer.datacomponents.TrackedStructure;
import gg.archipelago.aprandomizer.items.*;
import gg.archipelago.aprandomizer.managers.GoalManager;
import gg.archipelago.aprandomizer.managers.itemmanager.traps.*;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class ItemManager {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public static final long DRAGON_EGG_SHARD = 45043L;

    private static final Long2ObjectMap<ResourceKey<APItem>> DEFAULT_ITEMS = Util.make(new Long2ObjectOpenHashMap<>(), map -> {
        map.put(45000L, APItems.GROUP_RECIPES_ARCHERY);
        map.put(45001L, APItems.PROGRESSIVE_RECIPES_RESOURCE_CRAFTING);
        map.put(45003L, APItems.GROUP_RECIPES_BREWING);
        map.put(45004L, APItems.GROUP_RECIPES_ENCHANTING);
        map.put(45005L, APItems.GROUP_RECIPES_BUCKET);
        map.put(45006L, APItems.GROUP_RECIPES_FLINT_AND_STEEL);
        map.put(45007L, APItems.GROUP_RECIPES_BEDS);
        map.put(45008L, APItems.GROUP_RECIPES_BOTTLES);
        map.put(45009L, APItems.GROUP_RECIPES_SHIELD);
        map.put(45010L, APItems.GROUP_RECIPES_FISHING);
        map.put(45011L, APItems.GROUP_RECIPES_CAMPFIRES);

        map.put(45012L, APItems.PROGRESSIVE_RECIPES_WEAPONS);
        map.put(45013L, APItems.PROGRESSIVE_RECIPES_TOOLS);
        map.put(45014L, APItems.PROGRESSIVE_RECIPES_ARMOR);

        map.put(45015L, APItems.ITEMSTACK_NETHERITE_SCRAP);
        map.put(45016L, APItems.ITEMSTACK_EIGHT_EMERALD);
        map.put(45017L, APItems.ITEMSTACK_FOUR_EMERALD);

        map.put(45018L, APItems.ITEMSTACK_ENCHANTMENT_CHANNELING_ONE);
        map.put(45019L, APItems.ITEMSTACK_ENCHANTMENT_SILK_TOUCH_ONE);
        map.put(45020L, APItems.ITEMSTACK_ENCHANTMENT_SHARPNESS_THREE);
        map.put(45021L, APItems.ITEMSTACK_ENCHANTMENT_PIERCING_FOUR);
        map.put(45022L, APItems.ITEMSTACK_ENCHANTMENT_MOB_LOOTING_THREE);
        map.put(45023L, APItems.ITEMSTACK_ENCHANTMENT_INFINITY_ARROWS_ONE);

        map.put(45024L, APItems.ITEMSTACK_DIAMOND_ORE);
        map.put(45025L, APItems.ITEMSTACK_IRON_ORE);

        map.put(45026L, APItems.EXPERIENCE_FIVE_HUNDRED);
        map.put(45027L, APItems.EXPERIENCE_ONE_HUNDRED);
        map.put(45028L, APItems.EXPERIENCE_FIFTY);

        map.put(45029L, APItems.ITEMSTACK_ENDER_PEARL);
        map.put(45030L, APItems.ITEMSTACK_LAPIS_LAZULI);
        map.put(45031L, APItems.ITEMSTACK_COOKED_PORKCHOP);
        map.put(45032L, APItems.ITEMSTACK_GOLD_ORE);
        map.put(45033L, APItems.ITEMSTACK_ROTTEN_FLESH);
        map.put(45034L, APItems.ITEMSTACK_THE_ARROW);
        map.put(45035L, APItems.ITEMSTACK_THIRTY_TWO_ARROW);
        map.put(45036L, APItems.ITEMSTACK_SADDLE);

        map.put(45037L, APItems.COMPASS_VILLAGE);
        map.put(45038L, APItems.COMPASS_PILLAGER_OUTPOST);
        map.put(45039L, APItems.COMPASS_FORTRESS);
        map.put(45040L, APItems.COMPASS_BASTION_REMNANT);
        map.put(45041L, APItems.COMPASS_END_CITY);

        map.put(45042L, APItems.ITEMSTACK_SHULKER_BOX);

        map.put(45044L, APItems.GROUP_RECIPES_SPYGLASS);
        map.put(45045L, APItems.GROUP_RECIPES_LEAD);
    });

    long index = 45100L;
    private final HashMap<Long, Callable<Trap>> trapData = new HashMap<>() {{
        put(index++, BeeTrap::new);
        put(index++, CreeperTrap::new);
        put(index++, SandRain::new);
        put(index++, FakeWither::new);
        put(index++, GoonTrap::new);
        put(index++, FishFountainTrap::new);
        put(index++, MiningFatigueTrap::new);
        put(index++, BlindnessTrap::new);
        put(index++, PhantomTrap::new);
        put(index++, WaterTrap::new);
        put(index++, GhastTrap::new);
        put(index++, LevitateTrap::new);
        put(index++, AboutFaceTrap::new);
        put(index++, AnvilTrap::new);
    }};

    private LongList receivedItems = new LongArrayList();

    private final MinecraftServer server;
    private final GoalManager goalManager;

    public ItemManager(MinecraftServer server, GoalManager goalManager) {
        this.server = server;
        this.goalManager = goalManager;
    }
    public void setReceivedItems(LongList items) {
        this.receivedItems = items;
        goalManager.updateGoal(false);
        //APRandomizer.goalManager().ifPresent(value -> value.updateGoal(false));
    }

    public void giveItem(long itemID, ServerPlayer player, int itemIndex) {
        if (APRandomizer.isJailPlayers()) {
            //dont send items to players if game has not started.
            return;
        }

        APPlayerAttachment attachment = player.getData(APAttachmentTypes.AP_PLAYER);

        if (attachment.getIndex() >= itemIndex)
            return;

        //update the player's index of received items for syncing later.
        attachment.setIndex(itemIndex);
        if (DEFAULT_ITEMS.containsKey(itemID)) {
            ResourceKey<APItem> itemKey = DEFAULT_ITEMS.get(itemID);
            int tier = attachment.getTiers().getInt(itemKey);
            attachment.getTiers().put(itemKey, tier + 1);
            APCriteriaTriggers.RECEIVED_ITEM.get().trigger(player, itemKey, tier + 1);
            Optional<Holder.Reference<APItem>> itemOptional = player.registryAccess().get(itemKey);
            if (itemOptional.isPresent()) {
                APItem item = itemOptional.get().value();
                for (APReward reward : item.tiers().get(Math.min(item.tiers().size() - 1, tier)).rewards()) {
                    reward.give(player);
                }
            } else {
                LOGGER.error("{} not found", DEFAULT_ITEMS.get(itemID));
            }
        }

        if (trapData.containsKey(itemID)) {
            if (server == null) return;
            try {
                trapData.get(itemID).call().trigger(server, player);
            } catch (Exception ignored) {
            }
        }
    }


    public void giveItemToAll(long itemID, int index) {

        receivedItems.add(itemID);
        server.execute(() -> {
            for (ServerPlayer serverPlayerEntity : server.getPlayerList().getPlayers()){
                giveItem(itemID, serverPlayerEntity, index);
            }
        });
        goalManager.updateGoal(true);
    }

    /***
     fetches the index form the player's capability then makes sure they have all items after that index.
     * @param player ServerPlayer to catch up
     */
    public void catchUpPlayer(ServerPlayer player) {
        grantAllInitialRecipes(player);
        int playerIndex = player.getData(APAttachmentTypes.AP_PLAYER).getIndex();

        for (int i = playerIndex; i < receivedItems.size(); i++) {
            giveItem(receivedItems.getLong(i), player, i + 1);
        }
    }

    public LongList getAllItems() {
        return receivedItems;
    }

    public static Set<ResourceKey<Recipe<?>>> getLockedRecipes(RegistryAccess registryAccess) {
        Set<ResourceKey<Recipe<?>>> lockedRecipes = new HashSet<>();
        for (APItem item : registryAccess.lookupOrThrow(APRegistries.ARCHIPELAGO_ITEM)) {
            for (APTier tier : item.tiers()) {
                for (APReward reward : tier.rewards()) {
                    if (reward instanceof RecipeReward(ResourceKey<Recipe<?>> recipe)) {
                        lockedRecipes.add(recipe);
                    }
                }
            }
        }
        return lockedRecipes;
    }

    public static void grantAllInitialRecipes(ServerPlayer player) {
        Set<ResourceKey<Recipe<?>>> lockedRecipes = getLockedRecipes(player.registryAccess());
        Set<RecipeHolder<?>> recipes = player.server.getRecipeManager().getRecipes().stream().filter(recipe -> !lockedRecipes.contains(recipe.id())).collect(Collectors.toSet());
        player.awardRecipes(recipes);
    }

    public static void updateCompassLocation(TrackedStructure structure, ServerPlayer player, ItemStack compass) {
        MinecraftServer server = APRandomizer.getServer();
        if (server == null) return;
        CompassReward compassReward = structure.reward();

        //get the actual structure data from forge, and make sure its changed to the AP one if needed.

        //get our local custom structure if needed.
        ResourceKey<Level> world = compassReward.level().level();

        //only locate structure if the player is in the same world as the one for the compass
        //otherwise just point it to 0,0 in said dimension.
        BlockPos structurePos = new BlockPos(0,0,0);
        List<String> lore = new ArrayList<>(List.of(
                "Right click with compass in hand to",
                "cycle though unlocked compasses."));
        Component displayName = Component.empty()
                .append("Structure Compass (")
                .append(compassReward.name())
                .append(")");
        if (player.serverLevel().dimension().equals(world)) {
            structurePos = player.serverLevel().findNearestMapStructure(compassReward.structures(), player.blockPosition(), 75, false);
            if (structurePos != null) {
                lore.addFirst("Location X: " + structurePos.getX() + ", Z: " + structurePos.getZ());
            } else {
                player.displayClientMessage(Component.empty()
                        .append("Could not find a nearby ")
                        .append(compassReward.name()), false);
                displayName = Component.empty()
                        .append("Structure Compass (")
                        .append(compassReward.name())
                        .append(") Not Found")
                        .withStyle(ChatFormatting.YELLOW);
            }
        } else {
            displayName = Component.empty()
                    .append("Structure Compass (")
                    .append(compassReward.name())
                    .append(") Wrong Dimension")
                    .withStyle(ChatFormatting.DARK_RED);
        }

        if (structurePos == null)
            structurePos = new BlockPos(0,0,0);

        //update the nbt data with our new structure.
        compass.set(APDataComponents.TRACKED_STRUCTURE, new TrackedStructure(compassReward, structure.index()));

        //update the nbt data with our new structure.
        compass.set(DataComponents.LODESTONE_TRACKER, new LodestoneTracker(Optional.of(GlobalPos.of(world, structurePos)), false));
        Utils.addLodestoneTags(world,structurePos,compass);
        Utils.setNameAndLore(compass, displayName, lore);
        player.containerMenu.broadcastChanges();
    }

    // refresh all compasses in player inventory
    public static void refreshCompasses(ServerPlayer player) {
        player.getInventory().forEach((item) -> {
            TrackedStructure structure = item.get(APDataComponents.TRACKED_STRUCTURE);
            if (structure == null) return;

            updateCompassLocation(structure, player, item);
        });
    }
}
