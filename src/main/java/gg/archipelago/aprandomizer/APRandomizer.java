package gg.archipelago.aprandomizer;

import com.google.gson.Gson;
import dev.koifysh.archipelago.network.client.BouncePacket;
import gg.archipelago.aprandomizer.APStorage.APMCData;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import gg.archipelago.aprandomizer.data.WorldData;
import gg.archipelago.aprandomizer.managers.GoalManager;
import gg.archipelago.aprandomizer.managers.advancementmanager.AdvancementManager;
import gg.archipelago.aprandomizer.managers.itemmanager.ItemManager;
import gg.archipelago.aprandomizer.managers.recipemanager.RecipeManager;
import gg.archipelago.aprandomizer.modifiers.APStructureModifier;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(APRandomizer.MODID)
public class APRandomizer {
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "aprandomizer";

    //store our APClient
    @Nullable
    static private APClient apClient;
    @Nullable
    static public MinecraftServer server;
    @Nullable
    static private AdvancementManager advancementManager;
    @Nullable
    static private RecipeManager recipeManager;
    @Nullable
    static private ItemManager itemManager;
    @Nullable
    static private GoalManager goalManager;
    @NotNull
    static private final APMCData apmcData;
    @NotNull
    static private final Set<Integer> validVersions = new HashSet<>() {{ // TODO
        this.add(9); //mc 1.19
        this.add(10);
    }};
    static private boolean jailPlayers = true;
    @NotNull
    static private BlockPos jailCenter = BlockPos.ZERO;
    @Nullable
    static private WorldData worldData;

