package gg.archipelago.aprandomizer.common.Utils;

import gg.archipelago.aprandomizer.APRandomizer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;

public final class UnlockableHearts {
    public static final double STARTING_MAX_HEALTH = 2.0D;
    public static final double HEART_HEALTH = 2.0D;
    public static final double VANILLA_MAX_HEALTH = 20.0D;

    private UnlockableHearts() {
    }

    public static boolean enabled() {
        return APRandomizer.getApmcData().unlockable_hearts;
    }

    public static void initialize(ServerPlayer player) {
        if (!enabled()) return;
        setMaxHealth(player, STARTING_MAX_HEALTH);
        player.setHealth((float) STARTING_MAX_HEALTH);
    }

    public static void grantHeart(ServerPlayer player) {
        if (!enabled()) return;
        AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth == null) return;
        double newMaxHealth = Math.min(VANILLA_MAX_HEALTH, maxHealth.getBaseValue() + HEART_HEALTH);
        setMaxHealth(player, newMaxHealth);
        player.setHealth((float) Math.min(newMaxHealth, player.getHealth() + HEART_HEALTH));
    }

    private static void setMaxHealth(ServerPlayer player, double health) {
        AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth == null) return;
        maxHealth.setBaseValue(health);
        if (player.getHealth() > health) {
            player.setHealth((float) health);
        }
    }
}
