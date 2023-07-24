package pl.kpgtb.klock.item;

import com.github.kpgtb.ktools.manager.item.KItem;
import com.github.kpgtb.ktools.manager.language.LanguageLevel;
import com.github.kpgtb.ktools.util.item.ItemBuilder;
import com.github.kpgtb.ktools.util.item.ItemUtil;
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
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import pl.kpgtb.klock.data.LockedBlock;
import pl.kpgtb.klock.util.KeyUtil;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class LockItem extends KItem {
    private final ToolsObjectWrapper wrapper;
    private final Dao<LockedBlock, Location> blocksDAO;

    public LockItem(ToolsObjectWrapper wrapper, String fullItemTag) {
        super(wrapper, fullItemTag);
        this.wrapper = wrapper;
        this.blocksDAO = wrapper.getDataManager().getDao(LockedBlock.class, Location.class);
    }

    @Override
    public ItemStack getItem() {
        return new ItemBuilder(Material.IRON_NUGGET)
                .displayname(wrapper.getLanguageManager().getSingleString(LanguageLevel.PLUGIN, "lockName"))
                .lore(wrapper.getLanguageManager().getString(LanguageLevel.PLUGIN, "lockLore"))
                .model(1007)
                .build();
    }

    @Override
    public void onUse(PlayerInteractEvent event) {
        ItemStack is = event.getItem();
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

        is.setAmount(is.getAmount() - 1);

        List<Location> locations = KeyUtil.getAllKeyLocations(block);

        AtomicBoolean exists = new AtomicBoolean(false);

        locations.forEach(loc -> {
            try {
                if(blocksDAO.idExists(loc)) {
                    exists.set(true);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        if(exists.get()) {
            wrapper.getLanguageManager().getComponent(LanguageLevel.PLUGIN, "cantLock")
                    .forEach(audience::sendMessage);
            return;
        }

        int keyId = KeyUtil.getNextKeyId(wrapper);

        locations.forEach(loc -> {
            try {
                blocksDAO.create(new LockedBlock(loc,keyId));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        ItemStack key = wrapper.getItemManager().getCustomItem(wrapper.getTag(), KeyItem.class);
        wrapper.getCacheManager().setData(key, wrapper.getTag(), "key", keyId);

        ItemUtil.giveItemToPlayer(player,key);
        wrapper.getLanguageManager().getComponent(LanguageLevel.PLUGIN, "objectLocked")
                .forEach(audience::sendMessage);

        event.setCancelled(true);
    }
}
