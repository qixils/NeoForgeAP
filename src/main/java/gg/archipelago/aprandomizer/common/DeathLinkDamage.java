package gg.archipelago.aprandomizer.common;

import gg.archipelago.aprandomizer.APRandomizer;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class DeathLinkDamage extends DamageSource {

    public static final ResourceKey<DamageType> DEATH_LINK = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID, "indirect_magic"));
    private static final Holder<DamageType> damageType = APRandomizer.server().orElseThrow().registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(DeathLinkDamage.DEATH_LINK);

    public DeathLinkDamage() {
        super(damageType);
    }

    @Override
    public @NotNull Component getLocalizedDeathMessage(LivingEntity pLivingEntity) {
        // TODO: i18n
        return Component.literal(Objects.requireNonNullElseGet(pLivingEntity.getDisplayName(), pLivingEntity::getName).getString() + "'s soul was linked to another's fate.");
    }
}
