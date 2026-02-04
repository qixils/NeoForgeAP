package gg.archipelago.aprandomizer;

import com.google.gson.Gson;
import gg.archipelago.aprandomizer.advancements.APCriteriaTriggers;
import gg.archipelago.aprandomizer.ap.APClient;
import gg.archipelago.aprandomizer.ap.storage.APMCData;
import gg.archipelago.aprandomizer.ap.storage.APMCMetaData;
import gg.archipelago.aprandomizer.attachments.APAttachmentTypes;
import gg.archipelago.aprandomizer.common.Utils.Utils;
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
import io.github.archipelagomw.network.client.BouncePacket;
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

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(APRandomizer.MODID)
public class APRandomizer {
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "aprandomizer";
    private static final byte[] ZIP_HEADER = new byte[]{ 0x50, 0x4B, 0x03, 0x04 };

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
            9 // 1.21.8
    );
    @NotNull
    static private BlockPos jailCenter = BlockPos.ZERO;
    @Nullable
    static private WorldData worldData;

    static private final Gson gson = new Gson();

    private static APMCData readApmcInputStream(InputStream inputStream) throws IOException {
        byte[] bytes = inputStream.readAllBytes();
        String string = new String(bytes, StandardCharsets.UTF_8).trim();
        String jsonString;
        if (string.startsWith("{")) {
            jsonString = string;
        } else {
            byte[] b64 = Base64.getDecoder().decode(string);
            jsonString = new String(b64, StandardCharsets.UTF_8);
        }
        APMCData data = gson.fromJson(jsonString, APMCData.class);
        if (!VALID_VERSIONS.contains(data.client_version)) {
            data.state = APMCData.State.INVALID_VERSION;
        }
        LOGGER.info("Loaded .apmc data");
        return data;
    }

    private static APMCData createApmcData() {
        try {
            Path apDataDir = Paths.get("./APData/");
            if (!Files.exists(apDataDir)) {
                Files.createDirectories(apDataDir);
                LOGGER.info("APData folder missing, creating.");
            }

            Path apmcFile;
            try (Stream<Path> listFiles = Files.list(apDataDir)) {
                // todo: shouldn't this be reversed? we want the newest first right?
                Optional<Path> maybeFile = listFiles
                        .filter(item -> item.getFileName().toString().endsWith(".apmc"))
                        .min(Comparator.comparingLong(path -> {
                            // todo: i just set this up the same as the previous File-based logic but... shouldn't this be `max` not `min`? cus we want the newest first right?
                            try {
                                return Files.getLastModifiedTime(path).toMillis();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }));
                if (maybeFile.isEmpty()) {
                    LOGGER.error("Could not find '.apmc' file. Please place '.apmc' file in './APData/' folder!");
                    return APMCData.createInvalid();
                }
                apmcFile = maybeFile.get();
            }

            boolean isZip = false;
            try (InputStream inputStream = Files.newInputStream(apmcFile)) {
                byte[] header = new byte[ZIP_HEADER.length];
                int read = inputStream.read(header, 0, ZIP_HEADER.length);
                isZip = read >= ZIP_HEADER.length && Arrays.equals(header, ZIP_HEADER);
            }

            APMCData apmc = null;
            APMCMetaData metadata = null;

            if (isZip) {
                try (ZipFile zipFile = new ZipFile(apmcFile.toFile())) {
                    Enumeration<? extends ZipEntry> entries = zipFile.entries();
                    while (entries.hasMoreElements()) {
                        ZipEntry entry = entries.nextElement();
                        if (entry.isDirectory()) continue;

                        if (apmc == null && entry.getName().endsWith(".apmc")) {
                            try (InputStream inputStream = zipFile.getInputStream(entry)) {
                                apmc = readApmcInputStream(inputStream);
                            } catch (IOException e) {
                                // we catch the error here to allow iteration to continue
                                // in case there's a 2nd valid apmc file lmao?
                                LOGGER.warn("Could not read zipped apmc", e);
                            }
                        }

                        if (metadata == null && entry.getName().equals("archipelago.json")) {
                            try (InputStream inputStream = zipFile.getInputStream(entry)) {
                                byte[] bytes = inputStream.readAllBytes();
                                String jsonString = new String(bytes, StandardCharsets.UTF_8).trim();
                                metadata = gson.fromJson(jsonString, APMCMetaData.class);
                            } catch (IOException e) {
                                // see above catch comment
                                LOGGER.warn("Could not read zipped metadata", e);
                            }
                        }

                        if (apmc != null && metadata != null) break;
                    }
                }
            } else {
                try (InputStream inputStream = Files.newInputStream(apmcFile)) {
                    apmc = readApmcInputStream(inputStream);
                }
            }

            if (apmc == null) {
                LOGGER.error("Unable to parse '.apmc'");
                return APMCData.createInvalid();
            }

            if (apmc.server == null && metadata != null) {
                apmc.server = metadata.server();
            }

            LOGGER.info("Finished APMC initialization");
            return apmc;
        } catch (Exception e) {
            LOGGER.error("An unknown error occurred attempting to load '.apmc' data", e);
            return APMCData.createInvalid();
        }
    }

    static {
        apmcData = createApmcData();
    }

    public APRandomizer(IEventBus modEventBus) {
        LOGGER.info("Minecraft Archipelago 1.21.8 v2.0.1 Randomizer initializing.");

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

                String address = apmcData.server;
                if (apmcData.port > 0 && !address.contains(":")) address += ":" + apmcData.port;

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
}
