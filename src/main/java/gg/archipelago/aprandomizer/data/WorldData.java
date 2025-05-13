package gg.archipelago.aprandomizer.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gg.archipelago.aprandomizer.APRandomizer;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public class WorldData extends SavedData {

    private String seedName = "";
    private int dragonState = ASLEEP;
    private int witherState = ASLEEP;
    private boolean jailPlayers = false;
    private LongSet locations = new LongOpenHashSet();
    private int index = 0;

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
                    Codec.INT.fieldOf("index").forGetter(WorldData::getItemIndex))
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

    public void setDragonKilled() {
        setDragonState(KILLED);
    }

    public int getDragonState() {
        return dragonState;
    }

    public boolean isDragonKilled() {
        return dragonState == KILLED;
    }

    public boolean getJailPlayers() {
        return jailPlayers;
    }

    public void setJailPlayers(boolean jailPlayers) {
        this.jailPlayers = jailPlayers;
        this.setDirty();
    }

    public void addLocation(long location) {
        this.locations.add(location);
        this.setDirty();
    }

    public void addLocations(long[] locations) {
        this.locations.addAll(new LongOpenHashSet(locations));
        this.setDirty();
    }

    public LongSet getLocations() {
        return locations;
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

    private WorldData(String seedName, int dragonState, int witherState, boolean jailPlayers, LongSet locations, int itemIndex) {
        this.seedName = seedName;
        this.dragonState = dragonState;
        this.witherState = witherState;
        this.jailPlayers = jailPlayers;
        this.locations = locations;
        this.index = itemIndex;
    }

    public int getWitherState() {
        return witherState;
    }

    public void setWitherState(int waiting) {
        this.witherState = waiting;
        this.setDirty();
    }

    public void setWitherKilled() {
        setWitherState(KILLED);
    }

    public boolean isWitherKilled() {
        return witherState == KILLED;
    }
}