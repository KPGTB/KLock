package pl.kpgtb.klock;

import com.github.kpgtb.ktools.manager.command.CommandManager;
import com.github.kpgtb.ktools.manager.data.DataManager;
import com.github.kpgtb.ktools.manager.item.ItemManager;
import com.github.kpgtb.ktools.manager.listener.ListenerManager;
import com.github.kpgtb.ktools.manager.recipe.RecipeManager;
import com.github.kpgtb.ktools.manager.resourcepack.ResourcePackManager;
import com.github.kpgtb.ktools.manager.updater.IUpdater;
import com.github.kpgtb.ktools.manager.updater.SpigotUpdater;
import com.github.kpgtb.ktools.manager.updater.UpdaterManager;
import com.github.kpgtb.ktools.util.bstats.Metrics;
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
        saveDefaultConfig();
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
        resourcePack.registerSpaces();

        {
            // GUI
            resourcePack.registerCustomChar(packageUtil.getTag(), "\uE128", "gui.png", getResource("txt/gui.png"), 126,13,176);
            resourcePack.registerCustomModelData(packageUtil.getTag(), 1000, "guilocknotopen.png", getResource("txt/guiLockNotOpen.png"), Material.IRON_NUGGET);
            resourcePack.registerCustomModelData(packageUtil.getTag(), 1001, "guilockopendown.png", getResource("txt/guiLockOpenDown.png"), Material.IRON_NUGGET);
            resourcePack.registerCustomModelData(packageUtil.getTag(), 1002, "guilockopenup.png", getResource("txt/guiLockOpenUp.png"), Material.IRON_NUGGET);
            resourcePack.registerCustomModelData(packageUtil.getTag(), 1003, "guilockready.png", getResource("txt/guiLockReady.png"), Material.IRON_NUGGET);
            resourcePack.registerCustomModelData(packageUtil.getTag(), 1009, "guiinfo.png", getResource("txt/guiInfo.png"), Material.IRON_NUGGET);

            // GUI Buttons
            resourcePack.registerCustomModelData(packageUtil.getTag(), 1004, "guilockpick.png", getResource("txt/guiLockpick.png"), Material.IRON_NUGGET);
            resourcePack.registerCustomModelData(packageUtil.getTag(), 1005, "guiunlock.png", getResource("txt/guiUnlock.png"), Material.IRON_NUGGET);

            // Items
            resourcePack.registerCustomModelData(packageUtil.getTag(), 1006, "key.png", getResource("txt/key.png"), Material.IRON_NUGGET);
            resourcePack.registerCustomModelData(packageUtil.getTag(), 1007, "lock.png", getResource("txt/lock.png"), Material.IRON_NUGGET);
            resourcePack.registerCustomModelData(packageUtil.getTag(), 1008, "lockpick.png", getResource("txt/lockpick.png"), Material.IRON_NUGGET);
        }

        ListenerManager listener = new ListenerManager(wrapper,getFile());
        listener.registerListeners(packageUtil.get("listener"));

        CommandManager command = new CommandManager(wrapper,getFile(),wrapper.getTag());
        command.registerCommands(packageUtil.get("command"));

        UpdaterManager updater = new UpdaterManager(getDescription(), new SpigotUpdater(""), wrapper.getDebugManager());
        updater.checkUpdate();

        new Metrics(this, 19220);
    }

    @Override
    public void onDisable() {
        if(adventure!=null) adventure.close();
    }
}
