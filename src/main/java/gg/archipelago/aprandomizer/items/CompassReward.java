package gg.archipelago.aprandomizer.items;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gg.archipelago.aprandomizer.attachments.APAttachmentTypes;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import gg.archipelago.aprandomizer.managers.itemmanager.ItemManager;
import gg.archipelago.aprandomizer.structures.level.StructureLevelReference;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.List;
import java.util.Optional;

public record CompassReward(TagKey<Structure> structures, StructureLevelReference level, Component name) implements APReward {

    public static final MapCodec<CompassReward> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(
                    TagKey.hashedCodec(Registries.STRUCTURE).fieldOf("structures").forGetter(CompassReward::structures),
                    StructureLevelReference.CODEC.fieldOf("level").forGetter(CompassReward::level),
                    ComponentSerialization.CODEC.fieldOf("name").forGetter(CompassReward::name))
            .apply(instance, CompassReward::new));

    public static final Codec<CompassReward> CODEC = MAP_CODEC.codec();

    private static final ItemStack DEFAULT_COMPASS = new ItemStack(BuiltInRegistries.ITEM.wrapAsHolder(Items.COMPASS), 1, DataComponentPatch.builder()
            .set(DataComponents.LODESTONE_TRACKER, new LodestoneTracker(Optional.empty(), false))
            .set(DataComponents.ITEM_NAME, Component.literal("uninitialized structure compass"))
            .set(DataComponents.LORE, new ItemLore(List.of(
                    Component.literal("oops, this compass is broken."),
                    Component.literal("right click with compass in hand to fix."),
                    Component.literal("hopefully it will point to the right place."),
                    Component.literal("no guarantees."))))
            .build());

    @Override
    public MapCodec<? extends APReward> codec() {
        return MAP_CODEC;
    }

    @Override
    public void give(ServerPlayer player) {
        List<CompassReward> compassRewards = player.getData(APAttachmentTypes.AP_PLAYER).getUnlockedCompassRewards();
        compassRewards.add(this);

        ItemStack compass = DEFAULT_COMPASS.copy();
        ItemManager.updateCompassLocation(this, player, compass);
        CompoundTag tag = compass.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        tag.putInt("index", compassRewards.size() - 1);
        compass.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        Utils.giveItemToPlayer(player, compass);
        ItemManager.cleanCompasses(player);
    }

}
