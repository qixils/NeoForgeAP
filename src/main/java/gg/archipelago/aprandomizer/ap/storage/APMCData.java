package gg.archipelago.aprandomizer.ap.storage;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;

import gg.archipelago.aprandomizer.ap.storage.APMCData.RequiredBosses;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Map;

public class APMCData {

    @SerializedName("world_seed")
    public long world_seed;
    @SerializedName("structures")
    public Map<String, String> structures;
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
    @SerializedName("immediate_respawn")
    public boolean respawn = true;

    @SerializedName("required_bosses")
    public RequiredBosses required_bosses = RequiredBosses.ENDER_DRAGON;

    @SerializedName("server")
    @Nullable
    public String server;

    @SerializedName("port")
    public int port;

    public State state = State.VALID;

    public boolean dragonStartSpawned() {
        // if our goal is not to kill the dragon, start with the dragon spawned.
        if (!required_bosses.hasDragon())
            return true;
        // if our goal is "fast" and requires no advancements or egg shards then the
        // dragon should start spawned too;
        return advancements_required == 0 && egg_shards_required == 0;
    }

    public enum State {
        VALID, MISSING, INVALID_VERSION, INVALID_SEED
    }

    public enum Bosses {
        ENDER_DRAGON,
        WITHER,
        ELDER_GUARDIAN,
        WARDEN
    }

    public record RequiredBosses(boolean dragon, boolean wither, boolean elderGuardian, boolean warden) {
        public static RequiredBosses ENDER_DRAGON = new RequiredBosses(true, false, false, false);
        public static RequiredBosses WITHER = new RequiredBosses(false, true, false, false);
        public static RequiredBosses BOTH = new RequiredBosses(true, true, false, false);
        public static RequiredBosses ALL = new RequiredBosses(true, true, true, true);

        public boolean hasDragon() {
            return dragon;
        }

        public boolean hasWither() {
            return wither;
        }

        public boolean hasElderGuardian() {
            return elderGuardian;
        }

        public boolean hasWarden() {
            return warden;
        }

        public boolean hasBoss(Bosses boss) {
            return switch (boss) {
                case Bosses.ENDER_DRAGON -> hasDragon();
                case Bosses.WITHER -> hasWither();
                case Bosses.ELDER_GUARDIAN -> hasElderGuardian();
                case Bosses.WARDEN -> hasWarden();
            };
        }
    }

    public static APMCData createInvalid() {
        APMCData data = new APMCData();
        data.state = APMCData.State.MISSING;
        return data;
    }

}