    static {
        Gson gson = new Gson();
        APMCData data = null;
        try {
            Path path = Paths.get("./APData/");
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                LOGGER.info("APData folder missing, creating.");
            }

            File[] files = new File(path.toUri()).listFiles((d, name) -> name.endsWith(".apmc"));
            assert files != null;
            Arrays.sort(files, Comparator.comparingLong(File::lastModified));
            String b64 = Files.readAllLines(files[0].toPath()).getFirst();
            String json = new String(Base64.getDecoder().decode(b64));
            data = gson.fromJson(json, APMCData.class);
            // TODO
//            if (!validVersions.contains(apmcData.client_version)) {
//                apmcData.state = APMCData.State.INVALID_VERSION;
//            }
            LOGGER.info("Loaded .apmc data");
            //LOGGER.info(apmcData.structures.toString());
        } catch (Exception e) {
            LOGGER.error("no .apmc file found. please place .apmc file in './APData/' folder.");
            if (data == null) {
                data = new APMCData();
                data.state = APMCData.State.MISSING;
            }
        }
        apmcData = data;
    }

    public APRandomizer(IEventBus modEventBus) {
        LOGGER.info("Minecraft Archipelago 1.21.3 v0.2.0 Randomizer initializing.");

        // Register ourselves for server and other game events we are interested in
        IEventBus forgeBus = NeoForge.EVENT_BUS;
        forgeBus.register(this);

        // For registration and init stuff.
        APStructures.DEFERRED_REGISTRY_STRUCTURE.register(modEventBus);
        APStructureModifier.structureModifiers.register(modEventBus);
//        APStructureModifier.structureModifiers.register("ap_structure_modifier",APStructureModifier::makeCodec);

    }

    @Nullable
    public static APClient getAP() {
        return apClient;
    }

    @NotNull
    public static Optional<APClient> AP() {
        return Optional.ofNullable(apClient);
    }

    public static boolean isConnected() {
        return (apClient != null && apClient.isConnected());
    }

    @Nullable
    public static AdvancementManager getAdvancementManager() {
        return advancementManager;
    }

    public static Optional<AdvancementManager> advancementManager() {
        return Optional.ofNullable(advancementManager);
    }

    @Nullable
    public static RecipeManager getRecipeManager() {
        return recipeManager;
    }

    public static Optional<RecipeManager> recipeManager() {
        return Optional.ofNullable(recipeManager);
    }

    @NotNull
    public static APMCData getApmcData() {
        return apmcData;
    }

    @Nullable
    public static MinecraftServer getServer() {
        return server;
    }

    public static Optional<MinecraftServer> server() {
        return Optional.ofNullable(server);
    }

    @Nullable
    public static ItemManager getItemManager() {
        return itemManager;
    }

    public static Optional<ItemManager> itemManager() {
        return Optional.ofNullable(itemManager);
    }

    @NotNull
    public static Set<Integer> getValidVersions() {
        return validVersions;
    }

    public static boolean isJailPlayers() {
        return jailPlayers;
    }

    public static void setJailPlayers(boolean jailPlayers) {
        APRandomizer.jailPlayers = jailPlayers;

        if (worldData != null) worldData.setJailPlayers(jailPlayers);
    }

    @NotNull
    public static BlockPos getJailPosition() {
        return jailCenter;
    }

    public static boolean isRace() {
        return getApmcData().race;
    }

    public static void sendBounce(BouncePacket packet) {
        if (apClient != null)
            apClient.sendBounce(packet);
    }

    @Nullable
    public static GoalManager getGoalManager() {
        return goalManager;
    }

    public static Optional<GoalManager> goalManager() {
        return Optional.ofNullable(goalManager);
    }

    @Nullable
    public static WorldData getWorldData() {
        return worldData;
    }

    public static Optional<WorldData> worldData() {
        return Optional.ofNullable(worldData);
    }

    @SubscribeEvent
    public void onServerAboutToStart(ServerAboutToStartEvent event) {
        if (apmcData.state != APMCData.State.VALID) {
            LOGGER.error("invalid APMC file");
        }
        server = event.getServer();
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        if (server == null) server = event.getServer();

        // do something when the server starts
        advancementManager = new AdvancementManager();
        recipeManager = new RecipeManager();
        itemManager = new ItemManager();
        goalManager = new GoalManager();


        server.getGameRules().getRule(GameRules.RULE_LIMITED_CRAFTING).set(true, server);
        server.getGameRules().getRule(GameRules.RULE_KEEPINVENTORY).set(true, server);
        server.getGameRules().getRule(GameRules.RULE_ANNOUNCE_ADVANCEMENTS).set(false, server);
        server.setDifficulty(Difficulty.NORMAL, true);

        //fetch our custom world save data we attach to the worlds.
        worldData = server.overworld().getDataStorage().computeIfAbsent(WorldData.factory(), "apdata");
        jailPlayers = worldData.getJailPlayers();
        advancementManager.setCheckedAdvancements(worldData.getLocations());


        //check if APMC data is present and if the seed matches what we expect
        if (apmcData.state == APMCData.State.VALID && !worldData.getSeedName().equals(apmcData.seed_name)) {
            //check to see if our worlddata is empty if it is then save the aproom data.
            if (worldData.getSeedName().isEmpty()) {
                worldData.setSeedName(apmcData.seed_name);
                //this is also our first boot so set this flag so we can do first boot stuff.
            } else {
                apmcData.state = APMCData.State.INVALID_SEED;
            }
        }

        //if no apmc file was found set our world data seed to invalid so it will force a regen of this blank world.
        if (apmcData.state == APMCData.State.MISSING) {
            worldData.setSeedName("Invalid");
        }

        if (apmcData.state == APMCData.State.VALID) {
            apClient = new APClient();
        }


        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
        if (jailPlayers && overworld != null) {
            BlockPos spawn = overworld.getSharedSpawnPos();
            // alter the spawn box position, so it doesn't interfere with spawning

            var jailOptional = overworld.getStructureManager().get(ResourceLocation.fromNamespaceAndPath(MODID, "spawnjail"));
            if (jailOptional.isPresent()) {
                StructureTemplate jail = overworld.getStructureManager().get(ResourceLocation.fromNamespaceAndPath(MODID, "spawnjail")).orElseThrow();
                BlockPos jailPos = new BlockPos(spawn.getX() + 5, 300, spawn.getZ() + 5);
                jailCenter = new BlockPos(jailPos.getX() + (jail.getSize().getX() / 2), jailPos.getY() + 1, jailPos.getZ() + (jail.getSize().getZ() / 2));
                jail.placeInWorld(overworld, jailPos, jailPos, new StructurePlaceSettings(), RandomSource.create(), 2);
            } else {
                jailCenter = spawn;
            }
            server.getGameRules().getRule(GameRules.RULE_DAYLIGHT).set(false, server);
            server.getGameRules().getRule(GameRules.RULE_WEATHER_CYCLE).set(false, server);
            server.getGameRules().getRule(GameRules.RULE_DOFIRETICK).set(false, server);
            server.getGameRules().getRule(GameRules.RULE_RANDOMTICKING).set(0, server);
            server.getGameRules().getRule(GameRules.RULE_DO_PATROL_SPAWNING).set(false, server);
            server.getGameRules().getRule(GameRules.RULE_DO_TRADER_SPAWNING).set(false, server);
            server.getGameRules().getRule(GameRules.RULE_MOBGRIEFING).set(false, server);
            server.getGameRules().getRule(GameRules.RULE_DOMOBSPAWNING).set(false, server);
            server.getGameRules().getRule(GameRules.RULE_DO_IMMEDIATE_RESPAWN).set(true, server);
            server.getGameRules().getRule(GameRules.RULE_DOMOBLOOT).set(false, server);
            server.getGameRules().getRule(GameRules.RULE_DOENTITYDROPS).set(false, server);
            overworld.setDayTime(0);

        }

        if (apmcData.state == APMCData.State.VALID && apmcData.server != null) {

            APClient apClient = getAP();
            if (apClient != null) {
                apClient.setName(apmcData.player_name);
                String address = apmcData.server.concat(":" + apmcData.port);

                Utils.sendMessageToAll("Connecting to Archipelago server at " + address);

                try {
                    apClient.connect(address);
                } catch (URISyntaxException e) {
                    Utils.sendMessageToAll("unable to connect");
                }
            }
        }
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        if (apClient != null)
            apClient.close();
    }

    @SubscribeEvent
    public void onServerStopped(ServerStoppedEvent event) {
        if (apClient != null)
            apClient.close();
    }
}
