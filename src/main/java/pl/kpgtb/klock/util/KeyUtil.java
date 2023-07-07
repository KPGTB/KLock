package pl.kpgtb.klock.util;

import com.github.kpgtb.ktools.util.wrapper.ToolsObjectWrapper;
import com.j256.ormlite.dao.Dao;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Door;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import pl.kpgtb.klock.data.LockedBlock;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class KeyUtil {

    public static int getNextKeyId(ToolsObjectWrapper wrapper) {
        Dao<LockedBlock, Location> blocksDAO = wrapper.getDataManager().getDao(LockedBlock.class, Location.class);
        Optional<LockedBlock> lastKey;
        try {
            lastKey = blocksDAO.queryBuilder()
                    .orderBy("key", false)
                    .limit(1L)
                    .query().stream().findFirst();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return lastKey.map(lockedBlock -> lockedBlock.getKey() + 1).orElse(0);
    }

    public static List<Location> getAllKeyLocations(Block block) {
        List<Location> result = new ArrayList<>();
        result.add(block.getLocation());

        if(block.getType().equals(Material.AIR)) {
            return result;
        }
        BlockState state = block.getState();
        BlockData data = block.getBlockData();

        if(data instanceof Door) {
            Door door = (Door) data;

            switch (door.getHalf()) {
                case BOTTOM:
                    result.add(block.getRelative(BlockFace.UP).getLocation());
                    break;
                case TOP:
                    result.add(block.getRelative(BlockFace.DOWN).getLocation());
                    break;
            }
        }

        if(block.getType().equals(Material.CHEST)) {
            Chest chest = (Chest) state;
            Inventory inv = chest.getBlockInventory();
            if(inv instanceof DoubleChestInventory) {
                DoubleChestInventory doubleChestInventory = (DoubleChestInventory) inv;

                InventoryHolder left = doubleChestInventory.getLeftSide().getHolder();
                InventoryHolder right = doubleChestInventory.getRightSide().getHolder();

                if(left == null || right == null) {
                    return result;
                }

                if(left.equals(chest)) {
                    result.add(((Chest) right).getLocation());
                } else {
                    result.add(((Chest) left).getLocation());
                }
            }
        }

        return result;
    }

}
