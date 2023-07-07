package pl.kpgtb.klock.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.bukkit.Location;

@DatabaseTable(tableName = "klock_blocks")
public class LockedBlock {
    @DatabaseField(id = true)
    private Location location;
    @DatabaseField
    private int key;

    public LockedBlock(Location location, int key) {
        this.location = location;
        this.key = key;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }
}
