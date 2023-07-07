package pl.kpgtb.klock;

import com.github.kpgtb.ktools.manager.data.DataManager;
import com.github.kpgtb.ktools.manager.item.ItemManager;
import com.github.kpgtb.ktools.manager.listener.ListenerManager;
import com.github.kpgtb.ktools.manager.recipe.RecipeManager;
import com.github.kpgtb.ktools.manager.resourcepack.ResourcePackManager;
import com.github.kpgtb.ktools.util.file.PackageUtil;
import com.github.kpgtb.ktools.util.wrapper.ToolsInitializer;
import com.github.kpgtb.ktools.util.wrapper.ToolsObjectWrapper;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

public final class KLock extends JavaPlugin {

    private BukkitAudiences adventure;

    @Override
    public void onEnable() {
        ToolsObjectWrapper wrapper = new ToolsObjectWrapper(new ToolsInitializer(this).prepareLanguage(getConfig().getString("lang"), "en"));
        adventure = wrapper.getAdventure();
        PackageUtil packageUtil = wrapper.getPackageUtil();

        DataManager data = wrapper.getDataManager();
        data.registerTables(packageUtil.get("data"), getFile());

        ItemManager item = wrapper.getItemManager();
        item.registerItems(wrapper,getFile(),packageUtil.getTag(),packageUtil.get("item"));

        RecipeManager recipe = new RecipeManager(wrapper,getFile(), packageUtil.getTag());
        recipe.registerRecipes(packageUtil.get("recipe"));

        ResourcePackManager resourcePack = wrapper.getResourcePackManager();
        resourcePack.setRequired(true);
        resourcePack.registerPlugin(packageUtil.getTag(),getDescription().getVersion());

        {
            // GUI
            resourcePack.registerCustomModelData(packageUtil.getTag(), 999, "gui", getResource("txt/gui.png"), Material.IRON_NUGGET);
            resourcePack.registerCustomModelData(packageUtil.getTag(), 1000, "guilocknotopen", getResource("txt/guiLockNotOpen.png"), Material.IRON_NUGGET);
            resourcePack.registerCustomModelData(packageUtil.getTag(), 1001, "guilockopendown", getResource("txt/guiLockOpenDown.png"), Material.IRON_NUGGET);
            resourcePack.registerCustomModelData(packageUtil.getTag(), 1002, "guilockopenup", getResource("txt/guiLockOpenUp.png"), Material.IRON_NUGGET);
            resourcePack.registerCustomModelData(packageUtil.getTag(), 1003, "guilockready", getResource("txt/guiLockReady.png"), Material.IRON_NUGGET);

            // GUI Buttons
            resourcePack.registerCustomModelData(packageUtil.getTag(), 1004, "guilockpick", getResource("txt/guiLockpick.png"), Material.IRON_NUGGET);
            resourcePack.registerCustomModelData(packageUtil.getTag(), 1005, "guiunlock", getResource("txt/guiUnlock.png"), Material.IRON_NUGGET);

            // Items
            resourcePack.registerCustomModelData(packageUtil.getTag(), 1006, "key", getResource("txt/key.png"), Material.IRON_NUGGET);
            resourcePack.registerCustomModelData(packageUtil.getTag(), 1007, "lock", getResource("txt/lock.png"), Material.IRON_NUGGET);
            resourcePack.registerCustomModelData(packageUtil.getTag(), 1008, "lockpick", getResource("txt/lockpick.png"), Material.IRON_NUGGET);
        }

        ListenerManager listener = new ListenerManager(wrapper,getFile());
        listener.registerListeners(packageUtil.get("listener"));
    }

    @Override
    public void onDisable() {
        if(adventure!=null) adventure.close();
    }
}
