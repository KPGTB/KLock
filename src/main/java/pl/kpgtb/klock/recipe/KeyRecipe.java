package pl.kpgtb.klock.recipe;

import com.github.kpgtb.ktools.manager.recipe.KRecipe;
import com.github.kpgtb.ktools.util.wrapper.ToolsObjectWrapper;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import pl.kpgtb.klock.item.EmptyKeyItem;

public class KeyRecipe extends KRecipe {
    private final NamespacedKey recipeKey;
    private final ToolsObjectWrapper wrapper;

    public KeyRecipe(NamespacedKey recipeKey, ToolsObjectWrapper toolsObjectWrapper) {
        super(recipeKey, toolsObjectWrapper);
        this.recipeKey =recipeKey;
        this.wrapper = toolsObjectWrapper;
    }

    @Override
    public Recipe getRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(recipeKey,wrapper.getItemManager().getCustomItem(wrapper.getTag(), EmptyKeyItem.class));
        recipe.shape(
                "  g",
                "gg ",
                "g g"
        );
        recipe.setIngredient('g', Material.GOLD_INGOT);
        return recipe;
    }
}
