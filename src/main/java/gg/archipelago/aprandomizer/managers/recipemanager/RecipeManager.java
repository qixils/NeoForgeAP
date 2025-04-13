package gg.archipelago.aprandomizer.managers.recipemanager;

import gg.archipelago.aprandomizer.APRandomizer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class RecipeManager {

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    //have a lookup of every advancement
    private final RecipeData recipeData;

    private final Set<RecipeHolder<?>> initialRestricted = new HashSet<>();
    private final Set<RecipeHolder<?>> initialGranted = new HashSet<>();

    private Set<RecipeHolder<?>> restricted = new HashSet<>();
    private Set<RecipeHolder<?>> granted = new HashSet<>();

    private Set<ResourceLocation> itemAdvancements = new HashSet<>();


    public RecipeManager() {
        recipeData = new RecipeData();
        Collection<RecipeHolder<?>> recipeList = APRandomizer.getServer().getRecipeManager().getRecipes();
        for (RecipeHolder<?> iRecipe : recipeList) {
            if (recipeData.injectIRecipe(iRecipe)) {
                initialRestricted.add(iRecipe);
            } else {
                initialGranted.add(iRecipe);
            }
        }
        restricted = initialRestricted;
        granted = initialGranted;
    }

    public void grantRecipeList(List<Long> recipes) {
        for (var id : recipes) {
            if (!recipeData.hasID(id))
                continue;
            grantRecipe(id);
        }
    }

    public void grantRecipe(long id) {
        if (!recipeData.hasID(id))
            return;
        Set<RecipeHolder<?>> toGrant = recipeData.getID(id).getGrantedRecipes();

        granted.addAll(toGrant);
        restricted.removeAll(toGrant);
        itemAdvancements.addAll(recipeData.getID(id).getUnlockedTrackingAdvancements());

        for (ServerPlayer player : APRandomizer.getServer().getPlayerList().getPlayers()) {
            //player.resetRecipes(restricted);
            player.awardRecipes(granted);

            var serverAdvancements = APRandomizer.getServer().getAdvancements();
            recipeData.getID(id).getUnlockedTrackingAdvancements().forEach(
                    location -> {
                        var trackingAdvancement = serverAdvancements.get(location);
                        if (trackingAdvancement != null) {
                            APRandomizer.getAdvancementManager().syncAdvancement(trackingAdvancement);
                        }

                    });
        }
    }

    public Set<RecipeHolder<?>> getRestrictedRecipes() {
        return restricted;
    }

    public Set<RecipeHolder<?>> getGrantedRecipes() {
        return granted;
    }

    public void resetRecipes() {
        restricted = initialRestricted;
        granted = initialGranted;
        recipeData.reset();
    }

    public boolean hasReceived(ResourceLocation id) {
        return itemAdvancements.contains(id);
    }
}
