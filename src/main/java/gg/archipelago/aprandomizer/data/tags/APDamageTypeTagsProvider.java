package gg.archipelago.aprandomizer.data.tags;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.tags.APDamageTypeTags;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.world.damagesource.DamageTypes;

import java.util.concurrent.CompletableFuture;

public class APDamageTypeTagsProvider extends DamageTypeTagsProvider {

    public APDamageTypeTagsProvider(PackOutput output, CompletableFuture<Provider> lookupProvider) {
        super(output, lookupProvider, APRandomizer.MODID);
    }

    @Override
    protected void addTags(Provider p_270108_) {
        tag(APDamageTypeTags.FIREBALL)
                .add(DamageTypes.FIREBALL);
        tag(APDamageTypeTags.FALL)
                .add(DamageTypes.FALL);
    }

}
