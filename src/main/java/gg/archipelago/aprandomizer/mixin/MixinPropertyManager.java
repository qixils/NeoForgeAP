package gg.archipelago.aprandomizer.mixin;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.APStorage.APMCData;
import net.minecraft.server.dedicated.Settings;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.nio.file.Path;
import java.util.Properties;


@Mixin(value = Settings.class, remap = false)
public abstract class MixinPropertyManager {

    @Inject(method = "loadFromFile", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private static void onLoadFromFile(Path pPath, @NotNull CallbackInfoReturnable<Properties> cir) {
        Properties properties = cir.getReturnValue();
        APMCData data = APRandomizer.getApmcData();
        LogManager.getLogger().info("Injecting Archipelago Seed");

        properties.setProperty("level-seed", "" + data.world_seed);
        properties.setProperty("spawn-protection", "0");
        properties.setProperty("level-name", "Archipelago-" + data.seed_name + "-P" + data.player_id);
        properties.setProperty("level-type", "default");
        properties.setProperty("generator-settings", "{}");

        if (data.race) {
            LogManager.getLogger().info("Archipelago race flag found enforcing race settings.");
            properties.setProperty("view-distance", "10");
            properties.setProperty("gamemode", "survival");
            properties.setProperty("force-gamemode", "true");
        }


    }
}
