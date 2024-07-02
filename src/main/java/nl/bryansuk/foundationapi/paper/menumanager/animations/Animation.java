package nl.bryansuk.foundationapi.paper.menumanager.animations;

import nl.bryansuk.foundationapi.paper.menumanager.menuitems.MenuItem;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

public abstract class Animation {

    private final int period;
    private final int delay;

    public abstract BukkitRunnable animation(int slots, Inventory inventory, Map<Integer, MenuItem> menuItems);

    public Animation(int period, int delay) {
        this.period = period;
        this.delay = delay;
    }

    public int getPeriod() {
        return period;
    }

    public int getDelay() {
        return delay;
    }
}
