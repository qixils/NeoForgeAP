package gg.archipelago.aprandomizer.managers.advancementmanager;

import gg.archipelago.aprandomizer.APRegistries;
import gg.archipelago.aprandomizer.ap.APClient;
import gg.archipelago.aprandomizer.data.WorldData;
import gg.archipelago.aprandomizer.locations.APLocation;
import gg.archipelago.aprandomizer.locations.APLocations;
import gg.archipelago.aprandomizer.managers.GoalManager;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;

import static gg.archipelago.aprandomizer.APRandomizer.*;

public class AdvancementManager {

    private static final Object2LongMap<ResourceKey<APLocation>> DEFAULT_LOCATIONS = Util.make(new Object2LongOpenHashMap<>(), map -> {
        map.put(APLocations.VANILLA_NETHER_OBTAIN_CRYING_OBSIDIAN, 1L);
        map.put(APLocations.VANILLA_NETHER_DISTRACT_PIGLIN, 2L);
        map.put(APLocations.VANILLA_STORY_OBTAIN_ARMOR, 3L);
        map.put(APLocations.VANILLA_ADVENTURE_VERY_VERY_FRIGHTENING, 4L);
        map.put(APLocations.VANILLA_STORY_LAVA_BUCKET, 5L);
        map.put(APLocations.VANILLA_END_KILL_DRAGON, 6L);
        map.put(APLocations.VANILLA_NETHER_ALL_POTIONS, 7L);
        map.put(APLocations.VANILLA_HUSBANDRY_TAME_AN_ANIMAL, 8L);
        map.put(APLocations.VANILLA_NETHER_CREATE_BEACON, 9L);
        map.put(APLocations.VANILLA_STORY_DEFLECT_ARROW, 10L);
        map.put(APLocations.VANILLA_STORY_IRON_TOOLS, 11L);
        map.put(APLocations.VANILLA_NETHER_BREW_POTION, 12L);
        map.put(APLocations.VANILLA_END_DRAGON_EGG, 13L);
        map.put(APLocations.VANILLA_HUSBANDRY_FISHY_BUSINESS, 14L);
        map.put(APLocations.VANILLA_NETHER_EXPLORE_NETHER, 15L);
        map.put(APLocations.VANILLA_NETHER_RIDE_STRIDER, 16L);
        map.put(APLocations.VANILLA_ADVENTURE_SNIPER_DUEL, 17L);
        map.put(APLocations.VANILLA_NETHER_ROOT, 18L);
        map.put(APLocations.VANILLA_END_LEVITATE, 19L);
        map.put(APLocations.VANILLA_NETHER_ALL_EFFECTS, 20L);
        map.put(APLocations.VANILLA_ADVENTURE_BULLSEYE, 21L);
        map.put(APLocations.VANILLA_NETHER_GET_WITHER_SKULL, 22L);
        map.put(APLocations.VANILLA_HUSBANDRY_BRED_ALL_ANIMALS, 23L);
        map.put(APLocations.VANILLA_STORY_MINE_STONE, 24L);
        map.put(APLocations.VANILLA_ADVENTURE_TWO_BIRDS_ONE_ARROW, 25L);
        map.put(APLocations.VANILLA_STORY_ENTER_THE_NETHER, 26L);
        map.put(APLocations.VANILLA_ADVENTURE_WHOS_THE_PILLAGER_NOW, 27L);
        map.put(APLocations.VANILLA_STORY_UPGRADE_TOOLS, 28L);
        map.put(APLocations.VANILLA_HUSBANDRY_TACTICAL_FISHING, 29L);
        map.put(APLocations.VANILLA_STORY_CURE_ZOMBIE_VILLAGER, 30L);
        map.put(APLocations.VANILLA_END_FIND_END_CITY, 31L);
        map.put(APLocations.VANILLA_STORY_FORM_OBSIDIAN, 32L);
        map.put(APLocations.VANILLA_END_ENTER_END_GATEWAY, 33L);
        map.put(APLocations.VANILLA_NETHER_OBTAIN_BLAZE_ROD, 34L);
        map.put(APLocations.VANILLA_NETHER_LOOT_BASTION, 35L);
        map.put(APLocations.VANILLA_ADVENTURE_SHOOT_ARROW, 36L);
        map.put(APLocations.VANILLA_HUSBANDRY_SILK_TOUCH_NEST, 37L);
        map.put(APLocations.VANILLA_ADVENTURE_ARBALISTIC, 38L);
        map.put(APLocations.VANILLA_END_RESPAWN_DRAGON, 39L);
        map.put(APLocations.VANILLA_STORY_SMELT_IRON, 40L);
        map.put(APLocations.VANILLA_NETHER_CHARGE_RESPAWN_ANCHOR, 41L);
        map.put(APLocations.VANILLA_STORY_SHINY_GEAR, 42L);
        map.put(APLocations.VANILLA_END_ELYTRA, 43L);
        map.put(APLocations.VANILLA_ADVENTURE_SUMMON_IRON_GOLEM, 44L);
        map.put(APLocations.VANILLA_NETHER_RETURN_TO_SENDER, 45L);
        map.put(APLocations.VANILLA_ADVENTURE_SLEEP_IN_BED, 46L);
        map.put(APLocations.VANILLA_END_DRAGON_BREATH, 47L);
        map.put(APLocations.VANILLA_ADVENTURE_ROOT, 48L);
        map.put(APLocations.VANILLA_ADVENTURE_KILL_ALL_MOBS, 49L);
        map.put(APLocations.VANILLA_STORY_ENCHANT_ITEM, 50L);
        map.put(APLocations.VANILLA_ADVENTURE_VOLUNTARY_EXILE, 51L);
        map.put(APLocations.VANILLA_STORY_FOLLOW_ENDER_EYE, 52L);
        map.put(APLocations.VANILLA_END_ROOT, 53L);
        map.put(APLocations.VANILLA_HUSBANDRY_OBTAIN_NETHERITE_HOE, 54L);
        map.put(APLocations.VANILLA_ADVENTURE_TOTEM_OF_UNDYING, 55L);
        map.put(APLocations.VANILLA_ADVENTURE_KILL_A_MOB, 56L);
        map.put(APLocations.VANILLA_ADVENTURE_ADVENTURING_TIME, 57L);
        map.put(APLocations.VANILLA_HUSBANDRY_PLANT_SEED, 58L);
        map.put(APLocations.VANILLA_NETHER_FIND_BASTION, 59L);
        map.put(APLocations.VANILLA_ADVENTURE_HERO_OF_THE_VILLAGE, 60L);
        map.put(APLocations.VANILLA_NETHER_OBTAIN_ANCIENT_DEBRIS, 61L);
        map.put(APLocations.VANILLA_NETHER_CREATE_FULL_BEACON, 62L);
        map.put(APLocations.VANILLA_NETHER_SUMMON_WITHER, 63L);
        map.put(APLocations.VANILLA_HUSBANDRY_BALANCED_DIET, 64L);
        map.put(APLocations.VANILLA_NETHER_FAST_TRAVEL, 65L);
        map.put(APLocations.VANILLA_HUSBANDRY_ROOT, 66L);
        map.put(APLocations.VANILLA_ADVENTURE_USE_LODESTONE, 67L);
        map.put(APLocations.VANILLA_HUSBANDRY_SAFELY_HARVEST_HONEY, 68L);
        map.put(APLocations.VANILLA_ADVENTURE_TRADE, 69L);
        map.put(APLocations.VANILLA_NETHER_UNEASY_ALLIANCE, 70L);
        map.put(APLocations.VANILLA_STORY_MINE_DIAMOND, 71L);
        map.put(APLocations.VANILLA_NETHER_FIND_FORTRESS, 72L);
        map.put(APLocations.VANILLA_ADVENTURE_THROW_TRIDENT, 73L);
        map.put(APLocations.VANILLA_STORY_ROOT, 74L);
        map.put(APLocations.VANILLA_ADVENTURE_HONEY_BLOCK_SLIDE, 75L);
        map.put(APLocations.VANILLA_ADVENTURE_OL_BETSY, 76L);
        map.put(APLocations.VANILLA_NETHER_NETHERITE_ARMOR, 77L);
        map.put(APLocations.VANILLA_STORY_ENTER_THE_END, 78L);
        map.put(APLocations.VANILLA_HUSBANDRY_BREED_AN_ANIMAL, 79L);
        map.put(APLocations.VANILLA_HUSBANDRY_COMPLETE_CATALOGUE, 80L);
        map.put(APLocations.ARCHIPELAGO_GET_WOOD, 81L);
        map.put(APLocations.ARCHIPELAGO_GET_PICKAXE, 82L);
        map.put(APLocations.ARCHIPELAGO_HOT_TOPIC, 83L);
        map.put(APLocations.ARCHIPELAGO_BAKE_BREAD, 84L);
        map.put(APLocations.ARCHIPELAGO_THE_LIE, 85L);
        map.put(APLocations.ARCHIPELAGO_RIDE_MINECART, 86L);
        map.put(APLocations.ARCHIPELAGO_CRAFT_SWORD, 87L);
        map.put(APLocations.ARCHIPELAGO_COW_TIPPER, 88L);
        map.put(APLocations.ARCHIPELAGO_RIDE_PIG, 89L);
        map.put(APLocations.ARCHIPELAGO_OVERKILL, 90L);
        map.put(APLocations.ARCHIPELAGO_OBTAIN_BOOKSHELF, 91L);
        map.put(APLocations.ARCHIPELAGO_OVERPOWERED, 92L);
        map.put(APLocations.VANILLA_HUSBANDRY_WAX_ON, 93L);
        map.put(APLocations.VANILLA_HUSBANDRY_WAX_OFF, 94L);
        map.put(APLocations.VANILLA_HUSBANDRY_AXOLOTL_IN_A_BUCKET, 95L);
        map.put(APLocations.VANILLA_HUSBANDRY_KILL_AXOLOTL_TARGET, 96L);
        map.put(APLocations.VANILLA_ADVENTURE_SPYGLASS_AT_PARROT, 97L);
        map.put(APLocations.VANILLA_ADVENTURE_SPYGLASS_AT_GHAST, 98L);
        map.put(APLocations.VANILLA_ADVENTURE_SPYGLASS_AT_DRAGON, 99L);
        map.put(APLocations.VANILLA_ADVENTURE_LIGHTNING_ROD_WITH_VILLAGER_NO_FIRE, 100L);
        map.put(APLocations.VANILLA_ADVENTURE_WALK_ON_POWDER_SNOW_WITH_LEATHER_BOOTS, 101L);
        map.put(APLocations.VANILLA_HUSBANDRY_MAKE_A_SIGN_GLOW, 102L);
        map.put(APLocations.VANILLA_HUSBANDRY_RIDE_A_BOAT_WITH_A_GOAT, 103L);
        map.put(APLocations.VANILLA_ADVENTURE_FALL_FROM_WORLD_HEIGHT, 104L);
        map.put(APLocations.VANILLA_NETHER_RIDE_STRIDER_IN_OVERWORLD_LAVA, 105L);
        map.put(APLocations.VANILLA_ADVENTURE_PLAY_JUKEBOX_IN_MEADOWS, 106L);
        map.put(APLocations.VANILLA_ADVENTURE_TRADE_AT_WORLD_HEIGHT, 107L);
        // 1.19 advancements
        map.put(APLocations.VANILLA_HUSBANDRY_ALLAY_DELIVER_CAKE_TO_NOTE_BLOCK, 108L);
        map.put(APLocations.VANILLA_HUSBANDRY_TADPOLE_IN_A_BUCKET, 109L);
        map.put(APLocations.VANILLA_ADVENTURE_KILL_MOB_NEAR_SCULK_CATALYST, 110L);
        map.put(APLocations.VANILLA_ADVENTURE_AVOID_VIBRATION, 111L);
        map.put(APLocations.VANILLA_HUSBANDRY_LEASH_ALL_FROG_VARIANTS, 112L);
        map.put(APLocations.VANILLA_HUSBANDRY_FROGLIGHTS, 113L);
        map.put(APLocations.VANILLA_HUSBANDRY_ALLAY_DELIVER_ITEM_TO_PLAYER, 114L);
        // 1.20 advancements
        map.put(APLocations.VANILLA_HUSBANDRY_OBTAIN_SNIFFER_EGG, 115L);
        map.put(APLocations.VANILLA_HUSBANDRY_FEED_SNIFFLET, 116L); //
        map.put(APLocations.VANILLA_HUSBANDRY_PLANT_ANY_SNIFFER_SEED, 117L);
        map.put(APLocations.VANILLA_ADVENTURE_TRIM_WITH_ANY_ARMOR_PATTERN, 118L);
        map.put(APLocations.VANILLA_ADVENTURE_TRIM_WITH_ALL_EXCLUSIVE_ARMOR_PATTERNS, 119L);
        map.put(APLocations.VANILLA_ADVENTURE_SALVAGE_SHERD, 120L);
        map.put(APLocations.VANILLA_ADVENTURE_CRAFT_DECORATED_POT_USING_ONLY_SHERDS, 121L);
        map.put(APLocations.VANILLA_ADVENTURE_READ_POWER_OF_CHISELED_BOOKSHELF, 122L);
        map.put(APLocations.VANILLA_ADVENTURE_BRUSH_ARMADILLO, 123L);
        map.put(APLocations.VANILLA_HUSBANDRY_REMOVE_WOLF_ARMOR, 124L);
        map.put(APLocations.VANILLA_HUSBANDRY_REPAIR_WOLF_ARMOR, 125L);
        map.put(APLocations.VANILLA_HUSBANDRY_WHOLE_PACK, 126L);
        // 1.21 advancements
        map.put(APLocations.VANILLA_ADVENTURE_MINECRAFT_TRIALS_EDITION, 127L);
        map.put(APLocations.VANILLA_ADVENTURE_UNDER_LOCK_AND_KEY, 128L);
        map.put(APLocations.VANILLA_ADVENTURE_BLOWBACK, 129L);
        map.put(APLocations.VANILLA_ADVENTURE_WHO_NEEDS_ROCKETS, 130L);
        map.put(APLocations.VANILLA_ADVENTURE_CRAFTERS_CRAFTING_CRAFTERS, 131L);
        map.put(APLocations.VANILLA_ADVENTURE_LIGHTEN_UP, 132L);
        map.put(APLocations.VANILLA_ADVENTURE_OVEROVERKILL, 133L);
        map.put(APLocations.VANILLA_ADVENTURE_REVAULTING, 134L);
        // 1.21.6 advancements
        map.put(APLocations.VANILLA_HUSBANDRY_PLACE_DRIED_GHAST_IN_WATER, 135L);
        map.put(APLocations.VANILLA_ADVENTURE_HEART_TRANSPLANTER, 136L);
    });

