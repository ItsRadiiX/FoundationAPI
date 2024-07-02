package nl.bryansuk.foundationapi.paper.menumanager.animations;

import nl.bryansuk.foundationapi.paper.menumanager.menuitems.MenuItem;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

public class SpiralAnimation extends Animation{

    public SpiralAnimation() {
        super(1, 0);
    }

    public SpiralAnimation(int period, int delay) {
        super(period, delay);
    }

    @Override
    public BukkitRunnable animation(int slots, Inventory inventory, Map<Integer, MenuItem> menuItems) {

        int rows = slots / 9;

        return new BukkitRunnable() {
            int index = 0;
            final int[] spiralOrder = generateSpiralOrder(rows);

            @Override
            public void run() {
                if (index >= spiralOrder.length) {
                    this.cancel();
                    return;
                }

                int slot = spiralOrder[index];
                inventory.setItem(slot, menuItems.get(slot).getItemStack());
                index++;
            }
        };
    }

    private int[] generateSpiralOrder(int rows) {
        int columns = 9;
        int totalSlots = rows * columns;
        int[] spiralOrder = new int[totalSlots];
        boolean[][] visited = new boolean[rows][columns];

        int[] rowDirections = {0, 1, 0, -1}; // Directions for moving through rows
        int[] columnDirections = {1, 0, -1, 0}; // Directions for moving through columns
        int currentRow = 0, currentColumn = 0, directionIndex = 0;

        for (int i = 0; i < totalSlots; i++) {
            spiralOrder[i] = currentRow * columns + currentColumn;
            visited[currentRow][currentColumn] = true;

            int nextRow = currentRow + rowDirections[directionIndex];
            int nextColumn = currentColumn + columnDirections[directionIndex];

            if (nextRow >= 0 && nextRow < rows && nextColumn >= 0 && nextColumn < columns && !visited[nextRow][nextColumn]) {
                currentRow = nextRow;
                currentColumn = nextColumn;
            } else {
                directionIndex = (directionIndex + 1) % 4;
                currentRow += rowDirections[directionIndex];
                currentColumn += columnDirections[directionIndex];
            }
        }

        return spiralOrder;
    }
}
