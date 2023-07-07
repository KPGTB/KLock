package pl.kpgtb.klock.listener;

import com.github.kpgtb.ktools.manager.language.LanguageLevel;
import com.github.kpgtb.ktools.manager.listener.KListener;
import com.github.kpgtb.ktools.util.wrapper.ToolsObjectWrapper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
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
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import pl.kpgtb.klock.data.LockedBlock;
import pl.kpgtb.klock.util.KeyUtil;

import java.sql.SQLException;
import java.util.List;

public class BreakListener extends KListener {
    private final ToolsObjectWrapper wrapper;
    private final Dao<LockedBlock, Location> blocksDAO;

    public BreakListener(ToolsObjectWrapper toolsObjectWrapper) {
        super(toolsObjectWrapper);
        this.wrapper = toolsObjectWrapper;
        this.blocksDAO = wrapper.getDataManager().getDao(LockedBlock.class, Location.class);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) throws SQLException {
        if(event.isCancelled()) return;

        Player player = event.getPlayer();
        Audience audience = wrapper.getAdventure().player(player);
        Block block = event.getBlock();

        BlockState state = block.getState();
        BlockData data = block.getBlockData();

        if(!(data instanceof Openable) && !(state instanceof Container)) {
            return;
        }

        LockedBlock lockedBlock = blocksDAO.queryForId(block.getLocation());
        if(lockedBlock == null) {
            return;
        }

        ItemStack key = null;

        for (ItemStack is : player.getInventory().getContents()) {
            if(is == null || is.getType().equals(Material.AIR)) {
                continue;
            }
            int keyId = wrapper.getCacheManager().getDataOr(is, wrapper.getTag(), "key", -1);
            if(keyId == lockedBlock.getKey()) {
                key = is;
                break;
            }
        }

        if(key == null) {
            wrapper.getLanguageManager().getComponent(LanguageLevel.PLUGIN, "cantUnlock")
                    .forEach(audience::sendMessage);
            event.setCancelled(true);
            return;
        }

        DeleteBuilder<LockedBlock, Location> deleteBuilder = blocksDAO.deleteBuilder();
        deleteBuilder.where().eq("key", lockedBlock.getKey());
        deleteBuilder.delete();

        key.setAmount(0);
    }
}
