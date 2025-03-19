package gg.archipelago.aprandomizer.managers.itemmanager;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import gg.archipelago.aprandomizer.managers.itemmanager.traps.*;
import gg.archipelago.aprandomizer.tags.APStructureTags;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

public class ItemManager {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public static final long DRAGON_EGG_SHARD = 45043L;

    private final HashMap<Long, ItemStack> itemStacks = new HashMap<>() {{
        put(45015L, new ItemStack(Items.NETHERITE_SCRAP, 8));
        put(45016L, new ItemStack(Items.EMERALD, 8));
        put(45017L, new ItemStack(Items.EMERALD, 4));

//        put(45018L, EnchantmentHelper.createBook(new EnchantmentInstance(Enchantments.CHANNELING, 1)));
//        put(45019L, EnchantmentHelper.createBook(new EnchantmentInstance(Enchantments.SILK_TOUCH, 1)));
//        put(45020L, EnchantmentHelper.createBook(new EnchantmentInstance(Enchantments.SHARPNESS, 3)));
//        put(45021L, EnchantmentHelper.createBook(new EnchantmentInstance(Enchantments.PIERCING, 4)));
//        put(45022L, EnchantmentHelper.createBook(new EnchantmentInstance(Enchantments.MOB_LOOTING, 3)));
//        put(45023L, EnchantmentHelper.createBook(new EnchantmentInstance(Enchantments.INFINITY_ARROWS, 1)));


        put(45024L, new ItemStack(Items.DIAMOND_ORE, 4));
        put(45025L, new ItemStack(Items.IRON_ORE, 16));
        put(45029L, new ItemStack(Items.ENDER_PEARL, 3));
        put(45004L, new ItemStack(Items.LAPIS_LAZULI, 4));
        put(45030L, new ItemStack(Items.LAPIS_LAZULI, 4));
        put(45031L, new ItemStack(Items.COOKED_PORKCHOP, 16));
        put(45032L, new ItemStack(Items.GOLD_ORE, 8));
        put(45033L, new ItemStack(Items.ROTTEN_FLESH, 8));
        var arrow = new ItemStack(Items.ARROW, 1);
        Utils.setItemName(arrow, "The Arrow");
        put(45034L, arrow);
        put(45035L, new ItemStack(Items.ARROW, 32));
        put(45036L, new ItemStack(Items.SADDLE, 1));

        ItemStack villageCompass = new ItemStack(Items.COMPASS, 1);
            makeCompass(villageCompass, APStructureTags.VILLAGE);
        put(45037L, villageCompass);

        ItemStack outpostCompass = new ItemStack(Items.COMPASS, 1);
            makeCompass(outpostCompass, APStructureTags.PILLAGER_OUTPOST);
        put(45038L, outpostCompass);

        ItemStack fortressCompass = new ItemStack(Items.COMPASS, 1);
            makeCompass(fortressCompass, APStructureTags.FORTRESS);
        put(45039L, fortressCompass);

        ItemStack bastionCompass = new ItemStack(Items.COMPASS, 1);
            makeCompass(bastionCompass, APStructureTags.BASTION_REMNANT);
        put(45040L,bastionCompass);

        ItemStack endCityCompass = new ItemStack(Items.COMPASS, 1);
            makeCompass(endCityCompass, APStructureTags.END_CITY);
        put(45041L, endCityCompass);

        put(45042L, new ItemStack(Items.SHULKER_BOX, 1));
    }};

    private final HashMap<Long,TagKey<Structure>> compasses = new HashMap<>() {{
            put(45037L, APStructureTags.VILLAGE);
            put(45038L, APStructureTags.PILLAGER_OUTPOST);
            put(45039L, APStructureTags.FORTRESS);
            put(45040L, APStructureTags.BASTION_REMNANT);
            put(45041L, APStructureTags.END_CITY);

    }};

    private final HashMap<Long, Integer> xpData = new HashMap<>() {{
        put(45026L, 500);
        put(45027L, 100);
        put(45028L, 50);
    }};

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

    private ArrayList<Long> receivedItems = new ArrayList<>();

    private final ArrayList<TagKey<Structure>> receivedCompasses = new ArrayList<>();

