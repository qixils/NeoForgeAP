package gg.archipelago.aprandomizer.common.Utils;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import gg.archipelago.aprandomizer.ap.storage.APMCData;
import gg.archipelago.aprandomizer.ap.storage.APMCData.RequiredBosses;

public class RequiredBossesDeserializer implements JsonDeserializer<APMCData.RequiredBosses> {

    @Override
    public RequiredBosses deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        boolean dragon = false;
        boolean wither = false;
        boolean elderGuardian = false;
        boolean warden = false;
        // handle old single-string format, from before elder guardian and wither were
        // added as required boss options
        if (!json.isJsonArray()) {
            String bossName = json.getAsString();
            switch (bossName) {
                case "dragon":
                    dragon = true;
                    break;
                case "wither":
                    wither = true;
                    break;
                case "both":
                    dragon = true;
                    wither = true;
                    break;
                case "none":
                    break;
                default:
                    throw new JsonParseException("invalid boss key: " + bossName);
            }
        } else {
            for (JsonElement boss : json.getAsJsonArray()) {
                String bossName = boss.getAsString();
                switch (bossName) {
                    case "Ender Dragon":
                        dragon = true;
                        break;
                    case "Wither":
                        wither = true;
                        break;
                    case "Elder Guardian":
                        elderGuardian = true;
                        break;
                    case "Warden":
                        warden = true;
                        break;
                    default:
                        throw new JsonParseException("invalid boss key: " + bossName);
                }
            }
        }
        return new RequiredBosses(dragon, wither, elderGuardian, warden);
    }

}
