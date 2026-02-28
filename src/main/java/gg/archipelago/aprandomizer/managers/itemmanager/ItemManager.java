package gg.archipelago.aprandomizer.managers.itemmanager;

import com.google.common.base.Predicates;
import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.APRegistries;
import gg.archipelago.aprandomizer.advancements.APCriteriaTriggers;
import gg.archipelago.aprandomizer.attachments.APAttachmentTypes;
import gg.archipelago.aprandomizer.attachments.APPlayerAttachment;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import gg.archipelago.aprandomizer.data.WorldData;
import gg.archipelago.aprandomizer.items.*;
import gg.archipelago.aprandomizer.managers.GoalManager;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ItemManager {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public static final long DRAGON_EGG_SHARD = 43L;

    private static final Long2ObjectMap<ResourceKey<APItem>> DEFAULT_ITEMS = Util.make(new Long2ObjectOpenHashMap<>(), map -> {
        map.put(1L, APItems.GROUP_RECIPES_ARCHERY);
        map.put(2L, APItems.PROGRESSIVE_RECIPES_RESOURCE_CRAFTING);
        map.put(3L, APItems.GROUP_RECIPES_BREWING);
        map.put(4L, APItems.GROUP_RECIPES_ENCHANTING);
        map.put(5L, APItems.GROUP_RECIPES_BUCKET);
        map.put(6L, APItems.GROUP_RECIPES_FLINT_AND_STEEL);
        map.put(7L, APItems.GROUP_RECIPES_BEDS);
        map.put(8L, APItems.GROUP_RECIPES_BOTTLES);
        map.put(9L, APItems.GROUP_RECIPES_SHIELD);
        map.put(10L, APItems.GROUP_RECIPES_FISHING);
        map.put(11L, APItems.GROUP_RECIPES_CAMPFIRES);
        map.put(12L, APItems.PROGRESSIVE_RECIPES_WEAPONS);
        map.put(13L, APItems.PROGRESSIVE_RECIPES_TOOLS);
        map.put(14L, APItems.PROGRESSIVE_RECIPES_ARMOR);
        map.put(15L, APItems.ITEMSTACK_NETHERITE_SCRAP);
        map.put(16L, APItems.ITEMSTACK_EIGHT_EMERALD);
        map.put(17L, APItems.ITEMSTACK_FOUR_EMERALD);
        map.put(18L, APItems.ITEMSTACK_ENCHANTMENT_CHANNELING_ONE);
        map.put(19L, APItems.ITEMSTACK_ENCHANTMENT_SILK_TOUCH_ONE);
        map.put(20L, APItems.ITEMSTACK_ENCHANTMENT_SHARPNESS_THREE);
        map.put(21L, APItems.ITEMSTACK_ENCHANTMENT_PIERCING_FOUR);
        map.put(22L, APItems.ITEMSTACK_ENCHANTMENT_MOB_LOOTING_THREE);
        map.put(23L, APItems.ITEMSTACK_ENCHANTMENT_INFINITY_ARROWS_ONE);
        map.put(24L, APItems.ITEMSTACK_DIAMOND_ORE);
        map.put(25L, APItems.ITEMSTACK_IRON_ORE);
        map.put(26L, APItems.EXPERIENCE_FIVE_HUNDRED);
        map.put(27L, APItems.EXPERIENCE_ONE_HUNDRED);
        map.put(28L, APItems.EXPERIENCE_FIFTY);
        map.put(29L, APItems.ITEMSTACK_ENDER_PEARL);
        map.put(30L, APItems.ITEMSTACK_LAPIS_LAZULI);
        map.put(31L, APItems.ITEMSTACK_COOKED_PORKCHOP);
        map.put(32L, APItems.ITEMSTACK_GOLD_ORE);
        map.put(33L, APItems.ITEMSTACK_ROTTEN_FLESH);
        map.put(34L, APItems.ITEMSTACK_THE_ARROW);
        map.put(35L, APItems.ITEMSTACK_THIRTY_TWO_ARROW);
        map.put(36L, APItems.GROUP_RECIPES_SADDLE);
        map.put(37L, APItems.COMPASS_VILLAGE);
        map.put(38L, APItems.COMPASS_PILLAGER_OUTPOST);
        map.put(39L, APItems.COMPASS_FORTRESS);
        map.put(40L, APItems.COMPASS_BASTION_REMNANT);
        map.put(41L, APItems.COMPASS_END_CITY);
        map.put(42L, APItems.ITEMSTACK_SHULKER_BOX);
        //Dragon Egg Shard (If Applicable)
        map.put(44L, APItems.GROUP_RECIPES_SPYGLASS);
        map.put(45L, APItems.GROUP_RECIPES_LEAD);
        map.put(46L, APItems.TRAP_BEES);
        map.put(47L, APItems.GROUP_RECIPES_BRUSH);
        map.put(48L, APItems.COMPASS_OCEAN_MONUMENT);
        map.put(49L, APItems.COMPASS_WOODLAND_MANSION);
        map.put(50L, APItems.COMPASS_ANCIENT_CITY);
        map.put(51L, APItems.COMPASS_TRAIL_RUINS);
        //map.put(52L, APItems.COMPASS_TRIAL_CHAMBERS);
    });

//    long index = 51L;
//    private final HashMap<Long, Callable<Trap>> trapData = new HashMap<>() {{
//        put(index++, BeeTrap::new);
//        put(index++, CreeperTrap::new);
//        put(index++, SandRain::new);
//        put(index++, FakeWither::new);
//        put(index++, GoonTrap::new);
//        put(index++, FishFountainTrap::new);
//        put(index++, MiningFatigueTrap::new);
//        put(index++, BlindnessTrap::new);
//        put(index++, PhantomTrap::new);
//        put(index++, WaterTrap::new);
//        put(index++, GhastTrap::new);
//        put(index++, LevitateTrap::new);
//        put(index++, AboutFaceTrap::new);
//        put(index++, AnvilTrap::new);
//    }};

    private Object2IntMap<ResourceKey<APItem>> tiers = new Object2IntOpenHashMap<>();
    private List<Tier> receivedItems = new ArrayList<>();
    private int index = 0;
    private final MinecraftServer server;
    private final GoalManager goalManager;
    private final WorldData worldData;

    public ItemManager(MinecraftServer server, GoalManager goalManager, WorldData worldData) {
        this.server = server;
        this.goalManager = goalManager;
        this.worldData = worldData;
    }

    public void setReceivedItems(LongList items) {
        tiers = new Object2IntOpenHashMap<>();
        receivedItems = items.longStream()
                .mapToObj(this::getTier)
                .filter(Predicates.notNull())
                .collect(Collectors.toList());
        goalManager.updateGoal(false);
    }

    private Tier getTier(long itemID) {
        MinecraftServer server = APRandomizer.getServer();
        if (server != null && DEFAULT_ITEMS.containsKey(itemID)) {
            ResourceKey<APItem> itemKey = DEFAULT_ITEMS.get(itemID);
            int tierIndex = tiers.getInt(itemKey);
            tiers.put(itemKey, tierIndex + 1);
            Optional<Holder.Reference<APItem>> itemOptional = server.registryAccess().get(itemKey);
            if (itemOptional.isPresent()) {
                APItem item = itemOptional.get().value();
                APTier tier = item.tiers().get(Math.min(item.tiers().size() - 1, tierIndex));
                return new Tier(tier, itemKey, tierIndex);
            } else {
                LOGGER.error("{} not found", DEFAULT_ITEMS.get(itemID));
            }
        }
        return null;
    }

    public void giveItem(APTier tier, ResourceKey<APItem> key, ServerPlayer player, int itemIndex, int tierIndex) {
        if (APRandomizer.isJailPlayers()) {
            //don't send items to players if game has not started.
            return;
        }

        APPlayerAttachment attachment = player.getData(APAttachmentTypes.AP_PLAYER);

        if (attachment.getIndex() >= itemIndex)
            return;

        APCriteriaTriggers.RECEIVED_ITEM.get().trigger(player, key, tierIndex + 1);

        //update the player's index of received items for syncing later.
        attachment.setIndex(itemIndex);
        for (APReward reward : tier.rewards()) {
            reward.give(player);
        }

/*        if (trapData.containsKey(itemID)) {
            try {
                trapData.get(itemID).call().trigger(player);
            } catch (Exception ignored) {
           }
        }
*/
    }


    public boolean giveItemToAll(long itemID, int index) {

        if (index <= this.index)
            return false;
        this.index = index;
        Tier tier = getTier(itemID);
        if (tier != null) {
            receivedItems.add(tier);
            // Don't fire if we have already received this location
            WorldData worldData = APRandomizer.getWorldData();
            if (worldData == null)
                return false;
            if (index <= worldData.getItemIndex())
                return false;

            for (APReward reward : tier.tier.rewards()) {
                reward.give(server);
            }
            for (ServerPlayer serverPlayerEntity : server.getPlayerList().getPlayers()) {
                giveItem(tier.tier, tier.key, serverPlayerEntity, index, tier.tierIndex);
            }
            worldData.setItemIndex(index);
        } else {
            return false;
        }

        goalManager.updateGoal(true);
        return true;
    }

    /***
     fetches the index form the player's capability then makes sure they have all items after that index.
     * @param player ServerPlayer to catch up
     */
    public void catchUpPlayer(ServerPlayer player) {
        grantAllInitialRecipes(player);
        int playerIndex = player.getData(APAttachmentTypes.AP_PLAYER).getIndex();

        for (int i = playerIndex; i < receivedItems.size(); i++) {
            Tier tier = receivedItems.get(i);
            giveItem(tier.tier, tier.key, player, i + 1, tier.tierIndex);
        }
    }

    public void catchUp(MinecraftServer server) {
        WorldData data = APRandomizer.getWorldData();
        if (data != null) {
            for (int i = data.getItemIndex(); i < receivedItems.size(); i++) {
                for (APReward reward : receivedItems.get(i).tier.rewards()) {
                    reward.give(server);
                }
            }
            data.setItemIndex(receivedItems.size());
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                catchUpPlayer(player);
            }
        }
    }

    public Set<ResourceKey<Recipe<?>>> getLockedRecipes(RegistryAccess registryAccess) {
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
        lockedRecipes.removeAll(worldData.getUnlockedRecipes());
        return lockedRecipes;
    }

    public void grantAllInitialRecipes(ServerPlayer player) {
        if (player.getServer() == null) return;
        Set<ResourceKey<Recipe<?>>> lockedRecipes = getLockedRecipes(player.registryAccess());
        Set<RecipeHolder<?>> recipes = player.getServer().getRecipeManager().getRecipes().stream().filter(recipe -> !lockedRecipes.contains(recipe.id())).collect(Collectors.toSet());
        player.awardRecipes(recipes);
    }

    public static void updateCompassLocation(CompassReward compassReward, ServerPlayer player, ItemStack compass) {
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
        if (player.level().dimension().equals(world)) {
            structurePos = player.level().findNearestMapStructure(compassReward.structures(), player.blockPosition(), 75, false);
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
        CompoundTag nbt = compass.has(DataComponents.CUSTOM_DATA) ? compass.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag() : new CompoundTag();
        nbt.store("structure", CompassReward.CODEC, player.registryAccess().createSerializationContext(NbtOps.INSTANCE), compassReward);
        compass.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));

        //update the nbt data with our new structure.
        compass.set(DataComponents.LODESTONE_TRACKER, new LodestoneTracker(Optional.of(GlobalPos.of(world, structurePos)), false));
        Utils.addLodestoneTags(world,structurePos,compass);
        Utils.setNameAndLore(compass, displayName, lore);
        player.containerMenu.broadcastChanges();
    }

    // remove all compasses but one in player inventory
    public static void cleanCompasses(ServerPlayer player) {
        AtomicInteger cCount = new AtomicInteger();
        player.getInventory().forEach(item -> {
            if (!item.has(DataComponents.CUSTOM_DATA)) return;
            cCount.getAndIncrement();
            if (cCount.get() >1){
            player.getInventory().removeItem(item);
            }
        });
    }

    // refresh all compasses in player inventory
    public static void refreshCompasses(ServerPlayer player) {
        player.getInventory().forEach((item) -> {
            if (!item.has(DataComponents.CUSTOM_DATA)) return;
            CompoundTag nbt = item.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
            Optional<CompassReward> structure = nbt.read("structure", CompassReward.CODEC, player.registryAccess().createSerializationContext(NbtOps.INSTANCE));
            if (structure.isEmpty()) return;

            updateCompassLocation(structure.get(), player, item);
        });
    }

    private record Tier(APTier tier, ResourceKey<APItem> key, int tierIndex) {

    }
}