    private void makeCompass(ItemStack iStack, TagKey<Structure> structureTag) {
        CompoundTag nbt = iStack.has(DataComponents.CUSTOM_DATA) ? iStack.get(DataComponents.CUSTOM_DATA).copyTag() : new CompoundTag();
        nbt.put("structure", StringTag.valueOf(structureTag.location().toString()));
        iStack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));

        GlobalPos structureCords = GlobalPos.of(Utils.getStructureWorld(structureTag), new BlockPos(0, 0, 0));
        iStack.set(DataComponents.LODESTONE_TRACKER, new LodestoneTracker(Optional.of(structureCords), false));
        Utils.setItemName(iStack, "uninitialized structure compass");
        Utils.setItemLore(iStack, List.of(
                "oops, this compass is broken.",
                "right click with compass in hand to fix.",
                "hopefully it will point to the right place.",
                "no guarantees."));
    }

    public void setReceivedItems(ArrayList<Long> items) {
        this.receivedItems = items;
        for (var item : items) {
            if(compasses.containsKey(item) && !receivedCompasses.contains(compasses.get(item))) {
                receivedCompasses.add(compasses.get(item));
            }
        }
        APRandomizer.getGoalManager().updateGoal(false);
    }

        public void giveItem(Long itemID, ServerPlayer player, int itemIndex) {
        if (APRandomizer.isJailPlayers()) {
            //dont send items to players if game has not started.
            return;
        }

        if (APRandomizer.getWorldData().getPlayerIndex(player.getStringUUID()) >= itemIndex) return;

        //update the player's index of received items for syncing later.
        APRandomizer.getWorldData().updatePlayerIndex(player.getStringUUID(),itemIndex);

        if (itemStacks.containsKey(itemID)) {
            ItemStack itemstack = itemStacks.get(itemID).copy();
            if(compasses.containsKey(itemID)){

                TagKey<Structure> tag = compasses.get(itemID);
                updateCompassLocation(tag, player , itemstack);
            }
            Utils.giveItemToPlayer(player, itemstack);
        } else if (xpData.containsKey(itemID)) {
            int xpValue = xpData.get(itemID);
            player.giveExperiencePoints(xpValue);
        } else if (trapData.containsKey(itemID)) {
            try {
                trapData.get(itemID).call().trigger(player);
            } catch (Exception ignored) {
            }
        }
    }


    public void giveItemToAll(long itemID, int index) {

        receivedItems.add(itemID);
        //check if this item is a structure compass, and we are not already tracking that one.
        if(compasses.containsKey(itemID) && !receivedCompasses.contains(compasses.get(itemID))) {
            receivedCompasses.add(compasses.get(itemID));
        }

        APRandomizer.getServer().execute(() -> {
            for (ServerPlayer serverplayerentity : APRandomizer.getServer().getPlayerList().getPlayers()) {
                giveItem(itemID, serverplayerentity, index);
            }
        });

        APRandomizer.getGoalManager().updateGoal(true);
    }

    /***
     fetches the index form the player's capability then makes sure they have all items after that index.
     * @param player ServerPlayer to catch up
     */
    public void catchUpPlayer(ServerPlayer player) {
        int playerIndex = APRandomizer.getWorldData().getPlayerIndex(player.getStringUUID());

        for (int i = playerIndex; i < receivedItems.size(); i++) {
            giveItem(receivedItems.get(i), player, i+1);
        }

    }

    public ArrayList<TagKey<Structure>> getCompasses() {
        return receivedCompasses;
    }

    public ArrayList<Long> getAllItems() {
        return receivedItems;
    }

    public static void updateCompassLocation(TagKey<Structure> structureTag, Player player, ItemStack compass) {

        //get the actual structure data from forge, and make sure its changed to the AP one if needed.

        //get our local custom structure if needed.
        ResourceKey<Level> world = Utils.getStructureWorld(structureTag);

        //only locate structure if the player is in the same world as the one for the compass
        //otherwise just point it to 0,0 in said dimension.
        BlockPos structurePos = new BlockPos(0,0,0);
        List<String> lore = List.of(
                "Right click with compass in hand to",
                "cycle though unlocked compasses.");
        var displayName = Component.literal(String.format("Structure Compass (%s)", Utils.getAPStructureName(structureTag)));
        if(player.getCommandSenderWorld().dimension().equals(world)) {
            structurePos = APRandomizer.getServer().getLevel(world).findNearestMapStructure(structureTag, player.blockPosition(), 75, false);
            if (structurePos != null) {
                lore.add(0,"Location X: " + structurePos.getX() + ", Z: " + structurePos.getZ());
            } else {
                player.displayClientMessage(Component.literal("Could not find a nearby " + Utils.getAPStructureName(structureTag)), false);
                displayName = Component.literal(
                        String.format("Structure Compass (%s) Not Found",
                                Utils.getAPStructureName(structureTag))
                ).withStyle(ChatFormatting.YELLOW);
            }
        }
        else {
            displayName = Component.literal(
                    String.format("Structure Compass (%s) Wrong Dimension",
                            Utils.getAPStructureName(structureTag))
                    ).withStyle(ChatFormatting.DARK_RED);
        }

        if(structurePos == null)
            structurePos = new BlockPos(0,0,0);
        //update the nbt data with our new structure.
        CompoundTag nbt = compass.has(DataComponents.CUSTOM_DATA) ? compass.get(DataComponents.CUSTOM_DATA).copyTag() : new CompoundTag();
        nbt.put("structure", StringTag.valueOf(structureTag.location().toString()));
        compass.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));

        //update the nbt data with our new structure.
        nbt.put("structure", StringTag.valueOf(structureTag.location().toString()));
        compass.set(DataComponents.LODESTONE_TRACKER, new LodestoneTracker(Optional.of(GlobalPos.of(world, structurePos)), false));
        Utils.setNameAndLore(compass, displayName, lore);
    }

        // refresh all compasses in player inventory
    public static void refreshCompasses(ServerPlayer player) {
        player.getInventory().items.forEach( (item) -> {
            if(item.getItem().equals(Items.COMPASS)) {
                if (!item.has(DataComponents.CUSTOM_DATA))
                    return;
                CompoundTag nbt = item.get(DataComponents.CUSTOM_DATA).copyTag();
                if(nbt.get("structure") == null)
                    return;

                try {
                    TagKey<Structure> tagKey = TagKey.create(Registries.STRUCTURE, ResourceLocation.tryParse(nbt.getString("structure")));
                    updateCompassLocation(tagKey, player, item);
                } catch (Exception ignored) {}
            }
        });
    }
}
