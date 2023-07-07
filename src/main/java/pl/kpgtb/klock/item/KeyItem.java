package pl.kpgtb.klock.item;

import com.github.kpgtb.ktools.manager.item.KItem;
import com.github.kpgtb.ktools.manager.language.LanguageLevel;
import com.github.kpgtb.ktools.util.item.ItemBuilder;
import com.github.kpgtb.ktools.util.item.ItemUtil;
import com.github.kpgtb.ktools.util.wrapper.ToolsObjectWrapper;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;

public class KeyItem extends KItem {
    private final ToolsObjectWrapper wrapper;
    private final NamespacedKey keyIdKey;

    public KeyItem(ToolsObjectWrapper wrapper, String fullItemTag) {
        super(wrapper, fullItemTag);
        this.wrapper = wrapper;
        this.keyIdKey = new NamespacedKey(wrapper.getPlugin(),"key");
    }

    @Override
    public ItemStack getItem() {
        return new ItemBuilder(Material.IRON_NUGGET)
                .displayname(wrapper.getLanguageManager().getSingleString(LanguageLevel.PLUGIN, "keyName"))
                .lore(wrapper.getLanguageManager().getString(LanguageLevel.PLUGIN, "keyLoreAssigned"))
                .model(1006)
                .glow()
                .build();
    }

    @Override
    public boolean isSimilar(ItemStack is) {
        if(is == null || is.getType().equals(Material.AIR)) {
            return false;
        }

        return ItemUtil.compareWithoutPDC(getItem(),is, new ArrayList<>(Arrays.asList(this.keyIdKey)));
    }
}
