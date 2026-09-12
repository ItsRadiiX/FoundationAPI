package net.astralounge.foundationapi.paper.menumanager.v1.revealpatterns;

import net.astralounge.foundationapi.paper.menumanager.v1.BaseMenu;
import net.astralounge.foundationapi.paper.menumanager.v1.menuitems.MenuItem;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class MenuReveal {

    private static final Map<Class<? extends RevealPattern>, Map<CacheKey, int[]>> CACHE = new HashMap<>();

    private final RevealPattern pattern;
    private final PatternOptions options;
    private final int period;
    private final int delay;

    // Existing constructor, now delegates to the new one with empty options
    public MenuReveal(RevealPattern pattern, int period, int delay) {
        this(pattern, PatternOptions.empty(), period, delay);
    }

    // New constructor that allows passing options (e.g. per-player preferences)
    public MenuReveal(RevealPattern pattern, PatternOptions options, int period, int delay) {
        this.pattern = pattern;
        this.options = options;
        this.period = period;
        this.delay = delay;
    }

    public BukkitRunnable play(BaseMenu menu, int startSlot, RevealArea area) {
        final int areaRows = area.rows();
        final int areaCols = area.cols();

        final int startRow = startSlot / 9;
        final int startCol = startSlot % 9;

        final int inventorySize = menu.getInventory().getSize();

        int[] order = getCachedOrder(areaRows, areaCols);

        return new BukkitRunnable() {
            int index = 0;

            @Override
            public void run() {
                if (index >= order.length) {
                    cancel();
                    return;
                }

                int local = order[index++];
                int row = local / areaCols;
                int col = local % areaCols;

                int slot = (startRow + row) * 9 + (startCol + col);

                if (slot < 0 || slot >= inventorySize) {
                    return; // skip out-of-bounds slot safely
                }

                MenuItem item = menu.items().get(slot);
                if (item != null) {
                    menu.getInventory().setItem(slot, item.getItemStack());
                }
            }
        };
    }

    private int[] getCachedOrder(int rows, int cols) {
        CacheKey key = new CacheKey(rows, cols, options);

        return CACHE
                .computeIfAbsent(pattern.getClass(), k -> new HashMap<>())
                .computeIfAbsent(key, k -> pattern.generate(rows, cols, options));
    }

    public int period() {
        return period;
    }

    public int delay() {
        return delay;
    }

    /**
     * Cache key: grid size + options.
     */
    private record CacheKey(int rows, int cols, PatternOptions options) {

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CacheKey(int rows1, int cols1, PatternOptions options1))) return false;
            return rows == rows1 &&
                    cols == cols1 &&
                    Objects.equals(options, options1);
        }

    }
}
