package pl.kpgtb.klock.command;

import com.github.kpgtb.ktools.manager.command.KCommand;
import com.github.kpgtb.ktools.manager.command.annotation.Description;
import com.github.kpgtb.ktools.manager.command.annotation.MainCommand;
import com.github.kpgtb.ktools.manager.command.annotation.WithoutPermission;
import com.github.kpgtb.ktools.manager.language.LanguageLevel;
import com.github.kpgtb.ktools.util.item.ItemUtil;
import com.github.kpgtb.ktools.util.wrapper.ToolsObjectWrapper;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.kpgtb.klock.data.LockedBlock;
import pl.kpgtb.klock.item.EmptyKeyItem;

import java.sql.SQLException;

@Description("Reset key")
@WithoutPermission
public class ResetKeyCommand extends KCommand {
    private final ToolsObjectWrapper wrapper;

    public ResetKeyCommand(ToolsObjectWrapper wrapper, String groupPath) {
        super(wrapper, groupPath);
        this.wrapper = wrapper;
    }

    @MainCommand
    @Description("Reset key from your main hand")
    public void reset(Player player) throws SQLException {
        ItemStack is = player.getInventory().getItemInMainHand();
        Audience audience = wrapper.getAdventure().player(player);

        if(is == null || is.getType().equals(Material.AIR)||
        !wrapper.getItemManager().getCustomItems().get(wrapper.getTag().toLowerCase() + ":key").isSimilar(is)) {
            wrapper.getLanguageManager().getComponent(LanguageLevel.PLUGIN, "notKey")
                    .forEach(audience::sendMessage);
            return;
        }

        if(wrapper.getDataManager().getDao(LockedBlock.class, Location.class)
                .queryForEq("key", wrapper.getCacheManager().getDataOr(is, wrapper.getTag(), "key", -1))
                .size() > 0) {
            wrapper.getLanguageManager().getComponent(LanguageLevel.PLUGIN, "usedKey")
                    .forEach(audience::sendMessage);
            return;
        }

        int amount = is.getAmount();
        is.setAmount(0);
        ItemStack resetKey = wrapper.getItemManager().getCustomItem(wrapper.getTag(), EmptyKeyItem.class);
        resetKey.setAmount(amount);
        ItemUtil.giveItemToPlayer(player, resetKey);
        wrapper.getLanguageManager().getComponent(LanguageLevel.PLUGIN, "exchanged")
                .forEach(audience::sendMessage);
    }
}
