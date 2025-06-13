package gg.archipelago.aprandomizer;

import com.google.gson.Gson;
import dev.koifysh.archipelago.network.client.BouncePacket;
import gg.archipelago.aprandomizer.advancements.APCriteriaTriggers;
import gg.archipelago.aprandomizer.ap.APClient;
import gg.archipelago.aprandomizer.ap.storage.APMCData;
import gg.archipelago.aprandomizer.attachments.APAttachmentTypes;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import gg.archipelago.aprandomizer.common.commands.StartCommand;
import gg.archipelago.aprandomizer.data.WorldData;
import gg.archipelago.aprandomizer.data.loot.APLootModifierTypes;
import gg.archipelago.aprandomizer.datamaps.APDataMaps;
import gg.archipelago.aprandomizer.items.APItem;
import gg.archipelago.aprandomizer.items.APRewardTypes;
import gg.archipelago.aprandomizer.locations.APLocation;
import gg.archipelago.aprandomizer.locations.APLocationTypes;
import gg.archipelago.aprandomizer.managers.GoalManager;
import gg.archipelago.aprandomizer.managers.advancementmanager.AdvancementManager;
import gg.archipelago.aprandomizer.managers.itemmanager.ItemManager;
import gg.archipelago.aprandomizer.modifiers.APStructureModifier;
import gg.archipelago.aprandomizer.structures.level.StructureLevelReferenceTypes;
import it.unimi.dsi.fastutil.ints.IntSet;
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
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.util.Comparator;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(APRandomizer.MODID)
public class APRandomizer {
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "aprandomizer";

    //store our APClient
    @Nullable
    static private APClient APClient;
    @Nullable
    static public MinecraftServer server;
    @Nullable
    static private AdvancementManager advancementManager;
    @Nullable
    static private ItemManager itemManager;
    @Nullable
    static private GoalManager goalManager;
    @NotNull
    static private final APMCData apmcData;
    static private final IntSet VALID_VERSIONS = IntSet.of(
            9 // 1.19
    );
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
            if (!VALID_VERSIONS.contains(data.client_version)) {
                data.state = APMCData.State.INVALID_VERSION;
            }
            LOGGER.info("Loaded .apmc data");
            LOGGER.info(data.structures.toString());
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
        LOGGER.info("Minecraft Archipelago 1.21.5 v0.2.0 Randomizer initializing.");

        // Register ourselves for server and other game events we are interested in
        IEventBus forgeBus = NeoForge.EVENT_BUS;
        forgeBus.register(this);

        // For registration and init stuff.
        APStructures.DEFERRED_REGISTRY_STRUCTURE.register(modEventBus);
        APStructureModifier.STRUCTURE_MODIFIERS.register(modEventBus);
        APCriteriaTriggers.CRITERION_TRIGGERS.register(modEventBus);
        APLocationTypes.REGISTER.register(modEventBus);
        APRewardTypes.REGISTER.register(modEventBus);
        StructureLevelReferenceTypes.REGISTER.register(modEventBus);
        APAttachmentTypes.REGISTER.register(modEventBus);
        APLootModifierTypes.REGISTER.register(modEventBus);
        modEventBus.addListener(APRandomizer::registerDataPackRegistries);
        modEventBus.addListener(APRandomizer::registerDataMapTypes);
    }

    public static void registerDataPackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(APRegistries.ARCHIPELAGO_ITEM, APItem.CODEC);
        event.dataPackRegistry(APRegistries.ARCHIPELAGO_LOCATION, APLocation.CODEC);
    }

    public static void registerDataMapTypes(RegisterDataMapTypesEvent event) {
        event.register(APDataMaps.DEFAULT_STRUCTURE_BIOMES);
    }
    @Nullable
    public static APClient getAP() {
        return APClient;
    }

    public static boolean isConnected() {
        return (APClient != null && APClient.isConnected());
    }

    @Nullable
    public static AdvancementManager getAdvancementManager() {
        return advancementManager;
    }

    @NotNull
    public static APMCData getApmcData() {
        return apmcData;
    }

    @Nullable
    public static MinecraftServer getServer(){
        return server;
    }

    @Nullable
    public static ItemManager getItemManager() {
        return itemManager;
    }

    @NotNull
    public static IntSet getValidVersions() {
        return VALID_VERSIONS;
    }

    public static boolean isJailPlayers() {
        return worldData != null && worldData.getJailPlayers();
    }

    public static void setJailPlayers(boolean jailPlayers) {
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
        if (APClient != null)
            APClient.sendBounce(packet);
    }

    @Nullable
    public static GoalManager getGoalManager() {
        return goalManager;
    }

    @Nullable
    public static WorldData getWorldData() {
        return worldData;
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
        if (apmcData.state == APMCData.State.MISSING) {
            LOGGER.error("NO APMC FILE FOUND. PLEASE PLACE A VALID APMC FILE IN THE APDATA FOLDER.");
            return;
        }
        if (server == null) server = event.getServer();

        // do something when the server starts

        server.getGameRules().getRule(GameRules.RULE_LIMITED_CRAFTING).set(true, server);
        server.getGameRules().getRule(GameRules.RULE_KEEPINVENTORY).set(true, server);
        server.getGameRules().getRule(GameRules.RULE_ANNOUNCE_ADVANCEMENTS).set(false, server);
        server.setDifficulty(Difficulty.NORMAL, true);

        //fetch our custom world save data we attach to the worlds.
        worldData = server.overworld().getDataStorage().computeIfAbsent(WorldData.getFactory());

        //set up managers
        if (advancementManager == null) advancementManager = new AdvancementManager(worldData);
        if (goalManager == null) goalManager = new GoalManager(server, apmcData, advancementManager, worldData);
        if (itemManager == null) itemManager = new ItemManager(server, goalManager, worldData);

        advancementManager.setCheckedAdvancements(worldData.getLocations());

        //check if APMC data is present and if the seed matches what we expect
        if (apmcData.state == APMCData.State.VALID && !worldData.getSeedName().equals(apmcData.seed_name)) {
            //check to see if our worldData is empty. If it is, then save the apRoom data.
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
            APClient = new APClient(server, advancementManager, itemManager, goalManager);
        }


        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
        if (worldData.getJailPlayers() && overworld != null) {
            BlockPos spawn = overworld.getSharedSpawnPos();
            // alter the spawn box position, so it doesn't interfere with spawning
            var jailOptional = overworld.getStructureManager().get(ResourceLocation.fromNamespaceAndPath(MODID, "spawnjail"));
            if (jailOptional.isPresent()) {
                StructureTemplate jail = jailOptional.get();
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

            APClient APClient = getAP();
            if (APClient != null) {
                APClient.setName(apmcData.player_name);
                String address = apmcData.server.concat(":" + apmcData.port);

                Utils.sendMessageToAll("Connecting to Archipelago server at " + address);

                try {
                    APClient.connect(address);
                } catch (URISyntaxException e) {
                    Utils.sendMessageToAll("unable to connect");
                }
            }
        }
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        if (APClient != null)
            APClient.close();
    }

    @SubscribeEvent
    public void onServerStopped(ServerStoppedEvent event) {
        if (APClient != null)
            APClient.close();
    }

    //wait for register commands event then register us as a command.
    @SubscribeEvent
    static void onRegisterCommandsEvent(RegisterCommandsEvent event) {
        // i'm assuming this fires after the server loads lol
        new StartCommand(itemManager).Register(event.getDispatcher());
    }
}
