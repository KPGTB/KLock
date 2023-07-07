package pl.kpgtb.klock.item;

import com.github.kpgtb.ktools.manager.item.KItem;
import com.github.kpgtb.ktools.manager.language.LanguageLevel;
import com.github.kpgtb.ktools.util.item.ItemBuilder;
import com.github.kpgtb.ktools.util.wrapper.ToolsObjectWrapper;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class EmptyKeyItem extends KItem {
    private final ToolsObjectWrapper wrapper;

    public EmptyKeyItem(ToolsObjectWrapper wrapper, String fullItemTag) {
        super(wrapper, fullItemTag);
        this.wrapper = wrapper;
    }

    @Override
    public ItemStack getItem() {
        return new ItemBuilder(Material.IRON_NUGGET)
                .displayname(wrapper.getLanguageManager().getSingleString(LanguageLevel.PLUGIN, "keyName"))
                .lore(wrapper.getLanguageManager().getString(LanguageLevel.PLUGIN, "keyLoreNotAssigned"))
                .model(1006)
                .build();
    }
}
