package com.itsradiix.foundationapi.paper.menumanager.animations;

import com.itsradiix.foundationapi.paper.menumanager.menuitems.MenuItem;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

public class ZigZagAnimation extends Animation{
    public ZigZagAnimation() {
        super(1,0);
    }

    public ZigZagAnimation(int period, int delay) {
        super(period, delay);
    }

    @Override
    public BukkitRunnable animation(int slots, Inventory inventory, Map<Integer, MenuItem> menuItems) {
        int rows = slots / 9;

        return new BukkitRunnable() {
            int index = 0;
            final int[] zigzagOrder = generateZigzagOrder(rows);

            @Override
            public void run() {
                if (index >= zigzagOrder.length) {
                    this.cancel();
                    return;
                }

                int slot = zigzagOrder[index];
                inventory.setItem(slot, menuItems.get(slot).getItemStack());
                index++;
            }
        };
    }

    private int[] generateZigzagOrder(int rows) {
        int columns = 9;
        int totalSlots = rows * columns;
        int[] order = new int[totalSlots];
        int index = 0;

        for (int row = 0; row < rows; row++) {
            if (row % 2 == 0) {
                // Left to right
                for (int col = 0; col < columns; col++) {
                    order[index++] = row * columns + col;
                }
            } else {
                // Right to left
                for (int col = columns - 1; col >= 0; col--) {
                    order[index++] = row * columns + col;
                }
            }
        }

        return order;
    }
}
