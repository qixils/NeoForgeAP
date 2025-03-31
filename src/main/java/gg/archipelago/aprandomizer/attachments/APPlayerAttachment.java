package gg.archipelago.aprandomizer.attachments;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gg.archipelago.aprandomizer.APRegistries;
import gg.archipelago.aprandomizer.items.APItem;
import gg.archipelago.aprandomizer.items.CompassReward;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.resources.ResourceKey;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class APPlayerAttachment {

    public static final Codec<APPlayerAttachment> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(
                    Codec.INT.fieldOf("index").forGetter(APPlayerAttachment::getIndex),
                    CompassReward.CODEC.listOf().fieldOf("compass_rewards").forGetter(APPlayerAttachment::getUnlockedCompassRewards),
                    Codec.unboundedMap(ResourceKey.codec(APRegistries.ARCHIPELAGO_ITEM), Codec.INT).<Object2IntMap<ResourceKey<APItem>>>xmap(Object2IntOpenHashMap::new, Function.identity()).fieldOf("tiers").forGetter(APPlayerAttachment::getTiers))
            .apply(instance, APPlayerAttachment::new));

    private int index = 0;
    private List<CompassReward> compassRewards = new ArrayList<>();
    private Object2IntMap<ResourceKey<APItem>> tiers = new Object2IntOpenHashMap<>();

    public APPlayerAttachment() {
    }

    public APPlayerAttachment(int index, List<CompassReward> compassRewards, Object2IntMap<ResourceKey<APItem>> tiers) {
        this.index = index;
        this.compassRewards = new ArrayList<>(compassRewards);
        this.tiers = tiers;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<CompassReward> getUnlockedCompassRewards() {
        return compassRewards;
    }

    public Object2IntMap<ResourceKey<APItem>> getTiers() {
        return tiers;
    }
}
