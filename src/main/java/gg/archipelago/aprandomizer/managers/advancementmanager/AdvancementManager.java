package gg.archipelago.aprandomizer.managers.advancementmanager;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.APRegistries;
import gg.archipelago.aprandomizer.locations.APLocation;
import gg.archipelago.aprandomizer.locations.APLocations;
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
        map.put(APLocations.VANILLA_NETHER_OBTAIN_CRYING_OBSIDIAN, 42000L);
        map.put(APLocations.VANILLA_NETHER_DISTRACT_PIGLIN, 42001L);
        map.put(APLocations.VANILLA_STORY_OBTAIN_ARMOR, 42002L);
        map.put(APLocations.VANILLA_ADVENTURE_VERY_VERY_FRIGHTENING, 42003L);
        map.put(APLocations.VANILLA_STORY_LAVA_BUCKET, 42004L);
        map.put(APLocations.VANILLA_END_KILL_DRAGON, 42005L);
        map.put(APLocations.VANILLA_NETHER_ALL_POTIONS, 42006L);
        map.put(APLocations.VANILLA_HUSBANDRY_TAME_AN_ANIMAL, 42007L);
        map.put(APLocations.VANILLA_NETHER_CREATE_BEACON, 42008L);
        map.put(APLocations.VANILLA_STORY_DEFLECT_ARROW, 42009L);
        map.put(APLocations.VANILLA_STORY_IRON_TOOLS, 42010L);
        map.put(APLocations.VANILLA_NETHER_BREW_POTION, 42011L);
        map.put(APLocations.VANILLA_END_DRAGON_EGG, 42012L);
        map.put(APLocations.VANILLA_HUSBANDRY_FISHY_BUSINESS, 42013L);
        map.put(APLocations.VANILLA_NETHER_EXPLORE_NETHER, 42014L);
        map.put(APLocations.VANILLA_NETHER_RIDE_STRIDER, 42015L);
        map.put(APLocations.VANILLA_ADVENTURE_SNIPER_DUEL, 42016L);
        map.put(APLocations.VANILLA_NETHER_ROOT, 42017L);
        map.put(APLocations.VANILLA_END_LEVITATE, 42018L);
        map.put(APLocations.VANILLA_NETHER_ALL_EFFECTS, 42019L);
        map.put(APLocations.VANILLA_ADVENTURE_BULLSEYE, 42020L);
        map.put(APLocations.VANILLA_NETHER_GET_WITHER_SKULL, 42021L);
        map.put(APLocations.VANILLA_HUSBANDRY_BRED_ALL_ANIMALS, 42022L);
        map.put(APLocations.VANILLA_STORY_MINE_STONE, 42023L);
        map.put(APLocations.VANILLA_ADVENTURE_TWO_BIRDS_ONE_ARROW, 42024L);
        map.put(APLocations.VANILLA_STORY_ENTER_THE_NETHER, 42025L);
        map.put(APLocations.VANILLA_ADVENTURE_WHOS_THE_PILLAGER_NOW, 42026L);
        map.put(APLocations.VANILLA_STORY_UPGRADE_TOOLS, 42027L);
        map.put(APLocations.VANILLA_HUSBANDRY_TACTICAL_FISHING, 42028L);
        map.put(APLocations.VANILLA_STORY_CURE_ZOMBIE_VILLAGER, 42029L);
        map.put(APLocations.VANILLA_END_FIND_END_CITY, 42030L);
        map.put(APLocations.VANILLA_STORY_FORM_OBSIDIAN, 42031L);
        map.put(APLocations.VANILLA_END_ENTER_END_GATEWAY, 42032L);
        map.put(APLocations.VANILLA_NETHER_OBTAIN_BLAZE_ROD, 42033L);
        map.put(APLocations.VANILLA_NETHER_LOOT_BASTION, 42034L);
        map.put(APLocations.VANILLA_ADVENTURE_SHOOT_ARROW, 42035L);
        map.put(APLocations.VANILLA_HUSBANDRY_SILK_TOUCH_NEST, 42036L);
        map.put(APLocations.VANILLA_ADVENTURE_ARBALISTIC, 42037L);
        map.put(APLocations.VANILLA_END_RESPAWN_DRAGON, 42038L);
        map.put(APLocations.VANILLA_STORY_SMELT_IRON, 42039L);
        map.put(APLocations.VANILLA_NETHER_CHARGE_RESPAWN_ANCHOR, 42040L);
        map.put(APLocations.VANILLA_STORY_SHINY_GEAR, 42041L);
        map.put(APLocations.VANILLA_END_ELYTRA, 42042L);
        map.put(APLocations.VANILLA_ADVENTURE_SUMMON_IRON_GOLEM, 42043L);
        map.put(APLocations.VANILLA_NETHER_RETURN_TO_SENDER, 42044L);
        map.put(APLocations.VANILLA_ADVENTURE_SLEEP_IN_BED, 42045L);
        map.put(APLocations.VANILLA_END_DRAGON_BREATH, 42046L);
        map.put(APLocations.VANILLA_ADVENTURE_ROOT, 42047L);
        map.put(APLocations.VANILLA_ADVENTURE_KILL_ALL_MOBS, 42048L);
        map.put(APLocations.VANILLA_STORY_ENCHANT_ITEM, 42049L);
        map.put(APLocations.VANILLA_ADVENTURE_VOLUNTARY_EXILE, 42050L);
        map.put(APLocations.VANILLA_STORY_FOLLOW_ENDER_EYE, 42051L);
        map.put(APLocations.VANILLA_END_ROOT, 42052L);
        map.put(APLocations.VANILLA_HUSBANDRY_OBTAIN_NETHERITE_HOE, 42053L);
        map.put(APLocations.VANILLA_ADVENTURE_TOTEM_OF_UNDYING, 42054L);
        map.put(APLocations.VANILLA_ADVENTURE_KILL_A_MOB, 42055L);
        map.put(APLocations.VANILLA_ADVENTURE_ADVENTURING_TIME, 42056L);
        map.put(APLocations.VANILLA_HUSBANDRY_PLANT_SEED, 42057L);
        map.put(APLocations.VANILLA_NETHER_FIND_BASTION, 42058L);
        map.put(APLocations.VANILLA_ADVENTURE_HERO_OF_THE_VILLAGE, 42059L);
        map.put(APLocations.VANILLA_NETHER_OBTAIN_ANCIENT_DEBRIS, 42060L);
        map.put(APLocations.VANILLA_NETHER_CREATE_FULL_BEACON, 42061L);
        map.put(APLocations.VANILLA_NETHER_SUMMON_WITHER, 42062L);
        map.put(APLocations.VANILLA_HUSBANDRY_BALANCED_DIET, 42063L);
        map.put(APLocations.VANILLA_NETHER_FAST_TRAVEL, 42064L);
        map.put(APLocations.VANILLA_HUSBANDRY_ROOT, 42065L);
        map.put(APLocations.VANILLA_NETHER_USE_LODESTONE, 42066L);
        map.put(APLocations.VANILLA_HUSBANDRY_SAFELY_HARVEST_HONEY, 42067L);
        map.put(APLocations.VANILLA_ADVENTURE_TRADE, 42068L);
        map.put(APLocations.VANILLA_NETHER_UNEASY_ALLIANCE, 42069L);
        map.put(APLocations.VANILLA_STORY_MINE_DIAMOND, 42070L);
        map.put(APLocations.VANILLA_NETHER_FIND_FORTRESS, 42071L);
        map.put(APLocations.VANILLA_ADVENTURE_THROW_TRIDENT, 42072L);
        map.put(APLocations.VANILLA_STORY_ROOT, 42073L);
        map.put(APLocations.VANILLA_ADVENTURE_HONEY_BLOCK_SLIDE, 42074L);
        map.put(APLocations.VANILLA_ADVENTURE_OL_BETSY, 42075L);
        map.put(APLocations.VANILLA_NETHER_NETHERITE_ARMOR, 42076L);
        map.put(APLocations.VANILLA_STORY_ENTER_THE_END, 42077L);
        map.put(APLocations.VANILLA_HUSBANDRY_BREED_AN_ANIMAL, 42078L);
        map.put(APLocations.VANILLA_HUSBANDRY_COMPLETE_CATALOGUE, 42079L);
        map.put(APLocations.ARCHIPELAGO_GET_WOOD, 42080L);
        map.put(APLocations.ARCHIPELAGO_GET_PICKAXE, 42081L);
        map.put(APLocations.ARCHIPELAGO_HOT_TOPIC, 42082L);
        map.put(APLocations.ARCHIPELAGO_BAKE_BREAD, 42083L);
        map.put(APLocations.ARCHIPELAGO_THE_LIE, 42084L);
        map.put(APLocations.ARCHIPELAGO_RIDE_MINECART, 42085L);
        map.put(APLocations.ARCHIPELAGO_CRAFT_SWORD, 42086L);
        map.put(APLocations.ARCHIPELAGO_COW_TIPPER, 42087L);
        map.put(APLocations.ARCHIPELAGO_RIDE_PIG, 42088L);
        map.put(APLocations.ARCHIPELAGO_OVERKILL, 42089L);
        map.put(APLocations.ARCHIPELAGO_OBTAIN_BOOKSHELF, 42090L);
        map.put(APLocations.ARCHIPELAGO_OVERPOWERED, 42091L);
        map.put(APLocations.VANILLA_HUSBANDRY_WAX_ON, 42092L);
        map.put(APLocations.VANILLA_HUSBANDRY_WAX_OFF, 42093L);
        map.put(APLocations.VANILLA_HUSBANDRY_AXOLOTL_IN_A_BUCKET, 42094L);
        map.put(APLocations.VANILLA_HUSBANDRY_KILL_AXOLOTL_TARGET, 42095L);
        map.put(APLocations.VANILLA_ADVENTURE_SPYGLASS_AT_PARROT, 42096L);
        map.put(APLocations.VANILLA_ADVENTURE_SPYGLASS_AT_GHAST, 42097L);
        map.put(APLocations.VANILLA_ADVENTURE_SPYGLASS_AT_DRAGON, 42098L);
        map.put(APLocations.VANILLA_ADVENTURE_LIGHTNING_ROD_WITH_VILLAGER_NO_FIRE, 42099L);
        map.put(APLocations.VANILLA_ADVENTURE_WALK_ON_POWDER_SNOW_WITH_LEATHER_BOOTS, 42100L);
        map.put(APLocations.VANILLA_HUSBANDRY_MAKE_A_SIGN_GLOW, 42101L);
        map.put(APLocations.VANILLA_HUSBANDRY_RIDE_A_BOAT_WITH_A_GOAT, 42102L);
        map.put(APLocations.VANILLA_ADVENTURE_FALL_FROM_WORLD_HEIGHT, 42103L);
        map.put(APLocations.VANILLA_NETHER_RIDE_STRIDER_IN_OVERWORLD_LAVA, 42104L);
        map.put(APLocations.VANILLA_ADVENTURE_PLAY_JUKEBOX_IN_MEADOWS, 42105L);
        map.put(APLocations.VANILLA_ADVENTURE_TRADE_AT_WORLD_HEIGHT, 42106L);
        // 1.19 advancements
        map.put(APLocations.VANILLA_HUSBANDRY_ALLAY_DELIVER_CAKE_TO_NOTE_BLOCK, 42107L);
        map.put(APLocations.VANILLA_HUSBANDRY_TADPOLE_IN_A_BUCKET, 42108L);
        map.put(APLocations.VANILLA_ADVENTURE_KILL_MOB_NEAR_SCULK_CATALYST, 42109L);
        map.put(APLocations.VANILLA_ADVENTURE_AVOID_VIBRATION, 42110L);
        map.put(APLocations.VANILLA_HUSBANDRY_LEASH_ALL_FROG_VARIANTS, 42111L);
        map.put(APLocations.VANILLA_HUSBANDRY_FROGLIGHTS, 42112L);
        map.put(APLocations.VANILLA_HUSBANDRY_ALLAY_DELIVER_ITEM_TO_PLAYER, 42113L);
        // 1.20 advancements
        // TODO
