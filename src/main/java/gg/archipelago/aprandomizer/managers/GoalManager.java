package gg.archipelago.aprandomizer.managers;

import dev.koifysh.archipelago.ClientStatus;
import gg.archipelago.aprandomizer.APClient;
import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.APStorage.APMCData;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import gg.archipelago.aprandomizer.data.WorldData;
import gg.archipelago.aprandomizer.managers.advancementmanager.AdvancementManager;
import gg.archipelago.aprandomizer.managers.itemmanager.ItemManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.bossevents.CustomBossEvent;
import net.minecraft.server.bossevents.CustomBossEvents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@EventBusSubscriber
public class GoalManager {

    int advancementsRequired;
    int dragonEggShardsRequired;
    int totalDragonEggShards;

    @NotNull
    private final AdvancementManager advancementManager;
    @Nullable
    private CustomBossEvent advancementInfoBar;
    @Nullable
    private CustomBossEvent eggInfoBar;
    @Nullable
    private CustomBossEvent connectionInfoBar;

    @NotNull
    private final APMCData apmc;

    public GoalManager() {
        apmc = APRandomizer.getApmcData();
        advancementManager = Objects.requireNonNull(APRandomizer.getAdvancementManager(), "Mod not initialized");
        advancementsRequired = apmc.advancements_required;
        dragonEggShardsRequired = apmc.egg_shards_required;
        totalDragonEggShards = apmc.egg_shards_available;
        initializeInfoBar();
    }

    public void initializeInfoBar() {
        MinecraftServer server = APRandomizer.getServer();
        if (server == null) return;

        CustomBossEvents bossInfoManager = server.getCustomBossEvents();
        advancementInfoBar = bossInfoManager.create(ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "advancementinfobar"), Component.literal(""));
        advancementInfoBar.setMax(advancementsRequired);
        advancementInfoBar.setColor(BossEvent.BossBarColor.PINK);
        advancementInfoBar.setOverlay(BossEvent.BossBarOverlay.NOTCHED_10);

        eggInfoBar = bossInfoManager.create(ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "egginfobar"), Component.literal(""));
        eggInfoBar.setMax(dragonEggShardsRequired);
        eggInfoBar.setColor(BossEvent.BossBarColor.WHITE);
        eggInfoBar.setOverlay(BossEvent.BossBarOverlay.NOTCHED_6);

        connectionInfoBar = bossInfoManager.create(ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "connectioninfobar"), Component.literal("Not connected to Archipelago").withStyle(Style.EMPTY.withColor(ChatFormatting.RED)));
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
        if (canFinish)
            checkGoalCompletion();
        checkDragonSpawn();
    }


    public String getAdvancementRemainingString() {
        if (advancementsRequired > 0) {
            return String.format(" Advancements (%d / %d)", advancementManager.getFinishedAmount(), advancementsRequired);
        }
        return "";
    }

    public String getEggShardsRemainingString() {
        if (dragonEggShardsRequired > 0) {
            return String.format(" Dragon Egg Shards (%d / %d)", currentEggShards(), dragonEggShardsRequired);
        }
        return "";
    }

    private int currentEggShards() {
        ItemManager itemManager = APRandomizer.getItemManager();
        if (itemManager == null) return 0;
        int current = 0;
        for (var item : APRandomizer.getItemManager().getAllItems()) {
            if (item == ItemManager.DRAGON_EGG_SHARD) {
                ++current;
            }
        }
        return current;
    }

    public void updateInfoBar() {
        MinecraftServer server = APRandomizer.getServer();
        if (server == null || advancementInfoBar == null || connectionInfoBar == null || eggInfoBar == null)
            return;
        server.execute(() -> {
            var players = server.getPlayerList().getPlayers();
            advancementInfoBar.setPlayers(players);
            eggInfoBar.setPlayers(players);
            connectionInfoBar.setPlayers(players);
        });

        advancementInfoBar.setValue(advancementManager.getFinishedAmount());
        eggInfoBar.setValue(currentEggShards());

        connectionInfoBar.setVisible(!APRandomizer.isConnected());

        advancementInfoBar.setName(Component.literal(getAdvancementRemainingString()));
        eggInfoBar.setName(Component.literal(getEggShardsRemainingString()));

    }

    public void checkGoalCompletion() {
        if (!APRandomizer.isConnected())
            return;
        APClient apClient = APRandomizer.getAP();
        if (apClient == null) return; // checked by isConnected but a failsafe doesn't hurt

        boolean hasGoal = goalsDone();
        if (apmc.required_bosses.hasDragon())
            hasGoal = hasGoal && APRandomizer.worldData().map(WorldData::isDragonKilled).orElse(false);
        if (apmc.required_bosses.hasWither())
            hasGoal = hasGoal && APRandomizer.worldData().map(WorldData::isWitherKilled).orElse(false);

        if (hasGoal)
            apClient.setGameState(ClientStatus.CLIENT_GOAL);
    }

    public void checkDragonSpawn() {
        WorldData worldData = APRandomizer.getWorldData();
        if (worldData == null) return;

        //check if the dragon is not spawned and we need to spawn it.
        if (goalsDone() && worldData.getDragonState() == WorldData.DRAGON_ASLEEP) {
            //set the dragon state to spawn as soon as the end 0,0 chunk is loaded
            worldData.setDragonState(WorldData.DRAGON_WAITING);
            Utils.PlaySoundToAll(SoundEvents.ENDER_DRAGON_AMBIENT);
            Utils.sendMessageToAll("The Dragon has awoken.");
            Utils.sendTitleToAll(Component.literal("Ender Dragon").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(java.awt.Color.ORANGE.getRGB()))), Component.literal("has awoken"), 40, 120, 40);

            // TODO: Spawn end dragon
//            Utils.SpawnDragon(end);
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
        if (goalManager == null) return;
        if (mob instanceof EnderDragon && goalManager.goalsDone()) {
            APRandomizer.worldData().ifPresent(WorldData::setDragonKilled);
            Utils.sendMessageToAll("She is no more...");
            goalManager.updateGoal(false);
        }
        if (mob instanceof WitherBoss && goalManager.goalsDone()) {
            APRandomizer.worldData().ifPresent(WorldData::setWitherKilled);
            Utils.sendMessageToAll("The Darkness has lifted...");
            goalManager.updateGoal(true);
        }
    }
}
