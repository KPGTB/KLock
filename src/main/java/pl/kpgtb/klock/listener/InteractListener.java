package pl.kpgtb.klock.listener;

import com.github.kpgtb.ktools.manager.language.LanguageLevel;
import com.github.kpgtb.ktools.manager.listener.KListener;
import com.github.kpgtb.ktools.util.wrapper.ToolsObjectWrapper;
import com.j256.ormlite.dao.Dao;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Openable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import pl.kpgtb.klock.data.LockedBlock;
import pl.kpgtb.klock.item.LockPickItem;

import java.sql.SQLException;

public class InteractListener extends KListener {
    private final ToolsObjectWrapper wrapper;
    private final Dao<LockedBlock, Location> blocksDAO;

    public InteractListener(ToolsObjectWrapper toolsObjectWrapper) {
        super(toolsObjectWrapper);
        this.wrapper = toolsObjectWrapper;
        this.blocksDAO = wrapper.getDataManager().getDao(LockedBlock.class, Location.class);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) throws SQLException {
        Player player = event.getPlayer();
        Audience audience = wrapper.getAdventure().player(player);
        if(!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        Block block = event.getClickedBlock();

        BlockState state = block.getState();
        BlockData data = block.getBlockData();

        if(!(data instanceof Openable) && !(state instanceof Container)) {
            return;
        }

        LockedBlock lockedBlock = blocksDAO.queryForId(block.getLocation());
        if(lockedBlock == null) {
            return;
        }

        ItemStack is = player.getInventory().getItemInMainHand();

        if(is == null || is.getType().equals(Material.AIR)) {
            wrapper.getLanguageManager().getComponent(LanguageLevel.PLUGIN, "cantUnlock")
                    .forEach(audience::sendMessage);
            event.setCancelled(true);
            return;
        }

        int keyId = wrapper.getCacheManager().getDataOr(is, wrapper.getTag(), "key", -1);

        if(keyId == lockedBlock.getKey()) {
            return;
        }

        if(!wrapper.getItemManager().getCustomItem(wrapper.getTag(), LockPickItem.class).isSimilar(is)) {
            wrapper.getLanguageManager().getComponent(LanguageLevel.PLUGIN, "cantBreak")
                    .forEach(audience::sendMessage);
        }
        event.setCancelled(true);
    }
}
