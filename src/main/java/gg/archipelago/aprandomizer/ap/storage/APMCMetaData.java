package gg.archipelago.aprandomizer.ap.storage;

public record APMCMetaData(
        String server
        // unused stuff left uncommented to avoid potential json deserialization errors on, well, unused stuff
//        int compatible_version,
//        int version,
//        int player,
//        String player_name,
//        String game,
//        String patch_file_ending
) {
}
