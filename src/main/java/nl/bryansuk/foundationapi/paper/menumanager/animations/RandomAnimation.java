package nl.bryansuk.foundationapi.paper.menumanager.animations;

import nl.bryansuk.foundationapi.paper.menumanager.menuitems.MenuItem;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RandomAnimation extends Animation {

    public RandomAnimation() {
        super(1,0);
    }

    public RandomAnimation(int period, int delay) {
        super(period, delay);
    }

    @Override
    public BukkitRunnable animation(int slots, Inventory inventory, Map<Integer, MenuItem> menuItems) {

        return new BukkitRunnable() {
            int index = 0;
            final List<Integer> randomOrder = generateRandomOrder(slots);

            @Override
            public void run() {
                if (index >= randomOrder.size()) {
                    this.cancel();
                    return;
                }

                int slot = randomOrder.get(index);
                inventory.setItem(slot, menuItems.get(slot).getItemStack());
                index++;
            }
        };
    }

    private List<Integer> generateRandomOrder(int totalSlots) {
        List<Integer> order = new ArrayList<>(totalSlots);

        for (int i = 0; i < totalSlots; i++) {
            order.add(i);
        }

        Collections.shuffle(order);
        return order;
    }
}
