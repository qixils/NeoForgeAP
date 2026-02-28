package gg.archipelago.aprandomizer.tags;

import gg.archipelago.aprandomizer.APRandomizer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

public class APDamageTypeTags {
    public static final TagKey<DamageType> FIREBALL = TagKey.create(Registries.DAMAGE_TYPE, Identifier.fromNamespaceAndPath(APRandomizer.MODID, "fireball"));
    public static final TagKey<DamageType> FALL = TagKey.create(Registries.DAMAGE_TYPE, Identifier.fromNamespaceAndPath(APRandomizer.MODID, "fall"));
}
