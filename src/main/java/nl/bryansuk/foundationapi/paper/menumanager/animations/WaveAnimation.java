package nl.bryansuk.foundationapi.paper.menumanager.animations;

import nl.bryansuk.foundationapi.paper.menumanager.menuitems.MenuItem;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

public class WaveAnimation extends Animation{

    public WaveAnimation() {
        super(1,0);
    }

    public WaveAnimation(int period, int delay) {
        super(period, delay);
    }

    @Override
    public BukkitRunnable animation(int slots, Inventory inventory, Map<Integer, MenuItem> menuItems) {
        int rows = slots / 9;

        return new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                if (tick >= 9 + rows) { // 9 columns plus the number of rows for the wave to cover the entire grid
                    this.cancel();
                    return;
                }

                for (int row = 0; row < rows; row++) {
                    int col = tick - row;
                    if (col >= 0 && col < 9) {
                        int slot = row * 9 + col;
                        inventory.setItem(slot, menuItems.get(slot).getItemStack());
                    }
                }
                tick++;
            }
        };
    }
}