    private final LongSet earnedAdvancements = new LongOpenHashSet();

    private final WorldData worldData;
    public AdvancementManager(WorldData worldData) {
        this.worldData = worldData;
    }

    public long getAdvancementID(ResourceKey<APLocation> namespacedID) {
        return DEFAULT_LOCATIONS.getLong(namespacedID);
    }

    public boolean hasAdvancement(long id) {
        return earnedAdvancements.contains(id);
    }

    public boolean hasAdvancement(ResourceKey<APLocation> namespacedID) {
        return earnedAdvancements.contains(getAdvancementID(namespacedID));
    }

    public void addAdvancement(long id) {
        APClient apClient = getAP();
        if (apClient == null) return;
        GoalManager goalManager = getGoalManager();
        if (goalManager == null) return;
        earnedAdvancements.add(id);
        apClient.checkLocation(id);
        goalManager.updateGoal(true);
        worldData.addLocation(id);
        syncAllAdvancements();
    }

    public void addAdvancement(ResourceKey<APLocation> key) {
        if (DEFAULT_LOCATIONS.containsKey(key)) {
            addAdvancement(DEFAULT_LOCATIONS.getLong(key));
        }
    }

    public void resendAdvancements() {
        APClient apClient = getAP(); // TODO
        if (apClient == null) return;
        for (long earnedAdvancement : earnedAdvancements) {
            apClient.checkLocation(earnedAdvancement);
        }
    }

    public void syncAdvancement(ResourceKey<APLocation> key, APLocation location) {
        if (server == null) return;
        if (!hasAdvancement(key)) return;
        // TODO: is the 2nd arg necessary? doesnt hurt?
        for (ServerPlayer serverPlayerEntity : server.getPlayerList().getPlayers()) {
            location.give(serverPlayerEntity);
        }
    }

    public void syncAllAdvancements() {
        if (server == null) return;
        Registry<APLocation> locationRegistry = server.registryAccess().lookupOrThrow(APRegistries.ARCHIPELAGO_LOCATION);
        for (var a : locationRegistry.registryKeySet()) {
            syncAdvancement(a, locationRegistry.getValueOrThrow(a));
        }
    }

    public int getFinishedAmount() {
        return earnedAdvancements.size();
    }

    public void setCheckedAdvancements(LongSet checkedLocations) {
        if(worldData == null) return;
        earnedAdvancements.addAll(checkedLocations);
        for (var checkedLocation : checkedLocations) {
            worldData.addLocation(checkedLocation);
        }
        //checkedLocations.forEach(worldData::addLocation);
        syncAllAdvancements();
    }
}
