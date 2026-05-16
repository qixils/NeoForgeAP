package gg.archipelago.aprandomizer.attachments;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gg.archipelago.aprandomizer.APRegistries;
import gg.archipelago.aprandomizer.items.APItem;
import gg.archipelago.aprandomizer.items.CompassReward;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import java.util.HashMap;
import java.util.Map;

public class APPlayerAttachment {

    public static final MapCodec<APPlayerAttachment> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(
                    Codec.INT
                            .fieldOf("index").forGetter(APPlayerAttachment::getIndex),
                    Codec.unboundedMap(Identifier.CODEC, CompassReward.CODEC)
                            .fieldOf("compass_rewards").forGetter(APPlayerAttachment::getUnlockedCompassRewards),
                    Codec.unboundedMap(ResourceKey.codec(APRegistries.ARCHIPELAGO_ITEM), Codec.INT)//.<Object2IntMap<ResourceKey<APItem>>>xmap(Object2IntOpenHashMap::new, Function.identity())
                            .fieldOf("tiers").forGetter(APPlayerAttachment::getTiers)
            )
            .apply(instance, APPlayerAttachment::new));

    private int index = 0;
    private Map<Identifier, CompassReward> compassRewards = new HashMap<>();
    private Object2IntMap<ResourceKey<APItem>> tiers = new Object2IntOpenHashMap<>();

    public APPlayerAttachment() {
    }

    public APPlayerAttachment(int index, Map<Identifier, CompassReward> compassRewards, Map<ResourceKey<APItem>, Integer> tiers) {
        this.index = index;
        this.compassRewards = new HashMap<>(compassRewards);
        this.tiers = new Object2IntOpenHashMap<>(tiers);
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Map<Identifier, CompassReward> getUnlockedCompassRewards() {
        return compassRewards;
    }

    public Object2IntMap<ResourceKey<APItem>> getTiers() {
        return tiers;
    }
}
