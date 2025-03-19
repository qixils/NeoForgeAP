package gg.archipelago.aprandomizer.managers.advancementmanager;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.data.WorldData;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import net.minecraft.Util;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Set;

import static gg.archipelago.aprandomizer.APRandomizer.getServer;

public class AdvancementManager {

    private final Object2LongMap<ResourceLocation> advancementData = Util.make(new Object2LongOpenHashMap<>(), map -> {
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "nether/obtain_crying_obsidian"), 42000L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "nether/distract_piglin"), 42001L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "story/obtain_armor"), 42002L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "adventure/very_very_frightening"), 42003L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "story/lava_bucket"), 42004L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "end/kill_dragon"), 42005L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "nether/all_potions"), 42006L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "husbandry/tame_an_animal"), 42007L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "nether/create_beacon"), 42008L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "story/deflect_arrow"), 42009L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "story/iron_tools"), 42010L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "nether/brew_potion"), 42011L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "end/dragon_egg"), 42012L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "husbandry/fishy_business"), 42013L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "nether/explore_nether"), 42014L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "nether/ride_strider"), 42015L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "adventure/sniper_duel"), 42016L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "nether/root"), 42017L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "end/levitate"), 42018L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "nether/all_effects"), 42019L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "adventure/bullseye"), 42020L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "nether/get_wither_skull"), 42021L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "husbandry/bred_all_animals"), 42022L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "story/mine_stone"), 42023L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "adventure/two_birds_one_arrow"), 42024L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "story/enter_the_nether"), 42025L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "adventure/whos_the_pillager_now"), 42026L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "story/upgrade_tools"), 42027L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "husbandry/tactical_fishing"), 42028L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "story/cure_zombie_villager"), 42029L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "end/find_end_city"), 42030L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "story/form_obsidian"), 42031L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "end/enter_end_gateway"), 42032L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "nether/obtain_blaze_rod"), 42033L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "nether/loot_bastion"), 42034L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "adventure/shoot_arrow"), 42035L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "husbandry/silk_touch_nest"), 42036L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "adventure/arbalistic"), 42037L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "end/respawn_dragon"), 42038L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "story/smelt_iron"), 42039L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "nether/charge_respawn_anchor"), 42040L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "story/shiny_gear"), 42041L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "end/elytra"), 42042L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "adventure/summon_iron_golem"), 42043L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "nether/return_to_sender"), 42044L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "adventure/sleep_in_bed"), 42045L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "end/dragon_breath"), 42046L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "adventure/root"), 42047L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "adventure/kill_all_mobs"), 42048L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "story/enchant_item"), 42049L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "adventure/voluntary_exile"), 42050L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "story/follow_ender_eye"), 42051L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "end/root"), 42052L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "husbandry/obtain_netherite_hoe"), 42053L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "adventure/totem_of_undying"), 42054L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "adventure/kill_a_mob"), 42055L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "adventure/adventuring_time"), 42056L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "husbandry/plant_seed"), 42057L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "nether/find_bastion"), 42058L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "adventure/hero_of_the_village"), 42059L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "nether/obtain_ancient_debris"), 42060L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "nether/create_full_beacon"), 42061L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "nether/summon_wither"), 42062L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "husbandry/balanced_diet"), 42063L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "nether/fast_travel"), 42064L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "husbandry/root"), 42065L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "nether/use_lodestone"), 42066L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "husbandry/safely_harvest_honey"), 42067L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "adventure/trade"), 42068L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "nether/uneasy_alliance"), 42069L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "story/mine_diamond"), 42070L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "nether/find_fortress"), 42071L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "adventure/throw_trident"), 42072L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "story/root"), 42073L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "adventure/honey_block_slide"), 42074L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "adventure/ol_betsy"), 42075L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "nether/netherite_armor"), 42076L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "story/enter_the_end"), 42077L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "husbandry/breed_an_animal"), 42078L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "husbandry/complete_catalogue"), 42079L);
        map.put(ResourceLocation.fromNamespaceAndPath("aprandomizer", "archipelago/get_wood"), 42080L);
        map.put(ResourceLocation.fromNamespaceAndPath("aprandomizer", "archipelago/get_pickaxe"), 42081L);
        map.put(ResourceLocation.fromNamespaceAndPath("aprandomizer", "archipelago/hot_topic"), 42082L);
        map.put(ResourceLocation.fromNamespaceAndPath("aprandomizer", "archipelago/bake_bread"), 42083L);
        map.put(ResourceLocation.fromNamespaceAndPath("aprandomizer", "archipelago/the_lie"), 42084L);
        map.put(ResourceLocation.fromNamespaceAndPath("aprandomizer", "archipelago/ride_minecart"), 42085L);
        map.put(ResourceLocation.fromNamespaceAndPath("aprandomizer", "archipelago/craft_sword"), 42086L);
        map.put(ResourceLocation.fromNamespaceAndPath("aprandomizer", "archipelago/cow_tipper"), 42087L);
        map.put(ResourceLocation.fromNamespaceAndPath("aprandomizer", "archipelago/ride_pig"), 42088L);
        map.put(ResourceLocation.fromNamespaceAndPath("aprandomizer", "archipelago/overkill"), 42089L);
        map.put(ResourceLocation.fromNamespaceAndPath("aprandomizer", "archipelago/obtain_bookshelf"), 42090L);
        map.put(ResourceLocation.fromNamespaceAndPath("aprandomizer", "archipelago/overpowered"), 42091L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "husbandry/wax_on"), 42092L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "husbandry/wax_off"), 42093L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "husbandry/axolotl_in_a_bucket"), 42094L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "husbandry/kill_axolotl_target"), 42095L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "adventure/spyglass_at_parrot"), 42096L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "adventure/spyglass_at_ghast"), 42097L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "adventure/spyglass_at_dragon"), 42098L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "adventure/lightning_rod_with_villager_no_fire"), 42099L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "adventure/walk_on_powder_snow_with_leather_boots"), 42100L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "husbandry/make_a_sign_glow"), 42101L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "husbandry/ride_a_boat_with_a_goat"), 42102L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "adventure/fall_from_world_height"), 42103L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "nether/ride_strider_in_overworld_lava"), 42104L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "adventure/play_jukebox_in_meadows"), 42105L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "adventure/trade_at_world_height"), 42106L);
        // 1.19 advancements
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "husbandry/allay_deliver_cake_to_note_block"), 42107L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "husbandry/tadpole_in_a_bucket"), 42108L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "adventure/kill_mob_near_sculk_catalyst"), 42109L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "adventure/avoid_vibration"), 42110L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "husbandry/leash_all_frog_variants"), 42111L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "husbandry/froglights"), 42112L);
        map.put(ResourceLocation.fromNamespaceAndPath("minecraft", "husbandry/allay_deliver_item_to_player"), 42113L);

    });

    private final LongSet earnedAdvancements = new LongOpenHashSet();

    public AdvancementManager() {

    }


    public long getAdvancementID(ResourceLocation namespacedID) {
        if (advancementData.containsKey(namespacedID))
            return advancementData.getLong(namespacedID);
        return 0L;
    }

    public boolean hasAdvancement(long id) {
        return earnedAdvancements.contains(id);
    }

    public boolean hasAdvancement(ResourceLocation namespacedID) {
        return earnedAdvancements.contains(getAdvancementID(namespacedID));
    }

    public void addAdvancement(long id) {
        earnedAdvancements.add(id);
        APRandomizer.getAP().checkLocation(id);
        APRandomizer.getGoalManager().updateGoal( true);
        APRandomizer.getWorldData().addLocation(id);
        syncAllAdvancements();
    }

    public void resendAdvancements() {
        for (Long earnedAdvancement : earnedAdvancements) {
            APRandomizer.getAP().checkLocation(earnedAdvancement);
        }
    }

    public void syncAdvancement(AdvancementHolder advancementHolder) {
        if (hasAdvancement(advancementHolder.id())) {
            for (ServerPlayer serverPlayerEntity : APRandomizer.getServer().getPlayerList().getPlayers()) {
                AdvancementProgress ap = serverPlayerEntity.getAdvancements().getOrStartProgress(advancementHolder);
                if (ap.isDone())
                    continue;
                for (String remainingCriterion : ap.getRemainingCriteria()) {
                    serverPlayerEntity.getAdvancements().award(advancementHolder, remainingCriterion);
                }
            }
        }
        if (APRandomizer.getRecipeManager().hasReceived(advancementHolder.id())) {
            for (ServerPlayer serverPlayerEntity : APRandomizer.getServer().getPlayerList().getPlayers()) {
                AdvancementProgress ap = serverPlayerEntity.getAdvancements().getOrStartProgress(advancementHolder);
                if (ap.isDone())
                    continue;
                for (String remainingCriterion : ap.getRemainingCriteria()) {
                    serverPlayerEntity.getAdvancements().award(advancementHolder, remainingCriterion);
                }
            }
        }
    }

    public void syncAllAdvancements() {
        for (var a : getServer().getAdvancements().getAllAdvancements()) {
            syncAdvancement(a);
        }
    }

    public int getFinishedAmount() {
        return earnedAdvancements.size();
    }

    public void setCheckedAdvancements(Set<Long> checkedLocations) {
        earnedAdvancements.addAll(checkedLocations);
        WorldData data = APRandomizer.getWorldData();
        for (var checkedLocation : checkedLocations) {
            data.addLocation(checkedLocation);
        }

        syncAllAdvancements();
    }
}
