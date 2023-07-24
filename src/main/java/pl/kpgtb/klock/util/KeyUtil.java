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
        int nextKey = wrapper.getCacheManager().getServerDataOr(wrapper.getTag(), "next_key", -1) + 1;
        wrapper.getCacheManager().setServerData(wrapper.getTag(), "next_key", nextKey);
        return nextKey;
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
            Inventory inv = chest.getInventory();
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
