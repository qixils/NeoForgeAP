package gg.archipelago.aprandomizer.ap.storage;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

public class APMCData {

    @SerializedName("world_seed")
    public long world_seed;
    @SerializedName("structures")
    public HashMap<String, String> structures;
    @SerializedName("seed_name")
    public String seed_name;
    @SerializedName("player_name")
    public String player_name;
    @SerializedName("player_id")
    public int player_id;
    @SerializedName("client_version")
    public int client_version;
    @SerializedName("race")
    public boolean race = false;
    @SerializedName("egg_shards_required")
    public int egg_shards_required = -1;
    @SerializedName("egg_shards_available")
    public int egg_shards_available = -1;
    @SerializedName("advancement_goal")
    public int advancements_required = -1;

    @SerializedName("required_bosses")
    public Bosses required_bosses = Bosses.ENDER_DRAGON;

    @SerializedName("server")
    public String server;

    @SerializedName("port")
    public int port;

    public State state = State.VALID;

    public boolean dragonStartSpawned() {
        //if our goal is not to kill the dragon, start with the dragon spawned.
        if (required_bosses == Bosses.NONE || required_bosses == Bosses.WITHER)
            return true;
        //if our goal is "fast" and requires no advancements or egg shards then the dragon should start spawned too;
        return advancements_required == 0 && egg_shards_required == 0;
    }

    public enum State {
        VALID, MISSING, INVALID_VERSION, INVALID_SEED
    }

    public enum Bosses {
        @SerializedName("none")
        NONE(false, false),
        @SerializedName("ender_dragon")
        ENDER_DRAGON(true, false),
        @SerializedName("wither")
        WITHER(false, true),
        @SerializedName("both")
        BOTH(true, true);

        private final boolean dragon;
        private final boolean wither;

        Bosses(boolean dragon, boolean wither) {
            this.dragon = dragon;
            this.wither = wither;
        }

        public boolean hasDragon() {
            return dragon;
        }

        public boolean hasWither() {
            return wither;
        }
    }

}
