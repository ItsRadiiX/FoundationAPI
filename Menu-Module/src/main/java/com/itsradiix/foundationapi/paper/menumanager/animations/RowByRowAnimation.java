package com.itsradiix.foundationapi.paper.menumanager.animations;

import com.itsradiix.foundationapi.paper.menumanager.menuitems.MenuItem;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

public class RowByRowAnimation extends Animation {

    public RowByRowAnimation() {
        super(4,0);
    }

    public RowByRowAnimation(int period, int delay) {
        super(period, delay);
    }

    @Override
    public BukkitRunnable animation(int slots, Inventory inventory, Map<Integer, MenuItem> menuItems) {
        int rows = slots / 9;

        return new BukkitRunnable() {

            int row = 0;

            @Override
            public void run() {
                if (row >= rows) {
                    this.cancel();
                    return;
                }

                for (int col = 0; col < 9; col++) {
                    int slot = row * 9 + col;
                    inventory.setItem(slot, menuItems.get(slot).getItemStack());
                }
                row++;
            }
        };
    }
}
