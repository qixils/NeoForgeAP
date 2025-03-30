package gg.archipelago.aprandomizer.data;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.items.CompassReward;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class WorldData extends SavedData {


    private String seedName = "";
    private int dragonState = ASLEEP;
    private int witherState = ASLEEP;
    private boolean jailPlayers = true;
    private LongSet locations = new LongOpenHashSet();
    private int index = 0;
    private Object2IntMap<String> playerIndex = new Object2IntOpenHashMap<>();
    private List<CompassReward> compassRewards = new ArrayList<>();

    public static final int KILLED = 30;
    public static final int SPAWNED = 20;
    public static final int WAITING = 15;
    public static final int ASLEEP = 10;

    public static final Codec<WorldData> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(
                    Codec.STRING.fieldOf("seedName").forGetter(WorldData::getSeedName),
                    Codec.INT.fieldOf("dragonState").forGetter(WorldData::getDragonState),
                    Codec.INT.optionalFieldOf("witherState", ASLEEP).forGetter(WorldData::getWitherState),
                    Codec.BOOL.fieldOf("jailPlayers").forGetter(WorldData::getJailPlayers),
                    Codec.LONG_STREAM.<LongSet>xmap(stream -> new LongOpenHashSet(stream.toArray()), LongSet::longStream).fieldOf("locations").forGetter(WorldData::getLocations),
                    Codec.INT.fieldOf("index").forGetter(WorldData::getItemIndex),
                    Codec.unboundedMap(Codec.STRING, Codec.INT).<Object2IntMap<String>>xmap(Object2IntOpenHashMap::new, Function.identity()).fieldOf("playerIndex").forGetter(data -> data.playerIndex),
                    CompassReward.CODEC.listOf().fieldOf("compass_rewards").forGetter(WorldData::getUnlockedCompassRewards))
            .apply(instance, WorldData::new));

    public void setSeedName(String seedName) {
        this.seedName = seedName;
        this.setDirty();
    }

    public String getSeedName() {
        return seedName;
    }

    public void setDragonState(int dragonState) {
        this.dragonState = dragonState;
        this.setDirty();
    }

    public int getDragonState() {
        return dragonState;
    }

    public boolean getJailPlayers() {
        return jailPlayers;
    }

    public void setJailPlayers(boolean jailPlayers) {
        this.jailPlayers = jailPlayers;
        this.setDirty();
    }

    public void addLocation(Long location) {
        this.locations.add(location);
        this.setDirty();
    }

    public void addLocations(Long[] locations) {
        this.locations.addAll(Lists.newArrayList(Arrays.stream(locations).iterator()));
        this.setDirty();
    }

    public LongSet getLocations() {
        return locations;
    }

    public void updatePlayerIndex(String playerUUID, int index) {
        playerIndex.put(playerUUID, index);
        this.setDirty();
    }

    public int getPlayerIndex(String playerUUID) {
        return playerIndex.getOrDefault(playerUUID, 0);
    }

    public int getItemIndex() {
        return this.index;
    }

    public void setItemIndex(int index) {
        this.index = index;
        this.setDirty();
    }

    public static SavedDataType<WorldData> getFactory() {
        return new SavedDataType<>(APRandomizer.MODID, WorldData::new, WorldData.CODEC);
    }

    public WorldData() {
    }

    private WorldData(String seedName, int dragonState, int witherState, boolean jailPlayers, LongSet locations, int itemIndex, Object2IntMap<String> playerIndex, List<CompassReward> compassRewards) {
        this.seedName = seedName;
        this.dragonState = dragonState;
        this.witherState = witherState;
        this.jailPlayers = jailPlayers;
        this.locations = locations;
        this.index = itemIndex;
        this.playerIndex = playerIndex;
        this.compassRewards = compassRewards;
    }

    public int getWitherState() {
        return witherState;
    }

    public void setWitherState(int waiting) {
        this.witherState = waiting;
        this.setDirty();
    }

    public List<CompassReward> getUnlockedCompassRewards() {
        return Collections.unmodifiableList(compassRewards);
    }

    public void unlockCompassReward(CompassReward reward) {
        compassRewards.add(reward);
    }
}