//        put("minecraft:husbandry/obtain_sniffer_egg", 42114L); // smells interesting
//        put("minecraft:husbandry/feed_snifflet", 42115L); // little sniffs
//        put("husbandry/plant_any_sniffer_seed", 42116L); // planting the past
//        put("minecraft:adventure/trim_with_any_armor_pattern", 42117L); // crafting a new look
//        put("minecraft:adventure/trim_with_all_exclusive_armor_patterns", 42118L); // smithing with style
//        put("minecraft:adventure/salvage_sherd", 42119L); // respecting the remnants
//        put("minecraft:adventure/craft_decorated_pot_using_only_sherds", 42120L); // careful restoration
//        put("minecraft:adventure/read_power_of_chiseled_bookshelf", 42120L); // the power of books
//        put("minecraft:adventure/brush_armadillo", 42122L); // isn't it scute
//        put("minecraft:husbandry/remove_wolf_armor", 42123L); // Shear Brilliance
//        put("minecraft:husbandry/repair_wolf_armor", 42124L); // good as new
//        put("minecraft:husbandry/whole_pack", 42125L); // the whole pack
        // 1.21 advancements
//        put("minecraft:adventure/minecraft_trials_edition", 42126L); // Minecraft: Trial(s) Edition
//        put("minecraft:adventure/under_lock_and_key", 42127L); // Under Lock and Key
//        put("minecraft:adventure/blowback", 42128L); // blowback
//        put("minecraft:adventure/who_needs_rockets", 42129L); // Who needs rockets
//        put("minecraft:adventure/crafters_crafting_crafters", 42130L); // cafters crafting crafters
//        put("minecraft:adventure/lighten_up", 42131L); // lighten up
//        put("minecraft:adventure/overoverkill", 42132L); // over overkill
//        put("minecraft:adventure/revaulting", 42133L); // revaulting
    });

    private final LongSet earnedAdvancements = new LongOpenHashSet();

    public AdvancementManager() {

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
        earnedAdvancements.add(id);
        AP().ifPresent(value -> value.checkLocation(id));
        goalManager().ifPresent(value -> value.updateGoal(true));
        worldData().ifPresent(worldData -> worldData.addLocation(id));
        syncAllAdvancements();
    }

    public void addAdvancement(ResourceKey<APLocation> key) {
        if (DEFAULT_LOCATIONS.containsKey(key)) {
            addAdvancement(DEFAULT_LOCATIONS.getLong(key));
        }
    }

    public void resendAdvancements() {
        gg.archipelago.aprandomizer.ap.APClient apClient = getAP(); // TODO
        if (apClient == null) return;
        for (long earnedAdvancement : earnedAdvancements) {
            apClient.checkLocation(earnedAdvancement);
        }
    }

    public void syncAdvancement(ResourceKey<APLocation> key, APLocation location) {
        if (server == null) return;
        if (!hasAdvancement(key)) return;
        // TODO: is the 2nd arg necessary? doesnt hurt?
        for (ServerPlayer serverPlayerEntity : APRandomizer.getServer().getPlayerList().getPlayers()) {
            location.give(serverPlayerEntity);
        }
    }

    public void syncAllAdvancements() {
        if (server == null) return;
        Registry<APLocation> locationRegistry = getServer().registryAccess().lookupOrThrow(APRegistries.ARCHIPELAGO_LOCATION);
        for (var a : locationRegistry.registryKeySet()) {
            syncAdvancement(a, locationRegistry.getValueOrThrow(a));
        }
    }

    public int getFinishedAmount() {
        return earnedAdvancements.size();
    }

    public void setCheckedAdvancements(LongSet checkedLocations) {
        earnedAdvancements.addAll(checkedLocations);
        worldData().ifPresent(data -> {
            for (var checkedLocation : checkedLocations) {
                data.addLocation(checkedLocation);
            }
        });

        syncAllAdvancements();
    }
}
