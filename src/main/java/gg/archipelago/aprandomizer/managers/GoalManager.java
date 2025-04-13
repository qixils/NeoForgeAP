package gg.archipelago.aprandomizer.managers;

import dev.koifysh.archipelago.ClientStatus;
import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.ap.storage.APMCData;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import gg.archipelago.aprandomizer.data.WorldData;
import gg.archipelago.aprandomizer.managers.advancementmanager.AdvancementManager;
import gg.archipelago.aprandomizer.managers.itemmanager.ItemManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.bossevents.CustomBossEvent;
import net.minecraft.server.bossevents.CustomBossEvents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class GoalManager {

    int advancementsRequired = 0;

    int dragonEggShardsRequired = 0;

    int totalDragonEggShards = 0;

    private final AdvancementManager advancementManager;

    private CustomBossEvent advancementInfoBar;
    private CustomBossEvent eggInfoBar;
    private CustomBossEvent connectionInfoBar;

    private APMCData apmc;

    private boolean dragonKilled = false;
    private boolean witherKilled = false;

    public GoalManager () {
        apmc = APRandomizer.getApmcData();
        advancementManager = APRandomizer.getAdvancementManager();
        advancementsRequired = apmc.advancements_required;
        dragonEggShardsRequired = apmc.egg_shards_required;
        totalDragonEggShards = apmc.egg_shards_available;
        initializeInfoBar();
    }

    public void initializeInfoBar() {
        CustomBossEvents bossInfoManager = APRandomizer.getServer().getCustomBossEvents();
        advancementInfoBar = bossInfoManager.create(ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID,"advancementinfobar"), Component.literal(""));
        advancementInfoBar.setMax(advancementsRequired);
        advancementInfoBar.setColor(BossEvent.BossBarColor.PINK);
        advancementInfoBar.setOverlay(BossEvent.BossBarOverlay.NOTCHED_10);

        eggInfoBar = bossInfoManager.create(ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID,"egginfobar"), Component.literal(""));
        eggInfoBar.setMax(dragonEggShardsRequired);
        eggInfoBar.setColor(BossEvent.BossBarColor.WHITE);
        eggInfoBar.setOverlay(BossEvent.BossBarOverlay.NOTCHED_6);

        connectionInfoBar = bossInfoManager.create(ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID,"connectioninfobar"), Component.literal("Not connected to Archipelago").withStyle(Style.EMPTY.withColor(ChatFormatting.RED)));
        connectionInfoBar.setMax(1);
        connectionInfoBar.setValue(1);
        connectionInfoBar.setColor(BossEvent.BossBarColor.RED);
        connectionInfoBar.setOverlay(BossEvent.BossBarOverlay.PROGRESS);

        updateInfoBar();
        advancementInfoBar.setVisible((advancementsRequired > 0));
        eggInfoBar.setVisible((dragonEggShardsRequired > 0));
        connectionInfoBar.setVisible(true);
    }

    public void updateGoal(boolean canFinish) {
        updateInfoBar();
        if(canFinish)
            checkGoalCompletion();
        checkBossMessages();
    }



    public String getAdvancementRemainingString() {
        if (advancementsRequired > 0) {
            return String.format(" Advancements (%d / %d)", advancementManager.getFinishedAmount(), advancementsRequired);
        }
        return "";
    }

    public String getEggShardsRemainingString() {
        if(dragonEggShardsRequired > 0) {
            return String.format(" Dragon Egg Shards (%d / %d)",currentEggShards(), dragonEggShardsRequired);
        }
        return "";
    }

    private int currentEggShards() {
        int current = 0;
        for (var item : APRandomizer.getItemManager().getAllItems()) {
            if(item == ItemManager.DRAGON_EGG_SHARD) {
                ++current;
            }
        }
        return current;
    }

    public void updateInfoBar() {
        if(advancementInfoBar == null || eggInfoBar == null)
            return;
        APRandomizer.getServer().execute(() -> {
            advancementInfoBar.setPlayers(APRandomizer.getServer().getPlayerList().getPlayers());
            eggInfoBar.setPlayers(APRandomizer.getServer().getPlayerList().getPlayers());
            connectionInfoBar.setPlayers(APRandomizer.getServer().getPlayerList().getPlayers());
        });

        advancementInfoBar.setValue(advancementManager.getFinishedAmount());
        eggInfoBar.setValue(currentEggShards());

        connectionInfoBar.setVisible(!APRandomizer.isConnected());

        advancementInfoBar.setName(Component.literal(getAdvancementRemainingString()));
        eggInfoBar.setName(Component.literal(getEggShardsRemainingString()));

    }

    public void checkGoalCompletion() {
        if(!APRandomizer.isConnected())
            return;
        //check to see if our goal is done! if so send compleatoin to AP server
        if(goalsDone() && apmc.required_bosses == APMCData.Bosses.NONE) {
            APRandomizer.getAP().setGameState(ClientStatus.CLIENT_GOAL);
        }

        //check if both bosses have/need to be killed
        if(goalsDone() && apmc.required_bosses == APMCData.Bosses.BOTH && dragonKilled && witherKilled) {
            APRandomizer.getAP().setGameState(ClientStatus.CLIENT_GOAL);
        }

        //check wither goal completion
        if(goalsDone() && apmc.required_bosses == APMCData.Bosses.WITHER && witherKilled) {
            APRandomizer.getAP().setGameState(ClientStatus.CLIENT_GOAL);
        }

        //check wither goal completion
        if(goalsDone() && apmc.required_bosses == APMCData.Bosses.ENDER_DRAGON && dragonKilled) {
            APRandomizer.getAP().setGameState(ClientStatus.CLIENT_GOAL);
        }
    }

    public void checkBossMessages() {
        WorldData worldData = APRandomizer.getWorldData();

        //check if the dragon message has been sent, and send it if needed.
        if (goalsDone() && worldData.getDragonState() == WorldData.ASLEEP && isBossRequired(APMCData.Bosses.ENDER_DRAGON)) {
            worldData.setDragonState(WorldData.WAITING);
            Utils.PlaySoundToAll(SoundEvents.ENDER_DRAGON_AMBIENT);
            Utils.sendMessageToAll("The Dragon is waiting...");
            Utils.sendTitleToAll(Component.literal("The Dragon").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(java.awt.Color.ORANGE.getRGB()))), Component.literal("is waiting..."), 40, 120, 40);
        }

        //check if the wither message has been sent, and send it if needed.
        if (goalsDone() && worldData.getWitherState() == WorldData.ASLEEP && isBossRequired(APMCData.Bosses.WITHER)) {
            worldData.setWitherState(WorldData.WAITING);
            Utils.PlaySoundToAll(SoundEvents.WITHER_AMBIENT);
            Utils.sendMessageToAll("The Darkness is calling...");
            Utils.sendTitleToAll(Component.literal("The Darkness").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(java.awt.Color.BLACK.getRGB()))), Component.literal("is calling..."), 40, 120, 40);
        }
    }

    public boolean goalsDone() {
        return advancementManager.getFinishedAmount() >= advancementsRequired && this.currentEggShards() >= dragonEggShardsRequired;
    }


    //subscribe to living death event to check for wither/dragon kills;
    @SubscribeEvent
    static void onBossDeath(LivingDeathEvent event) {
        LivingEntity mob = event.getEntity();
        GoalManager goalManager = APRandomizer.getGoalManager();
        if(mob instanceof EnderDragon && goalManager.goalsDone() && isBossRequired(APMCData.Bosses.ENDER_DRAGON)) {
            goalManager.dragonKilled = true;
            Utils.sendMessageToAll("She is no more...");
            goalManager.updateGoal(false);
        }
        if(mob instanceof WitherBoss && goalManager.goalsDone() && isBossRequired(APMCData.Bosses.WITHER)) {
            goalManager.witherKilled = true;
            Utils.sendMessageToAll("The Darkness has lifted...");
            goalManager.updateGoal(true);
        }
    }

    // check APMC.required_bosses to see if the boss is required
    public static boolean isBossRequired(APMCData.Bosses boss) {
        var required = APRandomizer.getApmcData().required_bosses;

        // if it matches our goal its true
        if (required == boss) return true;
        // a boss is required and you asked about none.
        if (boss == APMCData.Bosses.NONE) return false;
        // if both boses are required and you didn't ask about none return ture;
        if (required == APMCData.Bosses.BOTH) return true;

        return false;
    }

    public boolean isDragonDead() {
        return dragonKilled;
    }
    public boolean isWitherDead() {
        return witherKilled;
    }
}
