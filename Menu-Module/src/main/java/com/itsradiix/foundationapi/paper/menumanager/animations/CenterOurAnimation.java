package com.itsradiix.foundationapi.paper.menumanager.animations;

import com.itsradiix.foundationapi.paper.menumanager.menuitems.MenuItem;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

public class CenterOurAnimation extends Animation{

    public CenterOurAnimation() {
        super(1,0);
    }

    public CenterOurAnimation(int period, int delay) {
        super(period, delay);
    }

    @Override
    public BukkitRunnable animation(int slots, Inventory inventory, Map<Integer, MenuItem> menuItems) {
        int rows = slots/9;

        return new BukkitRunnable() {
            int tick = 0;
            final int[] centerOutOrder = generateCenterOutOrder(rows);

            @Override
            public void run() {
                if (tick >= centerOutOrder.length) {
                    this.cancel();
                    return;
                }

                int slot = centerOutOrder[tick];
                inventory.setItem(slot, menuItems.get(slot).getItemStack());
                tick++;
            }
        };
    }

    private int[] generateCenterOutOrder(int rows) {
        int columns = 9;
        int totalSlots = rows * columns;
        int[] order = new int[totalSlots];

        int minRow = 0, maxRow = rows - 1;
        int minCol = 0, maxCol = columns - 1;

        int index = 0;

        while (minRow <= maxRow && minCol <= maxCol) {
            // Traverse from left to right along the top row
            for (int col = minCol; col <= maxCol; col++) {
                order[index++] = minRow * columns + col;
            }
            minRow++;

            // Traverse from top to bottom along the right column
            for (int row = minRow; row <= maxRow; row++) {
                order[index++] = row * columns + maxCol;
            }
            maxCol--;

            if (minRow <= maxRow) {
                // Traverse from right to left along the bottom row
                for (int col = maxCol; col >= minCol; col--) {
                    order[index++] = maxRow * columns + col;
                }
                maxRow--;
            }

            if (minCol <= maxCol) {
                // Traverse from bottom to top along the left column
                for (int row = maxRow; row >= minRow; row--) {
                    order[index++] = row * columns + minCol;
                }
                minCol++;
            }
        }

        return order;
    }
}
