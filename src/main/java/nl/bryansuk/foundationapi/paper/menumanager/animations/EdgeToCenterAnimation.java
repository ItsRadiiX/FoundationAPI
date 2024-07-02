package nl.bryansuk.foundationapi.paper.menumanager.animations;

import nl.bryansuk.foundationapi.paper.menumanager.menuitems.MenuItem;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

public class EdgeToCenterAnimation extends Animation{

    public EdgeToCenterAnimation() {
        super(1, 0);
    }

    public EdgeToCenterAnimation(int period, int delay) {
        super(period, delay);
    }

    @Override
    public BukkitRunnable animation(int slots, Inventory inventory, Map<Integer, MenuItem> menuItems) {

        int rows = slots / 9;

        return new BukkitRunnable() {
            int index = 0;
            final int[] edgeToCenterOrder = generateEdgeToCenterOrder(rows);

            @Override
            public void run() {
                if (index >= edgeToCenterOrder.length) {
                    this.cancel();
                    return;
                }

                int slot = edgeToCenterOrder[index];
                inventory.setItem(slot, menuItems.get(slot).getItemStack());
                index++;
            }
        };
    }

    private int[] generateEdgeToCenterOrder(int rows) {
        int columns = 9;
        int totalSlots = rows * columns;
        int[] order = new int[totalSlots];
        boolean[][] visited = new boolean[rows][columns];

        int index = 0;
        int left = 0, right = columns - 1, top = 0, bottom = rows - 1;

        while (left <= right && top <= bottom) {
            // Top row
            for (int col = left; col <= right; col++) {
                if (!visited[top][col]) {
                    order[index++] = top * columns + col;
                    visited[top][col] = true;
                }
            }
            top++;

            // Right column
            for (int row = top; row <= bottom; row++) {
                if (!visited[row][right]) {
                    order[index++] = row * columns + right;
                    visited[row][right] = true;
                }
            }
            right--;

            // Bottom row
            for (int col = right; col >= left; col--) {
                if (!visited[bottom][col]) {
                    order[index++] = bottom * columns + col;
                    visited[bottom][col] = true;
                }
            }
            bottom--;

            // Left column
            for (int row = bottom; row >= top; row--) {
                if (!visited[row][left]) {
                    order[index++] = row * columns + left;
                    visited[row][left] = true;
                }
            }
            left++;
        }

        return order;
    }
}
