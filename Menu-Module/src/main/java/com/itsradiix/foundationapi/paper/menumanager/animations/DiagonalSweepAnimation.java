package com.itsradiix.foundationapi.paper.menumanager.animations;

import com.itsradiix.foundationapi.paper.menumanager.menuitems.MenuItem;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

public class DiagonalSweepAnimation extends Animation {

    private final StartingPoint startingPoint;

    public DiagonalSweepAnimation(StartingPoint startingPoint) {
        super(1, 0);
        this.startingPoint = startingPoint;
    }

    public DiagonalSweepAnimation(int period, int delay, StartingPoint startingPoint) {
        super(period, delay);
        this.startingPoint = startingPoint;
    }

    @Override
    public BukkitRunnable animation(int slots, Inventory inventory, Map<Integer, MenuItem> menuItems) {
        int rows = slots / 9;

        return new BukkitRunnable() {
            int index = 0;
            final int[] diagonalOrder = generateDiagonalOrder(rows, startingPoint);

            @Override
            public void run() {
                if (index >= diagonalOrder.length) {
                    this.cancel();
                    return;
                }

                int slot = diagonalOrder[index];
                inventory.setItem(slot, menuItems.get(slot).getItemStack());
                index++;
            }
        };
    }

    private int[] generateDiagonalOrder(int rows, StartingPoint startingPoint) {
        int columns = 9;
        int totalSlots = rows * columns;
        int[] order = new int[totalSlots];
        int index = 0;

        for (int sum = 0; sum < rows + columns - 1; sum++) {
            for (int i = 0; i <= sum; i++) {
                int row, col;
                col = switch (startingPoint) {
                    case TOP_LEFT -> {
                        row = i;
                        yield sum - i;
                    }
                    case TOP_RIGHT -> {
                        row = i;
                        yield columns - 1 - (sum - i);
                    }
                    case BOTTOM_LEFT -> {
                        row = rows - 1 - i;
                        yield sum - i;
                    }
                    case BOTTOM_RIGHT -> {
                        row = rows - 1 - i;
                        yield columns - 1 - (sum - i);
                    }
                    default -> throw new IllegalArgumentException("Invalid starting point");
                };

                if (row >= 0 && row < rows && col >= 0 && col < columns) {
                    order[index++] = row * columns + col;
                }
            }
        }

        return order;
    }

    public enum StartingPoint {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT
    }

}
