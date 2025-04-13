package gg.archipelago.aprandomizer.data;

import com.google.common.collect.Lists;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class WorldData extends SavedData {

    private String seedName = "";
    private int dragonState = ASLEEP;
    private int witherState = ASLEEP;
    private boolean jailPlayers = false;
    private Set<Long> locations = new HashSet<>();
    private int index = 0;
    private Map<String, Integer> playerIndex = new HashMap<>();

    public static final int KILLED = 30;
    public static final int SPAWNED = 20;
    public static final int WAITING = 15;
    public static final int ASLEEP = 10;

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

    public Set<Long> getLocations() {
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

    @Override
    public @NotNull CompoundTag save(CompoundTag tag, HolderLookup.@NotNull Provider holder) {
        tag.putString("seedName", seedName);
        tag.putInt("dragonState", dragonState);
        tag.putInt("witherState", witherState);
        tag.putBoolean("jailPlayers", jailPlayers);
        tag.putLongArray("locations",locations.stream().toList());
        tag.putLong("index", index);
        CompoundTag tagIndex = new CompoundTag();
        this.playerIndex.forEach(tagIndex::putLong);
        tag.put("playerIndex", tagIndex);
        return tag;
    }

    public static SavedData.Factory<WorldData> factory() {
        return new SavedData.Factory<>(WorldData::new, WorldData::load, null);
    }

    public WorldData() {
    }

    private WorldData(String seedName, int dragonState, boolean jailPlayers, long[] locations, Map<String, Integer> playerIndex, int itemIndex) {
        this.seedName = seedName;
        this.dragonState = dragonState;
        this.jailPlayers = jailPlayers;
        this.locations = new HashSet<>(Set.of(ArrayUtils.toObject(locations)));
        this.index = itemIndex;
        this.playerIndex = playerIndex;
    }

    public static WorldData load(CompoundTag tag, HolderLookup.Provider provider) {
        CompoundTag indexTag = tag.getCompound("playerIndex");
        HashMap<String, Integer> indexMap = new HashMap<>();
        indexTag.getAllKeys().forEach(key -> indexMap.put(key, indexTag.getInt(key)));
        return new WorldData(
                tag.getString("seedName"),
                tag.getInt("dragonState"),
                tag.getBoolean("jailPlayers"),
                tag.getLongArray("locations"),
                indexMap,
                tag.getInt("index")
                );
    }

    public int getWitherState() {
        return witherState;
    }

    public void setWitherState(int waiting) {
        this.witherState = waiting;
        this.setDirty();
    }
}