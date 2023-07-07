package pl.kpgtb.klock.recipe;

import com.github.kpgtb.ktools.manager.recipe.KRecipe;
import com.github.kpgtb.ktools.util.wrapper.ToolsObjectWrapper;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import pl.kpgtb.klock.item.EmptyKeyItem;
import pl.kpgtb.klock.item.LockPickItem;

public class LockPickRecipe extends KRecipe {
    private final NamespacedKey recipeKey;
    private final ToolsObjectWrapper wrapper;

    public LockPickRecipe(NamespacedKey recipeKey, ToolsObjectWrapper toolsObjectWrapper) {
        super(recipeKey, toolsObjectWrapper);
        this.recipeKey =recipeKey;
        this.wrapper = toolsObjectWrapper;
    }

    @Override
    public Recipe getRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(recipeKey,wrapper.getItemManager().getCustomItem(wrapper.getTag(), LockPickItem.class));
        recipe.shape(
                "ii ",
                "i  ",
                "i  "
        );
        recipe.setIngredient('i', Material.IRON_INGOT);
        return recipe;
    }
}
