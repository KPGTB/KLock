package pl.kpgtb.klock.gui;

import com.github.kpgtb.ktools.manager.gui.KGui;
import com.github.kpgtb.ktools.manager.gui.container.GuiContainer;
import com.github.kpgtb.ktools.manager.gui.item.GuiItem;
import com.github.kpgtb.ktools.manager.gui.item.common.CloseItem;
import com.github.kpgtb.ktools.manager.language.LanguageLevel;
import com.github.kpgtb.ktools.util.item.ItemBuilder;
import com.github.kpgtb.ktools.util.ui.FontWidth;
import com.github.kpgtb.ktools.util.wrapper.ToolsObjectWrapper;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Random;

public class LockPickGUI extends KGui {
    private final ToolsObjectWrapper wrapper;
    private final IResponse response;

    private int action;
    private int y;
    private int expectedY;
    private boolean used;

    private final BukkitTask gameRunnable;
    private boolean responded;

    public LockPickGUI(ToolsObjectWrapper wrapper, IResponse response) {
        super(
            FontWidth.getSpaces(-8) + ChatColor.WHITE + "\uE128" + FontWidth.getSpaces(-176 + 8) + ChatColor.RESET +
                wrapper.getLanguageManager().getSingleString(LanguageLevel.PLUGIN, "lockPickGUI"),
            6,
            wrapper
        );
        this.wrapper = wrapper;
        this.response = response;
        this.responded = false;

        this.used = false;
        this.action = 0;
        this.y = 0;
        this.expectedY = new Random().nextInt(5) + 1;
        this.gameRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                if(used) {
                    used = false;
                    return;
                }
                y = Math.max(0, y-1);
                prepareGui();
            }
        }.runTaskTimer(wrapper.getPlugin(),0,wrapper.getConfig().getInt("lockPickDownTime"));

        blockClick();
        setCloseAction(e -> {
            this.gameRunnable.cancel();
            if(!responded) {
                response.response(false);
            }
        });
    }

    @Override
    public void prepareGui() {
        resetContainers();

        GuiContainer manageContainer = new GuiContainer(this, 5,0,3,6);


        GuiItem info = new GuiItem(new ItemBuilder(Material.IRON_NUGGET)
                .displayname(wrapper.getLanguageManager().getSingleString(LanguageLevel.PLUGIN, "infoGuiName"))
                .lore(wrapper.getLanguageManager().getString(LanguageLevel.PLUGIN, "infoGuiLore"))
                .model(1009));
        manageContainer.setItem(0,0,info);

        GuiItem lockPick = new GuiItem(new ItemBuilder(Material.IRON_NUGGET)
                .displayname(wrapper.getLanguageManager().getSingleString(LanguageLevel.PLUGIN, "lockPickGuiItemName"))
                .model(1004));
        lockPick.setClickAction((e,loc) -> {
            used = true;
            this.y = Math.min(5,this.y+1);
            prepareGui();
        });
        manageContainer.setItem(1,0,lockPick);

        GuiItem unlock = new GuiItem(new ItemBuilder(Material.IRON_NUGGET)
                .displayname(wrapper.getLanguageManager().getSingleString(LanguageLevel.PLUGIN, "unlockGuiItemName"))
                .model(1005));
        unlock.setClickAction((e,loc) -> {
            if (this.y==this.expectedY) {
                if(action >= 4) {
                    this.responded = true;
                    this.response.response(true);
                    e.getWhoClicked().closeInventory();
                    return;
                }
                this.action++;
            } else {
                if(action <= 0) {
                    this.responded = true;
                    this.response.response(false);
                    e.getWhoClicked().closeInventory();
                    return;
                }
                this.action--;
            }

            this.y = 0;
            this.expectedY = new Random().nextInt(5) + 1;

            prepareGui();
        });
        manageContainer.setItem(2,0,unlock);

        manageContainer.setItem(2,5, CloseItem.get(wrapper));

        addContainer(manageContainer);

        GuiContainer gameContainer = new GuiContainer(this,0,0,5,6);

        for (int x = 0; x < 5; x++) {
            if(this.action > x) {
                GuiItem up = new GuiItem(new ItemBuilder(Material.IRON_NUGGET, " ").model(1002));
                gameContainer.setItem(x, 2, up);

                GuiItem down = new GuiItem(new ItemBuilder(Material.IRON_NUGGET, " ").model(1001));
                gameContainer.setItem(x, 3, down);
                continue;
            }

            GuiItem point = new GuiItem(new ItemBuilder(Material.IRON_NUGGET, " ").model(this.y == this.expectedY && this.action == x ? 1003 : 1000));
            gameContainer.setItem(x, this.action == x ? 5-this.y : 5, point);
        }

        addContainer(gameContainer);
    }

    public interface IResponse {
        void response(boolean unlocked);
    }
}
