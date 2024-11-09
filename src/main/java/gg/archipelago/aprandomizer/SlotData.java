package gg.archipelago.aprandomizer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public class SlotData {

    public int include_hard_advancements;
    public int include_insane_advancements;
    public int include_postgame_advancements;
    public int advancement_goal;
    public long minecraft_world_seed;
    public int client_version;

    @SerializedName("MC35")
    public boolean MC35 = false;

    @SerializedName("death_link")
    public boolean deathlink = false;

    @SerializedName("starting_items")
    public String startingItems;

    transient public final List<ItemStack> startingItemStacks = new ArrayList<>();

    public boolean getMC35() {
        return MC35;
    }

    public boolean getDeath_link() {
        return deathlink;
    }

    public int getInclude_hard_advancements() {
        return include_hard_advancements;
    }

    public int getClient_version() {
        return client_version;
    }

    public long getMinecraft_world_seed() {
        return minecraft_world_seed;
    }

    public int getAdvancement_goal() {
        return advancement_goal;
    }

    public int getInclude_postgame_advancements() {
        return include_postgame_advancements;
    }

    public int getInclude_insane_advancements() {
        return include_insane_advancements;
    }

    public void parseStartingItems() {
        JsonArray si = JsonParser.parseString(startingItems).getAsJsonArray();
        for (JsonElement jsonItem : si) {
            JsonObject object = jsonItem.getAsJsonObject();
            String itemName = object.getAsJsonObject().get("item").getAsString();

            int amount = object.has("amount") ? object.get("amount").getAsInt() : 1;

            try {
                Item item = BuiltInRegistries.ITEM.getValue(ResourceLocation.parse(itemName));

                //air is the default item returned if the resource name is invalid.
                if (item == Items.AIR) {
                    Utils.sendMessageToAll("No such item \"" + itemName + "\"");
                    continue;
                }

                ItemStack iStack = new ItemStack(item, amount);

                //todo: figure out starting inventory NBT
//                if(object.has("nbt"))
//                    iStack.set(TagParser.parseTag(object.get("nbt").getAsString()));

                startingItemStacks.add(iStack);

//            } catch (CommandSyntaxException e) {
//                Utils.sendMessageToAll("NBT error in starting item " + itemName);
            } catch (ResourceLocationException e) {
                Utils.sendMessageToAll("No such item \"" + itemName + "\"");
            }
        }
    }
}
