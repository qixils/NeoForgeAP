package gg.archipelago.aprandomizer.managers.recipemanager;

import gg.archipelago.aprandomizer.APRandomizer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;


public class RecipeManager {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(RecipeManager.class);
    //have a lookup of every advancement
    private final RecipeData recipeData;

    private final Set<RecipeHolder<?>> initialRestricted = new HashSet<>();
    private final Set<RecipeHolder<?>> initialGranted = new HashSet<>();
    private final Set<RecipeHolder<?>> restricted = new HashSet<>();
    private final Set<RecipeHolder<?>> granted = new HashSet<>();


    public RecipeManager() {
        recipeData = new RecipeData();
        APRandomizer.server().ifPresent(server -> {
            Collection<RecipeHolder<?>> recipeList = server.getRecipeManager().getRecipes();
            for (RecipeHolder<?> iRecipe : recipeList) {
                if (recipeData.injectIRecipe(iRecipe)) {
                    log.trace("Restricting {}", iRecipe);
                    initialRestricted.add(iRecipe);
                } else {
                    log.trace("Granting {}", iRecipe);
                    initialGranted.add(iRecipe);
                }
            }
        });
        restricted.addAll(initialRestricted);
        granted.addAll(initialGranted);
    }

    public boolean grantRecipeList(List<Long> recipes) {
        for (var id : recipes) {
            if (!recipeData.hasID(id))
                continue;
            Set<RecipeHolder<?>> toGrant = recipeData.getID(id).getGrantedRecipes();
            log.trace("Granting and restricting {}", toGrant);
            granted.addAll(toGrant);
            restricted.removeAll(toGrant);
        }
        APRandomizer.server().ifPresent(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                player.resetRecipes(restricted);
                player.awardRecipes(granted);
            }
        });
        return true;
    }

    public void grantRecipe(long id) {
        if (!recipeData.hasID(id))
            return;
        Set<RecipeHolder<?>> toGrant = recipeData.getID(id).getGrantedRecipes();
        log.trace("Granting and restricting {}", toGrant);

        granted.addAll(toGrant);
        restricted.removeAll(toGrant);

        APRandomizer.server().ifPresent(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                player.resetRecipes(restricted);
                player.awardRecipes(granted);
            }
        });
    }

    public Set<RecipeHolder<?>> getRestrictedRecipes() {
        return restricted;
    }

    public Set<RecipeHolder<?>> getGrantedRecipes() {
        return granted;
    }

    public Stream<ResourceKey<Recipe<?>>> getGrantedRecipeKeys() {
        return granted.stream().map(RecipeHolder::id);
    }

    public void resetRecipes() {
        log.info("Resetting recipes");
        log.trace("Clearing restricted from {} to {}", restricted, initialRestricted);
        restricted.clear();
        restricted.addAll(initialRestricted);
        log.trace("Clearing granted from {} to {}", granted, initialGranted);
        granted.clear();
        granted.addAll(initialGranted);
        recipeData.reset();
    }
}
